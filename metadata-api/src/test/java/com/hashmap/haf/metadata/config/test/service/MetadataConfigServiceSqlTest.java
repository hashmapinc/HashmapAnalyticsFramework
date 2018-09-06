package com.hashmap.haf.metadata.config.test.service;

import com.hashmap.haf.metadata.config.model.config.MetadataConfig;
import com.hashmap.haf.metadata.config.model.config.MetadataConfigId;
import com.hashmap.haf.metadata.config.page.TextPageLink;
import com.hashmap.haf.metadata.config.service.config.MetadataConfigService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Slf4j
public class MetadataConfigServiceSqlTest {

    @Autowired
    MetadataConfigService metadataConfigService;

    private MetadataConfig metadataConfig;

    @Before
    public void before() {
        metadataConfig = new MetadataConfig();
    }

    private void tearDown(MetadataConfigId metadataConfigId) {
        metadataConfigService.deleteMetadataConfig(metadataConfigId);
    }

    @Test
    public void saveMetadataConfig() {
        MetadataConfig savedMetadataConfig = metadataConfigService.saveMetadataConfig(metadataConfig);
        Assert.assertNotNull(savedMetadataConfig);
        Assert.assertNotNull(savedMetadataConfig.getId());
        tearDown(savedMetadataConfig.getId());
    }

    @Test
    public void findMetadataConfigById() {
        MetadataConfig savedMetadataConfig = metadataConfigService.saveMetadataConfig(metadataConfig);
        Assert.assertNotNull(savedMetadataConfig);

        MetadataConfigId metadataConfigId = savedMetadataConfig.getId();

        MetadataConfig found = metadataConfigService.findMetadataConfigById(metadataConfigId);
        Assert.assertNotNull(found);
        Assert.assertEquals(metadataConfigId, found.getId());
        tearDown(metadataConfigId);
    }

    @Test
    public void deleteMetadataConfig() {
        MetadataConfig savedMetadataConfig = metadataConfigService.saveMetadataConfig(metadataConfig);
        Assert.assertNotNull(savedMetadataConfig);

        MetadataConfigId metadataConfigId = savedMetadataConfig.getId();
        metadataConfigService.deleteMetadataConfig(metadataConfigId);
        MetadataConfig found = metadataConfigService.findMetadataConfigById(metadataConfigId);
        Assert.assertNull(found);
    }

    @Test
    public void findAllMetadataConfigByOwnerId() {
        String ownerId = "3f5d9a77-694c-11e8-ab22-b5af61ab8a6a";
        metadataConfig.setOwnerId(ownerId);
        String testMetaConf = "Test Meta Conf";
        metadataConfig.setName(testMetaConf);
        MetadataConfig savedMetadataConfig = metadataConfigService.saveMetadataConfig(metadataConfig);
        Assert.assertNotNull(savedMetadataConfig);

        List<MetadataConfig> firstPageResults = metadataConfigService.findAllMetadataConfigByOwnerId(ownerId, new TextPageLink(1)).getData();
        Assert.assertEquals(1, firstPageResults.size());
        Assert.assertEquals(testMetaConf, firstPageResults.get(0).getName());

        metadataConfig.setName(testMetaConf + "_1");
        metadataConfigService.saveMetadataConfig(metadataConfig);

        List<MetadataConfig> secondPageResults = metadataConfigService.findAllMetadataConfigByOwnerId(ownerId, new TextPageLink(1, firstPageResults.get(0).getUuidId())).getData();
        Assert.assertEquals(1, secondPageResults.size());
        Assert.assertNotEquals(firstPageResults.get(0), secondPageResults.get(0));

        tearDown(savedMetadataConfig.getId());
    }

    @Test
    public void updateMetadataConfig() {
        MetadataConfig savedMetadataConfig = metadataConfigService.saveMetadataConfig(metadataConfig);
        Assert.assertNotNull(savedMetadataConfig);

        log.error("Id : " + savedMetadataConfig.getId());
        savedMetadataConfig.setName("testMetadataConfig");
        MetadataConfig updated = metadataConfigService.updateMetadataConfig(savedMetadataConfig);
        Assert.assertNotNull(updated);
        Assert.assertEquals("testMetadataConfig", updated.getName());
        tearDown(savedMetadataConfig.getId());
    }

}