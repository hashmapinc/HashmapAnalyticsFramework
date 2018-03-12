package com.hashmapinc.haf.dao;

import com.hashmapinc.haf.entity.UserEntity;
import com.hashmapinc.haf.models.User;
import com.hashmapinc.haf.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UsersDaoImpl implements UsersDao{
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    BCryptPasswordEncoder encoder;

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
        String encoded = encoder.encode(user.getPassword());
        user.setPassword(encoded);
        return usersRepository.save(new UserEntity(user)).toData();
    }

    @Override
    public void deleteById(String userId) {
        usersRepository.delete(userId);
    }

    @Override
    public User findById(String userId) {
        UserEntity user = usersRepository.findOne(userId);
        if(user != null)
            return user.toData();
        return null;
    }
}
