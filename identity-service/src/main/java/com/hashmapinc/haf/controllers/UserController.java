package com.hashmapinc.haf.controllers;

import com.hashmapinc.haf.models.User;
import com.hashmapinc.haf.models.UserInformation;
import com.hashmapinc.haf.services.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.Collection;

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
            if(userService.findById(user.getId()) != null) {
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

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<?> getUserById(@PathVariable String userId){
        User user = userService.findById(userId);
        if(user == null)
            return new ResponseEntity<>("No User found with id "+ userId, HttpStatus.NO_CONTENT);
        return ResponseEntity.ok(user);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getAllUsers(){
        Collection<User> users = userService.findAll();
        if(users == null || users.isEmpty())
            return new ResponseEntity<>("No Users found", HttpStatus.NO_CONTENT);
        return ResponseEntity.ok(users);
    }
}
