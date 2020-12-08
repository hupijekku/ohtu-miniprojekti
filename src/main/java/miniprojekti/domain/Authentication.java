package miniprojekti.domain;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Authentication {
    
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 128;
    
    public static String getSalt(int length) {
        String salt = "";
        for (int i = 0; i < length; i++) {
            salt += ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length()));
        }
        return salt;
    }
    
    public static byte[] hash(char[] password, byte[] salt) {
        PBEKeySpec keySpec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        Arrays.fill(password, Character.MIN_VALUE);
        try {
            SecretKeyFactory keyFac = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            return keyFac.generateSecret(keySpec).getEncoded();
        } catch (Exception e) {
            throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
        } finally {
            keySpec.clearPassword();
        }
    }
    
    public static String generatePassword(String password, String salt) {
        String generatedPassword = "";
        byte[] secure = hash(password.toCharArray(), salt.getBytes());
        generatedPassword = Base64.getEncoder().encodeToString(secure);
        return generatedPassword;
    }
    
    public static boolean verifyUser(String password, String salt, String hash) {
        String newHash = generatePassword(password, salt);
        
        return newHash.equalsIgnoreCase(hash);
    }
}
