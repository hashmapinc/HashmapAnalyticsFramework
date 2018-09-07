package com.hashmap.haf.metadata.config.dao.config;

import com.hashmap.haf.metadata.config.constants.ModelConstants;
import com.hashmap.haf.metadata.config.dao.DaoUtil;
import com.hashmap.haf.metadata.config.entity.config.MetadataConfigEntity;
import com.hashmap.haf.metadata.config.model.config.MetadataConfig;
import com.hashmap.haf.metadata.config.page.TextPageLink;
import com.hashmap.haf.metadata.config.utils.UUIDConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


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
    public List<MetadataConfig> findByOwnerId(String ownerId, TextPageLink textPageLink) {
        List<MetadataConfigEntity> metadataConfigEntities = metadataConfigRepository.findByOwnerId(ownerId,
                textPageLink.getIdOffset() == null ? ModelConstants.NULL_UUID_STR : UUIDConverter.fromTimeUUID(textPageLink.getIdOffset()),
                new PageRequest(0, textPageLink.getLimit())
        );
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
