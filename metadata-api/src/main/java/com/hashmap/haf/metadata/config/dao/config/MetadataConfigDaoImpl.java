package com.hashmap.haf.metadata.config.dao.config;

import com.hashmap.haf.metadata.config.constants.ModelConstants;
import com.hashmap.haf.metadata.config.dao.DaoUtil;
import com.hashmap.haf.metadata.config.entity.config.MetadataConfigEntity;
import com.hashmap.haf.metadata.config.model.config.MetadataConfig;
import com.hashmap.haf.metadata.config.model.data.resource.jdbc.JdbcResource;
import com.hashmap.haf.metadata.config.page.TextPageLink;
import com.hashmap.haf.metadata.config.utils.UUIDConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Component
@Slf4j
public class MetadataConfigDaoImpl implements MetadataConfigDao {

    @Autowired
    private MetadataConfigRepository metadataConfigRepository;

    @Autowired
    BCryptPasswordEncoder encoder;

    @Value("${password.jdbc-encrypt-salt}")
    private String saltStr;

    @Override
    @Transactional
    public MetadataConfig save(MetadataConfig metadataConfig) {
        encryptPassword(metadataConfig);
        MetadataConfigEntity metadataConfigEntity = new MetadataConfigEntity(metadataConfig);
        MetadataConfigEntity savedMetadataConfigEntity = metadataConfigRepository.save(metadataConfigEntity);
        MetadataConfig metadata = DaoUtil.getData(savedMetadataConfigEntity);
        decryptPassword(metadata);
        return metadata;
    }

    @Override
    public Optional<MetadataConfig> findById(UUID id) {
        String key = UUIDConverter.fromTimeUUID(id);
        Optional<MetadataConfig> metadataConfig = Optional.ofNullable(DaoUtil.getData(metadataConfigRepository.findOne(key)));
        metadataConfig.ifPresent(this::decryptPassword);
        return metadataConfig;
    }

    @Override
    public List<MetadataConfig> findByOwnerId(String ownerId, TextPageLink textPageLink) {
        List<MetadataConfigEntity> metadataConfigEntities = metadataConfigRepository.findByOwnerId(ownerId,
                textPageLink.getIdOffset() == null ? ModelConstants.NULL_UUID_STR : UUIDConverter.fromTimeUUID(textPageLink.getIdOffset()),
                new PageRequest(0, textPageLink.getLimit())
        );
        List<MetadataConfig> metadataConfigs = DaoUtil.convertDataList(metadataConfigEntities);
        metadataConfigs = metadataConfigs.stream().peek(this::decryptPassword).collect(Collectors.toList());
        return metadataConfigs;
    }

    @Override
    @Transactional
    public boolean removeById(UUID id) {
        String key = UUIDConverter.fromTimeUUID(id);
        metadataConfigRepository.delete(key);
        return metadataConfigRepository.findOne(key) == null;
    }

    private void encryptPassword(MetadataConfig metadataConfig) {
        if (metadataConfig.getSource() instanceof JdbcResource) {
            JdbcResource jdbcResource = (JdbcResource) metadataConfig.getSource();
            String encryptSalt = encoder.encode(saltStr);
            String salt = encryptSalt + jdbcResource.getPassword();
            jdbcResource.setPassword(Base64.getEncoder().encodeToString(salt.getBytes()));
        }
    }

    private void decryptPassword(MetadataConfig metadataConfig) {
        if (metadataConfig.getSource() instanceof JdbcResource) {
            JdbcResource jdbcResource = (JdbcResource) metadataConfig.getSource();
            String encryptSalt = encoder.encode(saltStr);
            String decodedPassword = new String(Base64.getDecoder().decode(jdbcResource.getPassword()));
            jdbcResource.setPassword(decodedPassword.substring(encryptSalt.length()));
        }
    }
}
