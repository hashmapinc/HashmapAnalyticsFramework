package com.hashmap.haf.metadata.config.dao.config;

import com.hashmap.haf.metadata.config.dao.DaoUtil;
import com.hashmap.haf.metadata.config.utils.UUIDConverter;
import com.hashmap.haf.metadata.config.entity.config.MetadataConfigEntity;
import com.hashmap.haf.metadata.config.model.config.MetadataConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Component
public class MetadataConfigDaoImpl implements MetadataConfigDao {

    @Autowired
    private MetadataConfigRepository metadataConfigRepository;

    @Override
    @Transactional
    public MetadataConfig save(MetadataConfig metadataConfig) {
        MetadataConfigEntity savedMetadataConfigEntity = metadataConfigRepository.save(new MetadataConfigEntity(metadataConfig));
        return DaoUtil.getData(savedMetadataConfigEntity);
    }

    @Override
    public Optional<MetadataConfig> findById(UUID id) {
        String key = UUIDConverter.fromTimeUUID(id);
        return Optional.ofNullable(DaoUtil.getData(metadataConfigRepository.findOne(key)));
    }

    @Override
    public List<MetadataConfig> findByOwnerId(String ownerId) {
        List<MetadataConfigEntity> metadataConfigEntities = metadataConfigRepository.findByOwnerId(ownerId);
        return DaoUtil.convertDataList(metadataConfigEntities);
    }

    @Override
    public List<MetadataConfig> findAll() {
        List<MetadataConfigEntity> metadataConfigEntities = metadataConfigRepository.findAll();
        return DaoUtil.convertDataList(metadataConfigEntities);
    }

    @Override
    @Transactional
    public boolean removeById(UUID id) {
        String key = UUIDConverter.fromTimeUUID(id);
        metadataConfigRepository.delete(key);
        return metadataConfigRepository.findOne(key) == null;
    }

}
