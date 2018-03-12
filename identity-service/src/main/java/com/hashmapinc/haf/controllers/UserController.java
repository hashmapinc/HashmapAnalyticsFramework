package com.hashmapinc.haf.controllers;

import com.hashmapinc.haf.models.User;
import com.hashmapinc.haf.services.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserDetailsService userService;

    @Value("${users.provider}")
    private String provider;

    @RequestMapping(value = "/current", method = RequestMethod.GET)
    public Principal getUser(Principal principal) {
        return principal;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> save(@RequestBody User user){
        if(provider.equalsIgnoreCase("database")) {
            User savedUser = userService.save(user);
            URI uri = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(savedUser.getId())
                    .toUri();
            return ResponseEntity.created(uri).body(savedUser);
        }
        else
            return ResponseEntity
                    .status(HttpStatus.METHOD_NOT_ALLOWED)
                    .body("User can't be created as provider is set to " + provider);
    }
}
