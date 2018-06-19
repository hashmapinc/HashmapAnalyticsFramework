package com.hashmapinc.haf.dao;

import com.datastax.driver.core.utils.UUIDs;
import com.hashmapinc.haf.entity.UserCredentialsEntity;
import com.hashmapinc.haf.models.UserCredentials;
import com.hashmapinc.haf.repository.UserCredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserCredentialsDaoImpl implements UserCredentialsDao{

    @Autowired
    private UserCredentialsRepository userCredentialsRepository;

    @Override
    public UserCredentials save(UserCredentials userCredentials) {
        if(userCredentials.getId() == null){
            userCredentials.setId(UUIDs.timeBased());
        }
        return userCredentialsRepository.save(new UserCredentialsEntity(userCredentials)).toData();

    }


}
