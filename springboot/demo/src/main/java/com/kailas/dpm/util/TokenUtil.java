package com.kailas.dpm.util;


import com.kailas.dpm.entities.AppUser;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Date;


@Component
public class TokenUtil {

    private static byte[] secret;
    private static Long tokenValidityInMins;
    private static JWSSigner signer;

    public TokenUtil(@Value("jwt.hash.secret") String secret, @Value("#{new Long(${jwt.validity.mins:15})}")Long validityInMins)
            throws KeyLengthException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(secret.getBytes(StandardCharsets.UTF_8));
        this.secret =md.digest();
        this.tokenValidityInMins = validityInMins;
        this.signer = new MACSigner(this.secret);
    }

    public String generateToken(AppUser userDetails) throws JOSEException {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(userDetails.getName())
                .issuer("https://dpm.com")
                .expirationTime(new Date(new Date().getTime() + (tokenValidityInMins * 60 * 1000)))
                .build();
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
        signedJWT.sign(signer);
        String token = signedJWT.serialize();
        return token;
    }

    public static String parseToken(String token) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifier(secret);

        if(!signedJWT.verify(verifier)) {
            throw new SecurityException("Token may have tampered");
        }

        if(new Date().after(signedJWT.getJWTClaimsSet().getExpirationTime())) {
            throw new SecurityException("Token is expired");
        }

        return signedJWT.getJWTClaimsSet().getSubject();
    }


}