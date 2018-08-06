package com.hashmapinc.haf.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hashmapinc.haf.entity.UserCredentialsEntity;
import com.hashmapinc.haf.models.ActivationType;
import com.hashmapinc.haf.models.User;
import com.hashmapinc.haf.models.UserCredentials;
import com.hashmapinc.haf.repository.UserCredentialsRepository;
import com.hashmapinc.haf.requests.CreateUserRequest;
import com.hashmapinc.haf.services.DatabaseUserDetailsService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.JacksonJsonParser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest
@ActiveProfiles("test")
public class UserControllerTest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String clientId = "ui";
    private static final String clientPassword = "password";

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    DatabaseUserDetailsService userService;

    private String adminToken;
    private User user;
    private User admin;

    abstract class UserPasswordMixin{
        @JsonProperty(access = JsonProperty.Access.AUTO)
        private String password;
    }

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
                .apply(springSecurity()).build();
        mapper.addMixIn(User.class, UserPasswordMixin.class);
        admin = new User();
        admin.setUserName("demo");
        admin.setEnabled(true);
        admin.setAuthorities(Arrays.asList("admin", "user"));
        admin.setClientId(clientId);
        createUser(admin);

        user = new User();
        user.setUserName("redTailUser");
        user.setEnabled(true);
        user.setClientId(clientId);
        user.setAuthorities(Arrays.asList("user"));

        createUser(user);

        adminToken = obtainAccessToken("demo", "password");
    }

    @Test
    public void shouldReturnUnauthorizedResponseWhileCreatingUser() throws Exception {
        String json = mapper.writeValueAsString(new CreateUserRequest(user, getCredentials(), ActivationType.NONE));

        mockMvc.perform(
                post("/users")
                        .header("Content-Type", "application/json")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(
                status().isUnauthorized()
        );
    }

    @Test
    public void shouldCreateNewUserWithCorrectAuthorization() throws Exception{
        User u = new User(user);
        u.setId(null);
        u.setUserName("temporary_user1");
        String json = mapper.writeValueAsString(new CreateUserRequest(u, getCredentials(), ActivationType.NONE));

        mockMvc.perform(
                post("/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .header("Content-Type", "application/json")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(status().isCreated());
    }

    @Test
    public void shouldReturnBadRequestIfActivationTypeIsNoneAndPasswordIsNotProvided() throws Exception{
        User u = new User(user);
        u.setId(null);
        u.setUserName("temporary_user2");
        String json = mapper.writeValueAsString(new CreateUserRequest(u, null, ActivationType.NONE));

        mockMvc.perform(
                post("/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .header("Content-Type", "application/json")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldCreateUserDisabledIfActivationIsNotNone() throws Exception {
        User u = new User(user);
        u.setId(null);
        u.setUserName("temporary_user3");
        u.setEnabled(true);
        String json = mapper.writeValueAsString(new CreateUserRequest(u, getCredentials(), ActivationType.LINK));

        mockMvc.perform(
                post("/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .header("Content-Type", "application/json")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(status().isCreated()).andExpect(jsonPath("$.user.enabled").value(false));
    }

    @Test
    public void shouldReturnConflictIfUserAlreadyPresentWhilePost() throws Exception {
        String json = mapper.writeValueAsString(new CreateUserRequest(user, getCredentials(), ActivationType.NONE));

        mockMvc.perform(
                post("/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .header("Content-Type", "application/json")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(
                status().isConflict()
        );
    }

    @Test
    public void shouldReturnConflictIfUserNameAlreadyPresentForRequestingClient() throws Exception {
        User u = new User(user);
        u.setId(null);
        String json = mapper.writeValueAsString(new CreateUserRequest(u, getCredentials(), ActivationType.NONE));

        mockMvc.perform(
                post("/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .header("Content-Type", "application/json")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(
                status().isConflict()
        );
    }

    @Test
    public void shouldReturnCurrentUserPrincipalLoggedIn() throws Exception {
        mockMvc.perform(
                get("/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer "+ adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userAuthentication.principal.enabled").value(true))
                .andExpect(jsonPath("$.userAuthentication.principal.user.id").value(admin.getId().toString()))
                .andExpect(jsonPath("$.userAuthentication.principal.user.userName").value("demo"));
    }

    @Test
    public void shouldReturnUnauthorizedIfNoPrincipalIsFound() throws Exception {
        mockMvc.perform(
                get("/users/current")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnUnauthorizedIfInvalidPrincipalIsFound() throws Exception {
        mockMvc.perform(
                get("/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer invalidToken"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnUserById() throws Exception {
        mockMvc.perform(
                get("/users/" + user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer "+ adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId().toString()))
                .andExpect(jsonPath("$.userName").value(user.getUserName()))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    public void shouldReturnUnauthorizedWhileFetchingUser() throws Exception {
        mockMvc.perform(
                get("/users/"+ user.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnRelevantInformationInJWTToken() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("client_id", clientId);
        params.add("username", user.getUserName());
        params.add("password", "password");

        Map<String, Object> info = accessOAuth2Endpoint(params, "/oauth/token");

        Assert.assertNotNull(info);
        Assert.assertNotNull(info.get("access_token"));
        Assert.assertNotNull(info.get("refresh_token"));
        Assert.assertEquals(info.get("token_type"), "bearer");
        Assert.assertEquals(info.get("scope"), "ui server");
        Assert.assertNotNull(info.get("jti"));
        Assert.assertNotNull(info.get("expires_in"));
    }

    @Test
    public void shouldReturnRefreshTokenForUser() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("client_id", clientId);
        params.add("username", user.getUserName());
        params.add("password", "password");

        Map<String, Object> response = accessOAuth2Endpoint(params, "/oauth/token");
        String refreshToken = (String)response.get("refresh_token");
        String accessToken = (String)response.get("access_token");

        params.remove("grant_type");
        params.remove("username");
        params.remove("password");
        params.add("grant_type", "refresh_token");
        params.add("refresh_token", refreshToken);

        Map<String, Object> refreshTokenResponse = accessOAuth2Endpoint(params, "/oauth/token");

        Assert.assertNotNull(refreshTokenResponse.get("access_token"));
        Assert.assertNotEquals(refreshTokenResponse.get("access_token"), accessToken);
        Assert.assertNotNull(refreshTokenResponse.get("refresh_token"));
        Assert.assertEquals(refreshTokenResponse.get("token_type"), "bearer");
        Assert.assertEquals(refreshTokenResponse.get("scope"), "ui server");
        Assert.assertNotNull(refreshTokenResponse.get("jti"));
        Assert.assertNotNull(refreshTokenResponse.get("expires_in"));
    }

    @Test
    public void shouldReturnClientCredentialsGrant() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "client_credentials");
        params.add("client_id", clientId);
        params.add("client_secret", clientPassword);
        params.add("scope", "server");

        Map<String, Object> info = accessOAuth2Endpoint(params, "/oauth/token");

        Assert.assertNotNull(info);
        Assert.assertNotNull(info.get("access_token"));
        Assert.assertEquals(info.get("token_type"), "bearer");
        Assert.assertEquals(info.get("scope"), "server");
        Assert.assertNotNull(info.get("jti"));
        Assert.assertNotNull(info.get("expires_in"));
        Assert.assertNull(info.get("refresh_token"));
    }

    private String obtainAccessToken(String username, String password) throws Exception {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("client_id", clientId);
        params.add("username", username);
        params.add("password", password);

        Map<String, Object> info = accessOAuth2Endpoint(params, "/oauth/token");


        return info.get("access_token").toString();
    }

    private Map<String, Object> accessOAuth2Endpoint(MultiValueMap<String, String> params, String endpoint) throws Exception {
        String contentType = "application/json;charset=UTF-8";
        ResultActions result
                = mockMvc.perform(post(endpoint)
                .params(params)
                .with(httpBasic(clientId,clientPassword))
                .accept(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));

        String resultString = result.andReturn().getResponse().getContentAsString();
        JacksonJsonParser jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(resultString);
    }

    private void createUser(User user){
        User saved = userService.save(user);
        if(saved == null) {
            throw new RuntimeException("User creation failed");
        }else{
            UserCredentials credentials = userService.findCredentialsByUserId(saved.getId());
            credentials.setPassword("password");
            UserCredentials savedCred = userService.saveUserCredentials(credentials);
            if(savedCred == null){
                throw new RuntimeException("User creation failed");
            }
        }
    }

    private UserCredentials getCredentials() {
        UserCredentials credentials = new UserCredentials();
        credentials.setPassword("password");
        return credentials;
    }
}
