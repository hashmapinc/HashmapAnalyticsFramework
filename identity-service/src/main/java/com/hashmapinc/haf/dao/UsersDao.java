package com.hashmapinc.haf.dao;


import com.hashmapinc.haf.models.User;

import java.util.Collection;

public interface UsersDao {

    User findByUserName(String userName);

    User save(User user);

    void deleteById(String userId);

    User findById(String userId);

    Collection<User> findAll();
}
