package com.hashmap.haf.metadata.config.dao;

import com.hashmap.haf.metadata.config.entity.MetadataQueryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QueryconfigRepository  extends JpaRepository<MetadataQueryEntity, String> {
    List<MetadataQueryEntity> findByMetadataId(String metadataId);

}
