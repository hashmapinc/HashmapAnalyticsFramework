package com.hashmap.haf.metadata.config.dao;

import com.hashmap.haf.metadata.config.entity.MetadataConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetadataConfigRepository extends JpaRepository<MetadataConfigEntity, String> {
    List<MetadataConfigEntity> findByOwnerId(String ownerId);
}
