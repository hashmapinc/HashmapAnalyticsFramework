package com.hashmapinc.haf;

import com.datastax.driver.core.utils.UUIDs;
import com.hashmapinc.haf.models.User;
import com.hashmapinc.haf.models.UserCredentials;
import com.hashmapinc.haf.services.DatabaseUserDetailsService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.common.util.JacksonJsonParser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest
@ActiveProfiles("test")
public class AuthenticationTest {

    private static final String clientId = "ui";
    private static final String clientPassword = "password";

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    DatabaseUserDetailsService userService;

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).apply(springSecurity()).build();

        createUser();
    }

    @Test
    public void shouldHaveAllTheClaimsInToken() throws Exception {
        String accessToken = obtainAccessToken("demo", "password");
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("token", accessToken);

        Map<String, Object> response = accessOAuth2Endpoint(HttpMethod.GET, "/oauth/check_token", params);

        Assert.assertNotNull(response.get("tenant_id"));
        Assert.assertEquals("demo", response.get("user_name"));
        Assert.assertEquals("FirstName", response.get("firstName"));
        Assert.assertEquals("LastName", response.get("lastName"));
        Assert.assertNotNull(response.get("customer_id"));
        Assert.assertEquals(true, response.get("enabled"));
        Assert.assertEquals(Arrays.asList("admin", "user"), response.get("authorities"));
        Assert.assertEquals(Arrays.asList("subject1:*"), response.get("permissions"));
        Assert.assertEquals(clientId, response.get("client_id"));
        Assert.assertEquals(Arrays.asList("ui", "server"), response.get("scope"));
        Assert.assertNotNull(response.get("exp"));
        Assert.assertNotNull(response.get("iat"));
    }


    private String obtainAccessToken(String username, String password) throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("client_id", clientId);
        params.add("username", username);
        params.add("password", password);

        Map<String, Object> info = accessOAuth2Endpoint(HttpMethod.POST, "/oauth/token", params);
        return info.get("access_token").toString();
    }

    private Map<String, Object> accessOAuth2Endpoint(HttpMethod requestMethod, String endpoint, MultiValueMap<String, String> params) throws Exception {
        String contentType = "application/json;charset=UTF-8";
        MockHttpServletRequestBuilder request;
        if (requestMethod == HttpMethod.POST) {
            request = post(endpoint);
        } else {
            request = get(endpoint);
        }
        ResultActions result = mockMvc.perform(request.params(params)
                .with(httpBasic(clientId, clientPassword))
                .accept(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));

        String resultString = result.andReturn().getResponse().getContentAsString();
        JacksonJsonParser jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(resultString);
    }

    private void createUser() {
        User user = new User(UUIDs.timeBased());
        user.setUserName("demo");
        user.setFirstName("FirstName");
        user.setLastName("LastName");
        user.setCustomerId(UUIDs.timeBased().toString());
        user.setTenantId(UUIDs.timeBased().toString());
        user.setEnabled(true);
        user.setAuthorities(Arrays.asList("admin", "user"));
        user.setPermissions(Arrays.asList("subject1:*"));
        user.setClientId(clientId);

        User saved = userService.save(user);
        if (saved == null)
            throw new RuntimeException("User creation failed");
        else{
            UserCredentials credentials = userService.findCredentialsByUserId(saved.getId());
            credentials.setPassword("password");
            UserCredentials savedCred = userService.saveUserCredentials(credentials);
            if(savedCred == null){
                throw new RuntimeException("User creation failed");
            }
        }
    }
}
