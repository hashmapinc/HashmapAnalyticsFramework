package com.hashmapinc.haf.dao;

import com.datastax.driver.core.utils.UUIDs;
import com.hashmapinc.haf.entity.UserEntity;
import com.hashmapinc.haf.models.User;
import com.hashmapinc.haf.page.PaginatedRequest;
import com.hashmapinc.haf.repository.UsersRepository;
import com.hashmapinc.haf.utils.UUIDConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public List<User> findByIdIn(List<UUID> userIds, PageRequest pageRequest) {
        List<String> userIdsStr = userIds.stream().map(UUIDConverter::fromTimeUUID).collect(Collectors.toList());
        List<UserEntity> userEntities = usersRepository.findByIdIn(userIdsStr, pageRequest);
        return userEntities.stream().map(UserEntity::toData).collect(Collectors.toList());
    }

    @Override
    public Collection<User> findAllByClientId(String clientId) {
        return convertDataList(usersRepository.findByClientId(clientId));
    }

    @Override
    public List<User> findByCriteria(PaginatedRequest request) {
        return convertDataList(usersRepository.findPaginated(request, new PageRequest(0, request.getPageLink().getLimit())));
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
