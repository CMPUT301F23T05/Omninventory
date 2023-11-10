package com.example.omninventory;

import java.security.MessageDigest;
import java.util.regex.Pattern;

/**
 * A class containing utilities methods including hashing passwords and validating password
 *
 * @author Nhung Nguyen
 */

public class Utils {
    /**
     * Hash password using sha-256 algorithm
     */
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

    /**
     * Check if password meets the requirements
     */
    public static boolean validatePassword(String password) {
        final String HAS_NUMBER = ".*[0-9].*";
        final String HAS_UPPERCASE = ".*[A-Z].*";
        final String HAS_LOWERCASE = ".*[a-z].*";
        final String HAS_SYMBOL = ".*[^a-zA-Z0-9].*";

        if (password.length() < 8 || password.length() > 20) {
            return false;
        }
        // Check for at least one number.
        else if (!Pattern.matches(HAS_NUMBER, password)) {
            return false;
        }
        // Check for at least one letter.
        else if (!Pattern.matches(HAS_LOWERCASE, password)) {
            return false;
        }
        // Check for at least one uppercase letter.
        else if (!Pattern.matches(HAS_UPPERCASE, password)) {
            return false;
        }
        // Check for at least one special character.
        else if (!Pattern.matches(HAS_SYMBOL, password)) {
            return false;
        }
        return true;
    }
}
