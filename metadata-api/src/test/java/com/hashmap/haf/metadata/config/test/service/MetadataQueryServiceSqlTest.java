package com.hashmap.haf.metadata.config.test.service;

import com.hashmap.haf.metadata.config.model.MetadataConfig;
import com.hashmap.haf.metadata.config.model.MetadataConfigId;
import com.hashmap.haf.metadata.config.model.MetadataQuery;
import com.hashmap.haf.metadata.config.model.MetadataQueryId;
import com.hashmap.haf.metadata.config.service.MetadataQueryService;
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
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Slf4j
public class MetadataQueryServiceSqlTest {

    @Autowired
    private MetadataQueryService metadataQueryService;

    private MetadataQuery metadataQuery;
    private MetadataConfigId metadataConfigId;

    @Before
    public void before() {
        metadataConfigId = new MetadataConfigId(UUID.fromString("3f5d9a77-694c-11e8-ab22-b5af61ab8a6a"));
        metadataQuery = new MetadataQuery();
        metadataQuery.setMetadataConfigId(metadataConfigId);
        metadataQuery.setQueryStmt("TestQueryStatement");
    }

    private void tearDown(MetadataQueryId metadataQueryId) {
        metadataQueryService.deleteMetadataQuery(metadataQueryId);
    }

    @Test
    public void saveMetadataQuery() {
        MetadataQuery saveMetadataQuery = metadataQueryService.saveMetadataQuery(metadataQuery);
        Assert.assertNotNull(saveMetadataQuery);
        Assert.assertNotNull(saveMetadataQuery.getId());
        tearDown(saveMetadataQuery.getId());
    }

    @Test
    public void findMetadataQueryById() {
        MetadataQuery saveMetadataQuery = metadataQueryService.saveMetadataQuery(metadataQuery);
        Assert.assertNotNull(saveMetadataQuery);

        MetadataQueryId metadataQueryId = saveMetadataQuery.getId();

        MetadataQuery found = metadataQueryService.findMetadataQueryById(metadataQueryId);
        Assert.assertNotNull(found);
        Assert.assertEquals(metadataQueryId, found.getId());
        tearDown(metadataQueryId);
    }

    @Test
    public void deleteMetadataQuery() {
        MetadataQuery saveMetadataQuery = metadataQueryService.saveMetadataQuery(metadataQuery);
        Assert.assertNotNull(saveMetadataQuery);

        MetadataQueryId metadataQueryId = saveMetadataQuery.getId();
        metadataQueryService.deleteMetadataQuery(metadataQueryId);
        MetadataQuery found = metadataQueryService.findMetadataQueryById(metadataQueryId);
        Assert.assertNull(found);
    }

    @Test
    public void findAllMetadataQueryByMetadataId() {
        MetadataQuery savedMetadataQuery = metadataQueryService.saveMetadataQuery(metadataQuery);
        Assert.assertNotNull(savedMetadataQuery);

        List<MetadataQuery> found = metadataQueryService.findAllMetadataQueryByMetadataId(metadataConfigId);
        Assert.assertEquals(1, found.size());
        tearDown(savedMetadataQuery.getId());
    }

    @Test
    public void findAllMetadataQuery() {
        MetadataQuery saveMetadataQuery = metadataQueryService.saveMetadataQuery(metadataQuery);
        Assert.assertNotNull(saveMetadataQuery);

        MetadataQuery metadataQuery1 = new MetadataQuery();
        metadataQuery1.setMetadataConfigId(metadataConfigId);
        metadataQuery1.setQueryStmt("TestQueryStatement1");
        MetadataQuery saveMetadataQuery1 = metadataQueryService.saveMetadataQuery(metadataQuery1);
        Assert.assertNotNull(saveMetadataQuery1);

        List<MetadataQuery> found = metadataQueryService.findAllMetadataQuery();
        Assert.assertEquals(2, found.size());
        tearDown(saveMetadataQuery.getId());
        tearDown(saveMetadataQuery1.getId());
    }

    @Test
    public void updateMetadataQuery() {
        MetadataQuery saveMetadataQuery = metadataQueryService.saveMetadataQuery(metadataQuery);
        Assert.assertNotNull(saveMetadataQuery);

        saveMetadataQuery.setQueryStmt("TestQueryStatementUpdated");
        MetadataQuery updated = metadataQueryService.updateMetadataQuery(saveMetadataQuery);
        Assert.assertNotNull(updated);
        Assert.assertEquals("TestQueryStatementUpdated", updated.getQueryStmt());
        tearDown(saveMetadataQuery.getId());
    }
}
