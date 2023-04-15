package org.kailas;

import org.kailas.dto.PasswordProfile;
import org.kailas.dto.User;
import org.kailas.service.PasswordChecker;
import org.kailas.service.PasswordGenerator;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        System.out.println("Hello world!");
        Main m = new Main();


        PasswordGenerator pg = new PasswordGenerator();
        PasswordProfile profile = new PasswordProfile();
        User user = new User();
        user.setName("sateesh");
        user.setEmail("sateesh_ks@gmail.com");
        profile.setDomain("yahoo.com");
        profile.setRevision(1);
        profile.setDigits(true);
        profile.setLength(10);
        profile.setSymbols(true);
        profile.setLowerCase(true);
        profile.setUpperCase(true);
        profile.setExlude("aA@`'");
        user.setProfile(profile);
        String pwd1=null;
        String pwd2=null;
        try {
            pwd1 = PasswordGenerator.generate_password(user);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (PasswordGenerator.ExcludeAllCharsAvailable e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println(" this is generated password :"+pwd1);
        System.out.println("IS password strength valid "+PasswordChecker.isValid(pwd1));
    }
}