package com.hashmap.haf.metadata.config.test.service;

import com.hashmap.haf.metadata.config.model.MetadataConfig;
import com.hashmap.haf.metadata.config.model.MetadataConfigId;
import com.hashmap.haf.metadata.config.service.MetadataConfigService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;

import java.lang.reflect.Array;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class MetadataConfigServiceSqlTest {

        @Autowired
    MetadataConfigService metadataConfigService;

    private MetadataConfig metadataConfig;

    @Before
    public void before() {
        metadataConfig = new MetadataConfig();
    }

    @Test
    public void saveMetadataConfig() {
        MetadataConfig savedMetadataConfig = metadataConfigService.saveMetadataConfig(metadataConfig);
        Assert.assertNotNull(savedMetadataConfig);
        Assert.assertNotNull(savedMetadataConfig.getId());
    }

    @Test
    public void findMetadataConfigById() {
        MetadataConfig savedMetadataConfig = metadataConfigService.saveMetadataConfig(metadataConfig);
        Assert.assertNotNull(savedMetadataConfig);

        MetadataConfigId metadataConfigId = savedMetadataConfig.getId();

        MetadataConfig found = metadataConfigService.findMetadataConfigById(metadataConfigId);
        Assert.assertNotNull(found);
        Assert.assertEquals(metadataConfigId, found.getId());
    }

}