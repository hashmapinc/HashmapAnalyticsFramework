package com.hashmapinc.haf.requests;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hashmapinc.haf.models.ActivationType;
import com.hashmapinc.haf.models.User;
import com.hashmapinc.haf.models.UserCredentials;
import org.junit.Test;

import java.io.IOException;

public class CreateUserRequestTest {

    @Test
    public void testSerializationOfRequest() throws IOException {
        User user = new User();
        user.setId(UUIDs.timeBased());
        user.setClientId("haf");
        user.setEnabled(false);
        user.setUserName("demo@hashmapinc.com");

        UserCredentials credentials = new UserCredentials();
        credentials.setPassword("password");
        credentials.setType(ActivationType.NONE);

        CreateUserRequest request = new CreateUserRequest(user, credentials);

        ObjectMapper mapper = new ObjectMapper();

        String req = mapper.writeValueAsString(request);
        System.out.println(req);

        CreateUserRequest parsed = mapper.readValue(req, CreateUserRequest.class);

        System.out.println(parsed);
    }
}
