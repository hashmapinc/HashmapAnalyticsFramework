package com.hashmap.haf.metadata.config.dao.query;

import com.hashmap.haf.metadata.config.entity.query.MetadataQueryEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetadataQueryRepository extends JpaRepository<MetadataQueryEntity, String> {

    @Query("SELECT m FROM MetadataQueryEntity m WHERE m.metadataConfigId = :metadataConfigId " +
            "AND m.id > :idOffset ORDER BY m.id")
    List<MetadataQueryEntity> findByMetadataConfigId(@Param("metadataConfigId") String metadataConfigId,
                                                     @Param("idOffset") String idOffset,
                                                     Pageable pageable);

    int removeByMetadataConfigId(@Param("metadataConfigId") String metadataConfigId);
}
