package com.hashmap.haf.metadata.config.dao;

import com.hashmap.haf.metadata.config.entity.MetadataQueryEntity;
import com.hashmap.haf.metadata.config.model.MetadataQuery;
import com.hashmap.haf.metadata.core.common.dao.DaoUtil;
import com.hashmap.haf.metadata.core.util.UUIDConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class MetadataQueryImpl implements MetadataQueryDao {

    @Autowired
    private MetadataQueryRepository metadataQueryRepository;

    @Override
    @Transactional
    public MetadataQuery save(MetadataQuery metadataQuery) {
        MetadataQueryEntity savedMetadataQueryEntity = metadataQueryRepository.save(new MetadataQueryEntity(metadataQuery));
        return DaoUtil.getData(savedMetadataQueryEntity);
    }

    @Override
    public Optional<MetadataQuery> findById(UUID id) {
        String key = UUIDConverter.fromTimeUUID(id);
        return Optional.ofNullable(DaoUtil.getData(metadataQueryRepository.findOne(key)));
    }

    @Override
    public List<MetadataQuery> findByMetadataConfigId(UUID metadataId) {
        String key = UUIDConverter.fromTimeUUID(metadataId);
        List<MetadataQueryEntity> metadataConfigEntities = metadataQueryRepository.findByMetadataConfigId(key);
        return DaoUtil.convertDataList(metadataConfigEntities);
    }

    @Override
    public List<MetadataQuery> findAll() {
        List<MetadataQueryEntity> metadataQueryEntities = metadataQueryRepository.findAll();
        return DaoUtil.convertDataList(metadataQueryEntities);
    }

    @Override
    @Transactional
    public boolean removeById(UUID id) {
        String key = UUIDConverter.fromTimeUUID(id);
        metadataQueryRepository.delete(key);
        return metadataQueryRepository.findOne(key) == null;
    }

}
