package com.hashmapinc.haf.dao;


import com.hashmapinc.haf.models.User;

public interface UsersDao {

    User findByUserName(String userName);

    User save(User user);

    void deleteById(String userId);
}
