package com.hashmap.haf.dao;

import com.hashmap.haf.models.User;

public interface UsersDao {

    User findByUserName(String userName);

    User save(User user);

    void deleteById(String userId);
}
