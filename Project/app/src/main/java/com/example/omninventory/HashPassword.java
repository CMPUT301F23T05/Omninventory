package com.example.omninventory;

import java.security.MessageDigest;

// Reference: https://www.baeldung.com/sha-256-hashing-java
public class HashPassword {
    public static String sha256(String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            StringBuffer hexString = new StringBuffer();
            byte[] hash = digest.digest(base.getBytes("UTF-8"));

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch(Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
