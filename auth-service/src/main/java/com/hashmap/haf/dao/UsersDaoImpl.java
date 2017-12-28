package com.hashmap.haf.dao;

import com.hashmap.haf.entity.UserEntity;
import com.hashmap.haf.models.User;
import com.hashmap.haf.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UsersDaoImpl implements UsersDao{
    @Autowired
    private UsersRepository usersRepository;

    @Override
    public User findByUserName(String userName) {
        List<UserEntity> userEntities = usersRepository.findByUserName(userName);
        if(userEntities != null && !userEntities.isEmpty()){
            UserEntity user = userEntities.get(0);
            if(user != null){
                return user.toData();
            }
        }
        return null;
    }

    @Override
    public User save(User user) {
        return usersRepository.save(new UserEntity(user)).toData();
    }

    @Override
    public void deleteById(String userId) {
        usersRepository.delete(userId);
    }
}
