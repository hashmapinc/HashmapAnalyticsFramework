package com.hashmap.haf.metadata.config.dao.config;

import com.hashmap.haf.metadata.config.entity.config.MetadataConfigEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetadataConfigRepository extends JpaRepository<MetadataConfigEntity, String> {

    @Query("SELECT m FROM MetadataConfigEntity m WHERE m.ownerId = :ownerId " +
            "AND m.id > :idOffset ORDER BY m.id")
    List<MetadataConfigEntity> findByOwnerId(@Param("ownerId") String ownerId,
                                             @Param("idOffset") String idOffset,
                                             Pageable pageable);

    MetadataConfigEntity findByNameAndOwnerId(String name, String ownerId);
}
