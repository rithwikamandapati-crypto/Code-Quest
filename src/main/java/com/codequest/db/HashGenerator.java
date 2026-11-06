package com.codequest.db;

import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class HashGenerator {

    private static final int ITERATIONS = 210000;
    private static final int KEY_LENGTH = 256;

    public static void main(String[] args) throws Exception {

        // CHANGE THIS to the admin password you want
        String password = "Admin@12345";

        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);

        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);

        SecretKeyFactory factory =
                SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        byte[] hash = factory.generateSecret(spec).getEncoded();

        String result = ITERATIONS + ":"
                + Base64.getEncoder().encodeToString(salt) + ":"
                + Base64.getEncoder().encodeToString(hash);

        System.out.println("Copy this line into MySQL:");
        System.out.println(result);
    }
}