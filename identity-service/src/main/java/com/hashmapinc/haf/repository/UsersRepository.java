package com.hashmapinc.haf.repository;

import com.hashmapinc.haf.entity.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersRepository extends BaseRepository<UserEntity, String> {

    List<UserEntity> findByUserNameAndClientId(String userName, String clientId);

    List<UserEntity> findByClientId(String clientId);

    List<UserEntity> findByAuthorities(String id);

    List<UserEntity> findByIdIn(List<String> id, Pageable pageable);

    List<UserEntity> findByTenantId(String tenantId);
}
