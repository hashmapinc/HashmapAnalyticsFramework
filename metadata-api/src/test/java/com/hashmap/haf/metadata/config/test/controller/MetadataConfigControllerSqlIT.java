package com.hashmap.haf.metadata.config.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hashmap.haf.metadata.config.dao.config.MetadataConfigDao;
import com.hashmap.haf.metadata.config.model.config.MetadataConfig;
import com.hashmap.haf.metadata.config.model.config.MetadataConfigId;
import com.hashmap.haf.metadata.config.service.config.MetadataConfigService;
import lombok.extern.slf4j.Slf4j;
import org.junit.*;
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
public class MetadataConfigControllerSqlIT {
    private static final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    MetadataConfigService metadataConfigService;

    @Autowired
    MetadataConfigDao metadataConfigDao;

    @Autowired
    RestTemplate restTemplate;

    private MockMvc mockMvc;
    private String adminToken;
    private MetadataConfig metadataConfig;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
                .apply(springSecurity()).build();
        metadataConfig = new MetadataConfig();
        adminToken = obtainAccessToken();
    }

    private void tearDown(MetadataConfigId metadataConfigId) {
        metadataConfigService.deleteMetadataConfig(metadataConfigId);
    }

    private String obtainAccessToken() {
        OAuth2RestTemplate oAuth2RestTemplate = (OAuth2RestTemplate) this.restTemplate;
        return oAuth2RestTemplate.getAccessToken().getValue();
    }

    @Test
    public void savedMetadataConfig() throws Exception {
        String json = mapper.writeValueAsString(metadataConfig);
        MvcResult mvcResult = mockMvc.perform(
                post("/api/metaconfig")
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andReturn();
        MetadataConfig saved = mapper.readValue(mvcResult.getResponse().getContentAsString(), MetadataConfig.class);
        Assert.assertNotNull(saved);
        Assert.assertNotNull(saved.getId());

        Assert.assertEquals(saved, metadataConfigDao.findById(saved.getUuidId()).get());
        tearDown(saved.getId());
    }

    @Test
    public void updateMetadataConfig() throws Exception {
        metadataConfig.setName("ConfigName");
        MetadataConfig savedMetadataConfig = metadataConfigService.saveMetadataConfig(metadataConfig);
        metadataConfig.setId(savedMetadataConfig.getId());
        metadataConfig.setName("Configuration");
        String json = mapper.writeValueAsString(metadataConfig);
        MvcResult mvcResult = mockMvc.perform(
                put("/api/metaconfig")
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andReturn();

        MetadataConfig updatedMetadataConfig = mapper.readValue(mvcResult.getResponse().getContentAsString(), MetadataConfig.class);
        Assert.assertNotNull(updatedMetadataConfig);
        Assert.assertEquals(savedMetadataConfig.getId(), updatedMetadataConfig.getId());
        Assert.assertNotEquals(updatedMetadataConfig.getName(), savedMetadataConfig.getName());
        Assert.assertEquals(updatedMetadataConfig.getName(), "Configuration");
        Assert.assertEquals(updatedMetadataConfig, metadataConfigDao.findById(updatedMetadataConfig.getUuidId()).get());

        tearDown(savedMetadataConfig.getId());
    }

    @Test
    public void getMetadataConfig() throws Exception {
        String json = mapper.writeValueAsString(metadataConfig);
        MetadataConfig savedMetadataConfig = metadataConfigService.saveMetadataConfig(metadataConfig);
        MetadataConfigId metadataConfigId = savedMetadataConfig.getId();
        MvcResult mvcResult = mockMvc.perform(
                get("/api/metaconfig/" + metadataConfigId)
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andReturn();

        MetadataConfig found = mapper.readValue(mvcResult.getResponse().getContentAsString(), MetadataConfig.class);
        Assert.assertNotNull(found);
        Assert.assertEquals(metadataConfigId, found.getId());
        tearDown(found.getId());
    }

    @Test
    public void getMetadataConfigByInvalidId() throws Exception {
        MetadataConfigId metadataConfigId = new MetadataConfigId(UUID.fromString("ed11697a-745a-11e8-aef0-939173014f2b"));
        MvcResult mvcResult = mockMvc.perform(
                get("/api/metaconfig/" + metadataConfigId)
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andReturn();
        String reponse = mvcResult.getResponse().getContentAsString();
        Assert.assertEquals("{\"error\":\"Requested item wasn't found!\"}",reponse);
    }

    @Test
    public void getMetadataConfigs() throws Exception {
        String json = mapper.writeValueAsString(metadataConfig);
        MetadataConfig metadataConfig1 = new MetadataConfig();
        MetadataConfig savedMetadataConfig1 = metadataConfigService.saveMetadataConfig(metadataConfig);
        MetadataConfig savedMetadataConfig2 = metadataConfigService.saveMetadataConfig(metadataConfig1);
        MvcResult mvcResult = mockMvc.perform(
                get("/api/metaconfig")
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andReturn();
        List<MetadataConfig> found = mapper.readValue(mvcResult.getResponse().getContentAsString(), List.class);
        Assert.assertNotNull(found);
        Assert.assertEquals(2, found.size());
        tearDown(savedMetadataConfig1.getId());
        tearDown(savedMetadataConfig2.getId());
    }

    @Test
    public void shouldReturnUnauthorizedResponseWhileGetMetadataConfigs() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                get("/api/metaconfig")
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + "Fake_Token")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    public void getMetadataConfigByOwnerId() throws Exception {
        String json = mapper.writeValueAsString(metadataConfig);
        String ownerId = "5af79646-6495-11e8-8e40-35361df8a23c";
        metadataConfig.setOwnerId(ownerId);
        MetadataConfig savedMetadataConfig = metadataConfigService.saveMetadataConfig(metadataConfig);
        MvcResult mvcResult = mockMvc.perform(
                get("/api/metaconfig/owner/" + ownerId)
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andReturn();
        List<MetadataConfig> found = mapper.readValue(mvcResult.getResponse().getContentAsString(), List.class);
        Assert.assertNotNull(found);
        Assert.assertEquals(1, found.size());
        tearDown(savedMetadataConfig.getId());
    }


    @Test
    public void deleteMetadataConfig() throws Exception {
        String json = mapper.writeValueAsString(metadataConfig);
        MetadataConfig savedMetadataConfig = metadataConfigService.saveMetadataConfig(metadataConfig);
        mockMvc.perform(
                delete("/api/metaconfig/" + savedMetadataConfig.getId())
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        Assert.assertNull(metadataConfigService.findMetadataConfigById(savedMetadataConfig.getId()));
        Assert.assertNotNull(metadataConfigDao.findById(savedMetadataConfig.getUuidId()));
    }
}