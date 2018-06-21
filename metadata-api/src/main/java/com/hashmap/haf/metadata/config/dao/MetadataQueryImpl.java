package com.hashmap.haf.metadata.config.dao;

import com.hashmap.haf.metadata.config.entity.MetadataQueryEntity;
import com.hashmap.haf.metadata.config.model.MetadataQuery;
import com.hashmap.haf.metadata.core.common.dao.DaoUtil;
import com.hashmap.haf.metadata.core.util.UUIDConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MetadataQueryImpl implements MetadataQueryDao {

    @Autowired
    private QueryconfigRepository queryconfigRepository;

    @Override
    @Transactional
    public MetadataQuery save(MetadataQuery metadataQuery) {
        MetadataQueryEntity savedMetadataQueryEntity = queryconfigRepository.save(new MetadataQueryEntity(metadataQuery));
        return DaoUtil.getData(savedMetadataQueryEntity);
    }

    @Override
    public Optional<MetadataQuery> findById(UUID id) {
        String key = UUIDConverter.fromTimeUUID(id);
        return Optional.ofNullable(DaoUtil.getData(queryconfigRepository.findOne(key)));
    }

    @Override
    public List<MetadataQuery> findByMetadataId(UUID metadataId) {
        String key = UUIDConverter.fromTimeUUID(metadataId);
        List<MetadataQueryEntity> metadataConfigEntities = queryconfigRepository.findByMetadataId(key);
        return DaoUtil.convertDataList(metadataConfigEntities);
    }

    @Override
    public List<MetadataQuery> findAll() {
        List<MetadataQueryEntity> metadataQueryEntities = queryconfigRepository.findAll();
        return DaoUtil.convertDataList(metadataQueryEntities);
    }

    @Override
    @Transactional
    public boolean removeById(UUID id) {
        String key = UUIDConverter.fromTimeUUID(id);
        queryconfigRepository.delete(key);
        return queryconfigRepository.findOne(key) == null;
    }

}
