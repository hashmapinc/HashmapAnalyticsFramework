package com.hashmapinc.haf.dao;


import com.hashmapinc.haf.models.User;

import java.util.Collection;
import java.util.UUID;

public interface UsersDao {

    User findByUserName(String userName, String clientId);

    User save(User user);

    void deleteById(String userId);

    User findById(UUID userId);

    Collection<User> findAll();
}
