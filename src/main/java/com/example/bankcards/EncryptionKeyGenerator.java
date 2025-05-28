package com.example.bankcards;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class EncryptionKeyGenerator {
    public static void main(String[] args) {
        try {
            // Генерируем ключ для AES (16 байт = 128 бит)
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128); // Можно поставить 256 для AES-256
            SecretKey secretKey = keyGen.generateKey();
            String base64Key = Base64.getEncoder().encodeToString(secretKey.getEncoded());
            System.out.println("Base64 Encryption Key: " + base64Key);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error generating key: " + e.getMessage());
        }
    }
}
