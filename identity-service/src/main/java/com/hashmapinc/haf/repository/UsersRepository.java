package com.hashmapinc.haf.repository;

import com.hashmapinc.haf.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersRepository extends CrudRepository<UserEntity, String> {

    List<UserEntity> findByUserName(String userName);
}
