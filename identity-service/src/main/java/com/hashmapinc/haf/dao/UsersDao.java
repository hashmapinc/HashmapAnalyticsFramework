package com.hashmapinc.haf.dao;


import com.hashmapinc.haf.models.User;
import com.hashmapinc.haf.page.PaginatedRequest;
import org.springframework.data.domain.PageRequest;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface UsersDao {

    User findByUserName(String userName, String clientId);

    User save(User user);

    void deleteById(String userId);

    User findById(UUID userId);

    List<User> findByIdIn(List<UUID> userIds, PageRequest pageRequest);

    Collection<User> findAllByClientId(String clientId);

    List<User> findByCriteria(PaginatedRequest request);

    List<User> findByAuthorities(String authority);

    List<User> findByTenantId(String tenantId);

    List<User> findByClientIdAndAuthorities(String clientId , String authority);

    List<User> findByClientIdAndAuthoritiesAndAdditionalDetails(String clientId, String authority, String keyName, String keyValue);
}
