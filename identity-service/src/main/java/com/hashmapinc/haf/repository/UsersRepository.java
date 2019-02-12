package com.hashmapinc.haf.repository;

import com.hashmapinc.haf.entity.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersRepository extends BaseRepository<UserEntity, String> {

    List<UserEntity> findByUserNameAndClientId(String userName, String clientId);

    List<UserEntity> findByClientId(String clientId);

    List<UserEntity> findByAuthorities(String authority);

    List<UserEntity> findByIdIn(List<String> id, Pageable pageable);

    List<UserEntity> findByTenantId(String tenantId);

    List<UserEntity> findByClientIdAndAuthorities(String clientId , String authority);

    @Query(value = "SELECT * from  haf_users  INNER JOIN haf_user_details ON haf_users.id = haf_user_details.user_id INNER JOIN  haf_user_authorities ON haf_users.id = haf_user_authorities.user_id " +
            " WHERE haf_users.client_id = ?1" +
            " and haf_user_authorities.authorities_id = ?2" +
            " and haf_user_details.key_name = ?3" +
            " and haf_user_details.key_value = ?4",nativeQuery = true)
    List<UserEntity> findByClientIdAndAuthoritiesAndAdditionalDetails(String clientId, String authority,
                                                                      String keyName , String keyValue);
}
