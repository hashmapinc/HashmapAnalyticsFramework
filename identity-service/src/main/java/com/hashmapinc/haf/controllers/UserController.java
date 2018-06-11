package com.hashmapinc.haf.controllers;

import com.hashmapinc.haf.models.User;
import com.hashmapinc.haf.models.UserInformation;
import com.hashmapinc.haf.services.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserDetailsService userService;

    @Value("${users.provider}")
    private String provider;

    @Value("${spring.application.name}")
    private String identityServiceName;

    @RequestMapping(value = "/current", method = RequestMethod.GET)
    public Principal getUser(Principal principal) {
        return principal;
    }

    @PreAuthorize("#oauth2.hasAnyScope('server', 'ui')")
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<?> getUserById(@PathVariable UUID userId){
        //String clientId = getCurrentClientId();
        User user = userService.findById(userId);
        if(user == null)
            return new ResponseEntity<>("No User found with id "+ userId, HttpStatus.NO_CONTENT);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("#oauth2.hasAnyScope('server', 'ui')")
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> save(@RequestBody User user){
        String clientId = getCurrentClientId();
        if(provider.equalsIgnoreCase("database")) {
            if(user.getId() == null || userService.findById(user.getId()) == null) {
                User savedUser = userService.save(user);
                URI uri = ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{userId}")
                        .buildAndExpand(savedUser.getId())
                        .toUri();
                return ResponseEntity.created(uri).body(savedUser);
            }else{
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User already present");
            }
        }
        else
            return ResponseEntity
                    .status(HttpStatus.METHOD_NOT_ALLOWED)
                    .body("User can't be created as provider is set to " + provider);
    }

    @PreAuthorize("#oauth2.hasAnyScope('server', 'ui')")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getAllUsers(){
        Collection<User> users = userService.findAll();
        if(users == null || users.isEmpty())
            return new ResponseEntity<>("No Users found", HttpStatus.NO_CONTENT);
        return ResponseEntity.ok(users);
    }

    private String getCurrentClientId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication instanceof OAuth2Authentication){
            OAuth2Authentication oauth = (OAuth2Authentication) authentication;
            return oauth.getOAuth2Request().getClientId();
        }
        return identityServiceName;
    }
}
