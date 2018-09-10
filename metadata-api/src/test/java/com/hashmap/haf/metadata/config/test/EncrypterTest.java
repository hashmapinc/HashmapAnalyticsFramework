package com.hashmap.haf.metadata.config.test;

import com.hashmap.haf.metadata.config.utils.Encrypter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class EncrypterTest {

    @Autowired
    private Encrypter encrypter;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Value("${password.encrypt-salt}")
    private String saltStr;

    @Test
    public void encryptDecrypt() {
        String encryptStr = "TestString";
        String receivedEncrypted = encrypter.encrypt(encryptStr);
        Assert.assertEquals("TestString", encrypter.decrypt(receivedEncrypted));
    }
}
