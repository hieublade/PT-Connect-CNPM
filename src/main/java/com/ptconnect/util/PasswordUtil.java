package com.ptconnect.util;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordUtil {
    private PasswordUtil() {}

    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));
    }

    public static boolean verify(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null || storedHash.isBlank()) {
            return false;
        }
        if (storedHash.startsWith("$2a$") || storedHash.startsWith("$2b$") || storedHash.startsWith("$2y$")) {
            String normalized = storedHash.startsWith("$2a$")
                    ? storedHash
                    : "$2a$" + storedHash.substring(4);
            try {
                return BCrypt.checkpw(plainPassword, normalized);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return plainPassword.equals(storedHash);
    }
}
