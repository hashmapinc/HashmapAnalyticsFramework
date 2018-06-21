package com.hashmapinc.haf.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.hashmapinc.haf.models.ActivationType;
import com.hashmapinc.haf.models.User;
import com.hashmapinc.haf.models.UserCredentials;
import com.hashmapinc.haf.requests.ActivateUserRequest;
import com.hashmapinc.haf.requests.CreateUserRequest;
import com.hashmapinc.haf.requests.CreateUserResponse;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.security.Principal;
import java.util.Collection;
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
    public ResponseEntity<?> save(@RequestBody CreateUserRequest userRequest, HttpServletRequest request){
        String clientId = getCurrentClientId();
        User user = userRequest.getUser();
        if(provider.equalsIgnoreCase("database")) {
            if(user.getId() == null || userService.findById(user.getId()) == null) {
                if(user.getClientId() == null){
                    user.setClientId(clientId);
                }
                if(!userRequest.getActivationType().equals(ActivationType.NONE)){
                    user.setEnabled(false);
                }else{
                    if(userRequest.getCredentials() == null || StringUtils.isEmpty(userRequest.getCredentials().getPassword())){
                        return ResponseEntity.badRequest().body("Password can't be null");
                    }
                }
                User savedUser = userService.save(user);
                UserCredentials savedCredentials = userService.findCredentialsByUserId(savedUser.getId());
                if(userRequest.getCredentials() != null && !StringUtils.isEmpty(userRequest.getCredentials().getPassword())){
                    savedCredentials.setPassword(userRequest.getCredentials().getPassword());
                    userService.saveUserCredentials(savedCredentials);
                }
                URI uri = ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{userId}")
                        .buildAndExpand(savedUser.getId())
                        .toUri();
                return ResponseEntity.created(uri).body(new CreateUserResponse(savedUser, savedCredentials.getActivationToken()));
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
    @RequestMapping(value = "/user-credentials", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> save(@RequestBody UserCredentials userCredentials, HttpServletRequest request){
          UserCredentials savdUserCredentials = userService.saveUserCredentials(userCredentials);
          return ResponseEntity.ok(savdUserCredentials);
    }

    @PreAuthorize("#oauth2.hasAnyScope('server', 'ui')")
    @RequestMapping(value = "/{userId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@PathVariable UUID userId, @RequestBody User user){
        String clientId = getCurrentClientId();
        if(provider.equalsIgnoreCase("database")) {
            if(userId != null && userService.findById(userId) != null) {
                if(user.getClientId() == null){
                    user.setClientId(clientId);
                }
                user.setId(userId);
                User savedUser = userService.save(user);
                return ResponseEntity.ok(savedUser);
            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found for given id");
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
        Collection<User> users = userService.findAllByClientId(getCurrentClientId());
        if(users == null || users.isEmpty())
            return new ResponseEntity<>("No Users found", HttpStatus.NO_CONTENT);
        return ResponseEntity.ok(users);
    }

    @PreAuthorize("#oauth2.hasAnyScope('server', 'ui')")
    @RequestMapping(value = "/{userId}/activation-token", method = RequestMethod.GET)
    public ResponseEntity<?> getActivationToken(@PathVariable UUID userId){
        UserCredentials credentials = userService.findCredentialsByUserId(userId);
        if(credentials == null)
            return new ResponseEntity<>("No Token found", HttpStatus.NO_CONTENT);
        return ResponseEntity.ok(credentials.getActivationToken());
    }

    @PreAuthorize("#oauth2.hasAnyScope('server', 'ui')")
    @RequestMapping(value = "/activate", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<?> activateUserCredentials(@RequestBody ActivateUserRequest activateUserRequest,
                                                               HttpServletRequest request){
        UserCredentials credentials = userService.activateUserCredentials(activateUserRequest);
        if(credentials == null)
            return new ResponseEntity<>("No Token found", HttpStatus.NO_CONTENT);
        return ResponseEntity.ok(credentials);
    }


    @PreAuthorize("#oauth2.hasAnyScope('server', 'ui')")
    @RequestMapping(value = "/{resetToken}/user-credentials", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<?> findUserCredentialsByResetToken(@PathVariable String resetToken){
        UserCredentials credentials = userService.findUserCredentialsByResetToken(resetToken);
        if(credentials == null)
            return new ResponseEntity<>("No User Credentials found", HttpStatus.NO_CONTENT);
        return ResponseEntity.ok(credentials);
    }


    @PreAuthorize("#oauth2.hasAnyScope('server', 'ui')")
    @RequestMapping(value = "/resetPasswordByEmail", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<?> requestPasswordRequest(@RequestBody JsonNode resetPasswordByEmailRequest,
                                                    HttpServletRequest request){
        String email = resetPasswordByEmailRequest.get("email").asText();
        String clientId = getCurrentClientId();
        UserCredentials userCredentials = userService.requestPasswordReset(email, clientId);
        return ResponseEntity.ok(userCredentials);
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
