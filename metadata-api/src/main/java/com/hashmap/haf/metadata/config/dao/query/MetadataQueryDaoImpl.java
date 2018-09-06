package com.hashmap.haf.metadata.config.dao.query;

import com.hashmap.haf.metadata.config.constants.ModelConstants;
import com.hashmap.haf.metadata.config.dao.DaoUtil;
import com.hashmap.haf.metadata.config.entity.query.MetadataQueryEntity;
import com.hashmap.haf.metadata.config.model.query.MetadataQuery;
import com.hashmap.haf.metadata.config.page.TextPageLink;
import com.hashmap.haf.metadata.config.utils.UUIDConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class MetadataQueryDaoImpl implements MetadataQueryDao {

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
    public List<MetadataQuery> findByMetadataConfigId(UUID metadataId, TextPageLink pageLink) {
        List<MetadataQueryEntity> metadataQueryEntities = metadataQueryRepository.findByMetadataConfigId(
                UUIDConverter.fromTimeUUID(metadataId),
                pageLink.getIdOffset() == null ? ModelConstants.NULL_UUID_STR : UUIDConverter.fromTimeUUID(pageLink.getIdOffset()),
                new PageRequest(0, pageLink.getLimit())
        );
        return DaoUtil.convertDataList(metadataQueryEntities);
    }

    @Override
    public List<MetadataQuery> findAll() {
        List<MetadataQueryEntity> metadataQueryEntities = metadataQueryRepository.findAll();
        return DaoUtil.convertDataList(metadataQueryEntities);
    }

    @Override
    @Transactional
    public int removeByMetadataConfigId(UUID metadataId) {
        String key = UUIDConverter.fromTimeUUID(metadataId);
        return metadataQueryRepository.removeByMetadataConfigId(key);
    }

    @Override
    @Transactional
    public boolean removeById(UUID id) {
        String key = UUIDConverter.fromTimeUUID(id);
        metadataQueryRepository.delete(key);
        return metadataQueryRepository.findOne(key) == null;
    }
}
