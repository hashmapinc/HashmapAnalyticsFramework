package com.hashmap.haf.metadata.config.dao.config;

import com.hashmap.haf.metadata.config.constants.ModelConstants;
import com.hashmap.haf.metadata.config.dao.DaoUtil;
import com.hashmap.haf.metadata.config.entity.config.MetadataConfigEntity;
import com.hashmap.haf.metadata.config.model.config.MetadataConfig;
import com.hashmap.haf.metadata.config.model.data.resource.jdbc.JdbcResource;
import com.hashmap.haf.metadata.config.page.TextPageLink;
import com.hashmap.haf.metadata.config.utils.Encrypter;
import com.hashmap.haf.metadata.config.utils.UUIDConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Component
public class MetadataConfigDaoImpl implements MetadataConfigDao {

    @Autowired
    private MetadataConfigRepository metadataConfigRepository;

    @Autowired
    private Encrypter encrypter;

    @Override
    @Transactional
    public MetadataConfig save(MetadataConfig metadataConfig) {
        MetadataConfig cleanedMetadata = cleanUpForDb(metadataConfig);
        MetadataConfigEntity metadataConfigEntity = new MetadataConfigEntity(cleanedMetadata);
        MetadataConfigEntity savedMetadataConfigEntity = metadataConfigRepository.save(metadataConfigEntity);
        MetadataConfig metadata = DaoUtil.getData(savedMetadataConfigEntity);
        return cleanUpFromDb(metadata);
    }

    @Override
    public Optional<MetadataConfig> findById(UUID id) {
        String key = UUIDConverter.fromTimeUUID(id);
        Optional<MetadataConfig> metadataConfig = Optional.ofNullable(DaoUtil.getData(metadataConfigRepository.findOne(key)));
        metadataConfig.ifPresent(this::cleanUpFromDb);
        return metadataConfig;
    }

    @Override
    public Optional<MetadataConfig> findByNameAndOwnerId(String name , String ownerId) {
        Optional<MetadataConfig> metadataConfig = Optional.ofNullable(DaoUtil.getData(metadataConfigRepository.findByNameAndOwnerId(name, ownerId)));
        metadataConfig.ifPresent(this::cleanUpFromDb);
        return metadataConfig;
    }

    @Override
    public List<MetadataConfig> findByOwnerId(String ownerId, TextPageLink textPageLink) {
        List<MetadataConfigEntity> metadataConfigEntities = metadataConfigRepository.findByOwnerId(ownerId,
                textPageLink.getIdOffset() == null ? ModelConstants.NULL_UUID_STR : UUIDConverter.fromTimeUUID(textPageLink.getIdOffset()),
                new PageRequest(0, textPageLink.getLimit())
        );
        List<MetadataConfig> metadataConfigs = DaoUtil.convertDataList(metadataConfigEntities);
        metadataConfigs = metadataConfigs.stream().peek(this::cleanUpFromDb).collect(Collectors.toList());
        return metadataConfigs;
    }

    @Override
    @Transactional
    public boolean removeById(UUID id) {
        String key = UUIDConverter.fromTimeUUID(id);
        metadataConfigRepository.delete(key);
        return metadataConfigRepository.findOne(key) == null;
    }

    private MetadataConfig cleanUpForDb(MetadataConfig metadataConfig) {
        if (metadataConfig.getSource() instanceof JdbcResource) {
            JdbcResource jdbcResource = (JdbcResource) metadataConfig.getSource();
            String encryptPassword = encrypter.encrypt(jdbcResource.getPassword());
            jdbcResource.setPassword(encryptPassword);
            metadataConfig.setSource(jdbcResource);
        }
        return metadataConfig;
    }

    private MetadataConfig cleanUpFromDb(MetadataConfig metadataConfig) {
        if (metadataConfig.getSource() instanceof JdbcResource) {
            JdbcResource jdbcResource = (JdbcResource) metadataConfig.getSource();
            String decryptPassword = encrypter.decrypt(jdbcResource.getPassword());
            jdbcResource.setPassword(decryptPassword);
            metadataConfig.setSource(jdbcResource);
        }
        return metadataConfig;
    }
}
