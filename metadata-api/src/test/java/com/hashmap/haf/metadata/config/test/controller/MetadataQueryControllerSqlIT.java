package com.hashmap.haf.metadata.config.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hashmap.haf.metadata.config.dao.query.MetadataQueryDao;
import com.hashmap.haf.metadata.config.model.config.MetadataConfig;
import com.hashmap.haf.metadata.config.model.config.MetadataConfigId;
import com.hashmap.haf.metadata.config.model.query.MetadataQuery;
import com.hashmap.haf.metadata.config.model.query.MetadataQueryId;
import com.hashmap.haf.metadata.config.service.config.MetadataConfigService;
import com.hashmap.haf.metadata.config.service.query.MetadataQueryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
@PropertySource("classpath:application-test.yaml")
@ActiveProfiles("test")
public class MetadataQueryControllerSqlIT {
    private static final ObjectMapper mapper = new ObjectMapper();


    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MetadataQueryService metadataQueryService;

    @Autowired
    private MetadataQueryDao metadataQueryDao;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MetadataConfigService metadataConfigService;

    private MockMvc mockMvc;
    private String adminToken;
    private MetadataConfigId metadataConfigId;
    private MetadataQuery metadataQuery;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
                .apply(springSecurity()).build();
        metadataConfigId = new MetadataConfigId(UUID.fromString("ed11697a-745a-11e8-aef0-939173014f2b"));
        MetadataConfig metadataConfig = new MetadataConfig(metadataConfigId);
        metadataConfigService.saveMetadataConfig(metadataConfig);

        metadataQuery = new MetadataQuery();
        metadataQuery.setMetadataConfigId(metadataConfigId);
        metadataQuery.setQueryStmt("TestQueryStatement");
        adminToken = obtainAccessToken();
    }

    private void tearDown(MetadataQueryId metadataQueryId) {
        metadataQueryService.deleteMetadataQuery(metadataQueryId);
        metadataConfigService.deleteMetadataConfig(metadataConfigId);
    }

    private String obtainAccessToken() {
        OAuth2RestTemplate oAuth2RestTemplate = (OAuth2RestTemplate) this.restTemplate;
        return oAuth2RestTemplate.getAccessToken().getValue();
    }

    @Test
    public void savedMetadataQuery() throws Exception {
        String json = mapper.writeValueAsString(metadataQuery);
        MvcResult mvcResult = mockMvc.perform(
                post("/api/metaquery")
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andReturn();
        MetadataQuery saved = mapper.readValue(mvcResult.getResponse().getContentAsString(), MetadataQuery.class);
        Assert.assertNotNull(saved);
        Assert.assertNotNull(saved.getId());

        Assert.assertEquals(saved, metadataQueryDao.findById(saved.getUuidId()).get());
        tearDown(saved.getId());
    }

    @Test
    public void updateMetadataQuery() throws Exception {
        metadataQuery.setQueryStmt("SELECT * FROM table_name");
        MetadataQuery savedMetadataQuery = metadataQueryService.saveMetadataQuery(metadataQuery);
        metadataQuery.setId(savedMetadataQuery.getId());
        metadataQuery.setQueryStmt("DELETE FROM table_name");
        String json = mapper.writeValueAsString(metadataQuery);
        MvcResult mvcResult = mockMvc.perform(
                put("/api/metaquery")
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andReturn();

        MetadataQuery updatedMetadataQuery = mapper.readValue(mvcResult.getResponse().getContentAsString(), MetadataQuery.class);
        Assert.assertNotNull(updatedMetadataQuery);
        Assert.assertEquals(savedMetadataQuery.getId(), updatedMetadataQuery.getId());
        Assert.assertNotEquals(updatedMetadataQuery.getQueryStmt(), savedMetadataQuery.getQueryStmt());
        Assert.assertEquals(updatedMetadataQuery.getQueryStmt(), "DELETE FROM table_name");
        Assert.assertEquals(updatedMetadataQuery, metadataQueryDao.findById(updatedMetadataQuery.getUuidId()).get());

        tearDown(savedMetadataQuery.getId());
    }

    @Test
    public void getMetadataQuery() throws Exception {
        String json = mapper.writeValueAsString(metadataQuery);
        MetadataQuery savedMetadataQuery= metadataQueryService.saveMetadataQuery(metadataQuery);
        MetadataQueryId metadataQueryId = savedMetadataQuery.getId();
        MvcResult mvcResult = mockMvc.perform(
                get("/api/metaquery/" + metadataQueryId)
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andReturn();

        MetadataQuery found = mapper.readValue(mvcResult.getResponse().getContentAsString(), MetadataQuery.class);
        Assert.assertNotNull(found);
        Assert.assertEquals(metadataQueryId, found.getId());
        tearDown(found.getId());
    }

    @Test
    public void getMetadataQueryByInvalidId() throws Exception {
        MetadataQueryId metadataQueryId = new MetadataQueryId(UUID.fromString("ed11697a-745a-11e8-aef0-939173014f2b"));
        MvcResult mvcResult = mockMvc.perform(
                get("/api/metaquery/" + metadataQueryId)
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andReturn();
        String reponse = mvcResult.getResponse().getContentAsString();
        Assert.assertEquals("{\"error\":\"Requested item wasn't found!\"}",reponse);
    }

    @Test
    public void shouldReturnUnauthorizedResponseWhileGetMetadataQuery() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                get("/api/metaquery")
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + "Fake_Token")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    public void getMetadataQueryByMetadataConfigId() throws Exception {
        String json = mapper.writeValueAsString(metadataQuery);
        metadataQuery.setMetadataConfigId(metadataConfigId);
        MetadataQuery savedMetadataQuery = metadataQueryService.saveMetadataQuery(metadataQuery);
        MvcResult mvcResult = mockMvc.perform(
                get("/api/metaquery/metaconfig/" + metadataConfigId.getId().toString())
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andReturn();
        List found = mapper.readValue(mvcResult.getResponse().getContentAsString(), List.class);
        Assert.assertNotNull(found);
        Assert.assertEquals(1, found.size());
        tearDown(savedMetadataQuery.getId());
    }


    @Test
    public void deleteMetadataQuery() throws Exception {
        String json = mapper.writeValueAsString(metadataQuery);
        MetadataQuery saveMetadataQuery = metadataQueryService.saveMetadataQuery(metadataQuery);
        mockMvc.perform(
                delete("/api/metaquery/" + saveMetadataQuery.getId())
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        Assert.assertNull(metadataQueryService.findMetadataQueryById(saveMetadataQuery.getId()));
        Assert.assertNotNull(metadataQueryDao.findById(saveMetadataQuery.getUuidId()));
    }
}
