package com.hashmap.haf.metadata.config.dao;

import com.hashmap.haf.metadata.config.entity.MetadataQueryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetadataQueryRepository extends JpaRepository<MetadataQueryEntity, String> {

    List<MetadataQueryEntity> findByMetadataConfigId(@Param("metadataConfigId") String metadataConfigId);

    int removeByMetadataConfigId(@Param("metadataConfigId") String metadataConfigId);
}
