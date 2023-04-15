package com.kailas.dpm.service;


import javax.crypto.Mac;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.regex.Pattern;

import com.kailas.dpm.domain.User;
import com.kailas.dpm.domain.PasswordProfile;
import com.kailas.dpm.entities.AppUser;
import com.kailas.dpm.entities.UserProfile;

public class PasswordGenerator extends Dictionary {


    public static class ExcludeAllCharsAvailable extends Exception {};


    private static String getMasterKey(String userName,String userSecrete){
        String masterPassword = userSecrete;
        String name = userName;
        int N = 32768;
        int r = 8;
        int p = 2;
        int dkLen = 64;
        String seed = "scope2" + Integer.toString(name.length()) + name;
        String encodedMasterKey="";
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            PBEKeySpec spec = new PBEKeySpec(masterPassword.toCharArray(), seed.getBytes(), N, dkLen * 8);
            byte[] masterKey = factory.generateSecret(spec).getEncoded();
            encodedMasterKey = Base64.getEncoder().encodeToString(masterKey);
            System.out.println("Encoded master key: " + encodedMasterKey);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return encodedMasterKey;
    }

    /**
     * siteKey = HMAC-SHA-25612( key, seed )
     * key = <master key>
     * seed = scope3 . LEN(<site name>) . <site name> . <counter>
     * @param masterKey
     * @param domainName
     * @param counter
     * @return
     */
    private static String getSiteKey(String masterKey,String domainName,int counter){
        byte[] masterKeyBytes = Base64.getDecoder().decode(masterKey);
        String siteName = domainName;
        String seed = "scope3" + Integer.toString(siteName.length()) + siteName + Integer.toString(counter);
        String encodedSiteKey="";
        try {
            SecretKeySpec secretKey = new SecretKeySpec(masterKeyBytes, "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKey);
            byte[] siteKey = mac.doFinal(seed.getBytes());
            encodedSiteKey = Base64.getEncoder().encodeToString(siteKey);
            System.out.println("Encoded site key: " + encodedSiteKey);

        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return  encodedSiteKey;
    }

    private static BigInteger calcEntropy(AppUser user,UserProfile profile) throws NoSuchAlgorithmException, InvalidKeyException {
        //user username and email combo here
        String masterKey =getMasterKey(user.getEmail(),user.getName());
        //user master key as part seed for domain key
        String domainKey = getSiteKey(masterKey,profile.getDomain(),profile.getRevision());
        byte[] masterPasswordBytes = domainKey.getBytes(StandardCharsets.UTF_8);
        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(masterPasswordBytes, "HmacSHA256");
        sha256Hmac.init(secretKey);
        byte[] hexEntropy = sha256Hmac.doFinal(masterKey.getBytes());
        BigInteger entr = new BigInteger(1, hexEntropy);
        System.out.println("user for given domain entropy= "+entr);
        return entr;
    }


    private static String removeExcludedChars(String string, String exclude)
            throws ExcludeAllCharsAvailable {
        StringBuilder newString = new StringBuilder();
        for (char c : string.toCharArray()) {
            if (exclude.indexOf(c) == -1) {
                newString.append(c);
            }
        }
        if (newString.length() == 0) {
            throw new ExcludeAllCharsAvailable();
        }
        return newString.toString();
    }

    private static String getSetOfCharacters(List<String> rules, String exclude)
            throws ExcludeAllCharsAvailable {
        if (rules == null) {
            return Dictionary.rules.get("lowercase")
                    + Dictionary.rules.get("uppercase")
                    + Dictionary.rules.get("digits")
                    + Dictionary.rules.get("symbols");
        }
        StringBuilder poolOfChars = new StringBuilder();
        for (String rule : rules) {
            poolOfChars.append(Dictionary.rules.get(rule));
        }
        String str =poolOfChars.toString();
        for(Character ch :exclude.toCharArray())
            str= str.replaceAll(Pattern.quote(String.valueOf(ch)), "");
        return str;
    }

    private static Object[] consumeEntropy(String generatedPassword, BigInteger quotient, String setOfCharacters, int maxLength) {
        if (generatedPassword.length() >= maxLength) {
            return new Object[] {generatedPassword, quotient};
        }
        int setOfCharactersLen = setOfCharacters.length();
        quotient = quotient.divide( BigInteger.valueOf(setOfCharactersLen));
        BigInteger remainder = quotient.remainder(BigInteger.valueOf(setOfCharactersLen));// % setOfCharactersLen;
        generatedPassword += setOfCharacters.charAt(remainder.intValue());
        return consumeEntropy(generatedPassword, quotient, setOfCharacters, maxLength);
    }

    /**
     * Replaces the genrated password char with guaranteed rule based char
     * @param generatedPassword
     * @param entropy
     * @param guaranteedChars
     * @return
     */
    private static String insertStringPseudoRandomly(String generatedPassword, BigInteger entropy, String guaranteedChars) {
        List<BigInteger> reminders = new ArrayList<BigInteger>();
        for (int i = 0; i < guaranteedChars.length(); i++) {
            int generatedPasswordLen = generatedPassword.length();
            BigInteger quotient = entropy.divide( BigInteger.valueOf(generatedPasswordLen));
            BigInteger remainder = quotient.remainder(BigInteger.valueOf(generatedPasswordLen));
            //try not replace allready replaced
            while(reminders.contains(remainder)) {
                entropy = quotient;
                quotient = entropy.divide( BigInteger.valueOf(generatedPasswordLen));
                remainder = quotient.remainder(BigInteger.valueOf(generatedPasswordLen));
            }
            reminders.add(remainder);
            generatedPassword = replaceChar (generatedPassword ,remainder.intValue(),guaranteedChars.charAt(i)) ;//generatedPassword.substring(0, remainder.intValue()) + string.charAt(i) + generatedPassword.substring(remainder.intValue());
            entropy = quotient;


        }
        //Final password
        return generatedPassword;
    }

    private static String replaceChar(String input,int index,char replacment){
        StringBuilder sbuilder = new StringBuilder(input);
        sbuilder.setCharAt(index,replacment);
        return sbuilder.toString();
    }

    /**
     * finds the guaranteed one char per rule  from list of available chars using entropy
     * @param entropy
     * @param rules
     * @param exclude
     * @return
     * @throws ExcludeAllCharsAvailable
     */
    private static Object[] getOneCharPerRule(BigInteger entropy, List<String> rules, String exclude) throws ExcludeAllCharsAvailable {
        String oneCharPerRules = "";
        for (String rule : rules) {
            String availableChars = removeExcludedChars(Dictionary.rules.get(rule), exclude);
            Object[] valueAndEntropy = consumeEntropy("", entropy, availableChars, 1);
            oneCharPerRules += (String)valueAndEntropy[0];
            entropy =(BigInteger) valueAndEntropy[1];
        }
        return new Object[] {oneCharPerRules,entropy};
    }

    //TODO this will be comming from UI input
    private static List<String> getConfiguredRules(UserProfile passwordProfile) {
        String[] rules = new String[] {"lowercase", "uppercase", "digits", "symbols"};
        List<String> configuredRules = new ArrayList<>();
        for (String rule : rules) {
            if (passwordProfile.getRules().contains(rule) ){//&& (Integer)passwordProfile.get(rule) != 0) {
                configuredRules.add(rule);
            }
        }
        return configuredRules;
    }
    private static String renderPassword(BigInteger entropy, UserProfile passwordProfile) throws ExcludeAllCharsAvailable {
        List<String> rules = getConfiguredRules(passwordProfile);
        String excludedChars = passwordProfile.getExclude() ; //optional string to exclude
        String setOfCharacters = getSetOfCharacters(rules, excludedChars);
        String password = "";
        BigInteger passwordEntropy = entropy;
        Object[] firstCut = consumeEntropy(password, passwordEntropy, setOfCharacters, passwordProfile.getLength() );//- rules.size());
        password = (String)firstCut[0];
        passwordEntropy = (BigInteger)firstCut[1];
        Object[] secondCut = getOneCharPerRule(passwordEntropy, rules, excludedChars);
        String charactersToAdd = (String)secondCut[0];
        BigInteger characterEntropy = ((BigInteger)secondCut[1]);
        return insertStringPseudoRandomly(password, characterEntropy, charactersToAdd);
    }

    public static String generate_password(UserProfile profile ) throws NoSuchAlgorithmException, ExcludeAllCharsAvailable, InvalidKeyException {
        BigInteger entropy = calcEntropy(profile.getUser(),profile);
        return renderPassword(entropy, profile);
    }

}