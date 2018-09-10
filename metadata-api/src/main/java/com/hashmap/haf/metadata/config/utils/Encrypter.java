package com.hashmap.haf.metadata.config.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class Encrypter {

    @Autowired
    BCryptPasswordEncoder encoder;

    @Value("${password.encrypt-salt}")
    private String saltStr;

    public String encrypt(String encryptStr) {
        String encryptSalt = encoder.encode(saltStr);
        String salt = encryptSalt + encryptStr;
        return Base64.getEncoder().encodeToString(salt.getBytes());
    }

    public String decrypt(String decryptStr) {
        String encryptSalt = encoder.encode(saltStr);
        String decodedPassword = new String(Base64.getDecoder().decode(decryptStr));
        return decodedPassword.substring(encryptSalt.length());
    }
}
