package com.hashmapinc.haf.dao;

import com.datastax.driver.core.utils.UUIDs;
import com.hashmapinc.haf.entity.UserCredentialsEntity;
import com.hashmapinc.haf.models.UserCredentials;
import com.hashmapinc.haf.repository.UserCredentialsRepository;
import com.hashmapinc.haf.utils.UUIDConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Component
public class UserCredentialsDaoImpl implements UserCredentialsDao{

    @Autowired
    private UserCredentialsRepository userCredentialsRepository;

    @Autowired
    BCryptPasswordEncoder encoder;

    @Override
    public UserCredentials save(UserCredentials userCredentials) {
        if(userCredentials.getId() == null){
            userCredentials.setId(UUIDs.timeBased());
        }
        if(!StringUtils.isEmpty(userCredentials.getPassword())){
            userCredentials.setPassword(encoder.encode(userCredentials.getPassword()));
        }
        return userCredentialsRepository.save(new UserCredentialsEntity(userCredentials)).toData();

    }

    @Override
    public UserCredentials findByUserId(UUID userId) {
        return userCredentialsRepository.findByUserId(UUIDConverter.fromTimeUUID(userId)).toData();
    }

}
