package com.hashmapinc.haf.dao;

import com.datastax.driver.core.utils.UUIDs;
import com.hashmapinc.haf.entity.UserEntity;
import com.hashmapinc.haf.models.User;
import com.hashmapinc.haf.repository.UsersRepository;
import com.hashmapinc.haf.utils.UUIDConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component
public class UsersDaoImpl implements UsersDao{
    @Autowired
    private UsersRepository usersRepository;

    @Override
    public User findByUserName(String userName, String clientId) {
        List<UserEntity> userEntities = usersRepository.findByUserNameAndClientId(userName, clientId);
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
        if(user.getId() == null){
            user.setId(UUIDs.timeBased());
        }
        return usersRepository.save(new UserEntity(user)).toData();
    }

    @Override
    public void deleteById(String userId) {
        usersRepository.delete(userId);
    }

    @Override
    public User findById(UUID userId) {
        UserEntity user = usersRepository.findOne(UUIDConverter.fromTimeUUID(userId));
        if(user != null)
            return user.toData();
        return null;
    }

    @Override
    public Collection<User> findAllByClientId(String clientId) {
        return convertDataList(usersRepository.findByClientId(clientId));
    }

    private List<User> convertDataList(Iterable<UserEntity> toDataList){
        List<UserEntity> entities = new ArrayList<>();
        List<User> list = new ArrayList<>();
        if(toDataList != null) {
            toDataList.iterator().forEachRemaining(entities::add);
            entities.forEach(e -> list.add(e.toData()));
        }
        return list;
    }
}
