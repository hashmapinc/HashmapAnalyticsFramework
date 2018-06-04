package com.hashmapinc.haf.repository;

import com.hashmapinc.haf.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersRepository extends JpaRepository<UserEntity, String> {

    List<UserEntity> findByUserName(String userName);
}
