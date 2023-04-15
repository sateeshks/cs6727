package com.kailas.dpm.util;

import com.yubico.webauthn.data.ByteArray;
import io.micrometer.common.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class Utils {
    private static final SecureRandom random = new SecureRandom();

    public static ByteArray generateRandom(int length) {
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return new ByteArray(bytes);
    }

    public static String  getHash(String input){
        byte[] hash =null;
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            digest.update(input.getBytes("UTF-8"));
            hash = digest.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return Base64.getEncoder().encodeToString(hash);

    }

    public static Boolean match(String one ,String two){
        if(StringUtils.isNotBlank(one) && StringUtils.isNotBlank(two)){
            if(one.equals(two))
                return true;
        }
    return false;
    }
}
