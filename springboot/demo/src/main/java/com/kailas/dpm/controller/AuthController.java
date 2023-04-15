package com.kailas.dpm.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kailas.dpm.domain.*;
import com.kailas.dpm.entities.AppUser;
import com.kailas.dpm.entities.Authenticator;

import com.kailas.dpm.repo.RegistrationServiceRepo;
import com.kailas.dpm.repo.UserRepository;


import com.kailas.dpm.util.TokenUtil;
import com.kailas.dpm.util.Utils;
import com.nimbusds.jose.JOSEException;
import com.yubico.webauthn.*;
import com.yubico.webauthn.data.*;
import com.yubico.webauthn.exception.AssertionFailedException;
import com.yubico.webauthn.exception.RegistrationFailedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class AuthController {
    @Autowired
    private final RelyingParty relyingParty;
    @Autowired
    private final RegistrationServiceRepo service;
    @Autowired
    private final CacheManager cacheManager;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenUtil tokenUtil;

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping(path="/health",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String>  health() throws URISyntaxException {
        return ResponseEntity.ok("Sucess");
    }

    AuthController(RegistrationServiceRepo service, RelyingParty relyingPary, CacheManager cacheManager) {
        this.relyingParty = relyingPary;
        this.service = service;
        this.cacheManager = cacheManager;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping(path="/register", consumes="application/json")
    @ResponseBody
    public String newUserRegistration(
            @RequestBody InputUser user,
            HttpSession session
    ) {
         //Using email as primary key
        AppUser existingUser = service.getUserRepo().findByEmail(user.getEmail());
        if (existingUser == null) {
            DPMUserIdentity dpmUserIdentity = DPMUserIdentity.builder()
                    .name(user.getName())//using name field for email
                    .email(user.getEmail())
                    .id(Utils.generateRandom(32))
                    .build();
            AppUser saveUser = new AppUser(dpmUserIdentity);
            service.getUserRepo().save(saveUser);
            String response = null;
            try {
                response = newAuthRegistration(saveUser, session);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return response;
        } else {
            //TODO call new device registration - may be ask him to do 2MFa for new device before call  newAuthregistration
             throw new ResponseStatusException(HttpStatus.CONFLICT, "Username " + user.getName() + " already exists. Choose a new name.");
        }
    }
    private String getUserSessionKey(DPMUserIdentity identity){
        return  Utils.getHash(identity.getName()+identity.getEmail());
    }
    private String getHash(String input){
        return Utils.getHash(input);
    }
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/registerauth")
    public String newAuthRegistration(
            @RequestParam AppUser user,
            HttpSession session
    ) throws JsonProcessingException {
        AppUser existingUser = service.getUserRepo().findByHandle(user.getHandle());
        if (existingUser != null) {
            UserIdentity uUserIdentity = user.toUserIdentity();
            StartRegistrationOptions registrationOptions = StartRegistrationOptions.builder()
                    .user(uUserIdentity)
                    .build();
            PublicKeyCredentialCreationOptions registration = relyingParty.startRegistration(registrationOptions);
            //Session key is email+name
             cacheManager.getCache("userAuth").put(getUserSessionKey(user.toDPMUserIdentity()), registration.toJson());
            try {
                return registration.toCredentialsCreateJson();
            } catch (JsonProcessingException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing JSON.", e);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User " + user.getName() + " does not exist. Please register.");
        }
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping(path="/finishauth" , consumes="application/json" )
    public ResponseEntity<Authenticator> finishRegistration(
          @RequestBody FinishReg regData,
            HttpSession session
    ) {
        try {
            AppUser user = service.getUserRepo().findByName(regData.getName());
            PublicKeyCredentialCreationOptions requestOptions = null;
            try {
                String credValues =(String)  cacheManager.getCache("userAuth").get(getUserSessionKey(user.toDPMUserIdentity())).get();
                requestOptions = PublicKeyCredentialCreationOptions.fromJson(credValues);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            if (requestOptions != null) {
                PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> pkc = regData.getCredential();
                FinishRegistrationOptions options = FinishRegistrationOptions.builder()
                        .request(requestOptions)
                        .response(pkc)
                        .build();
                RegistrationResult result = relyingParty.finishRegistration(options);
                Authenticator savedAuth = new Authenticator(result, pkc.getResponse(), user, regData.getCredname());
                service.getAuthRepository().save(savedAuth);
                return ResponseEntity.ok(savedAuth);
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cached request expired. Try to register again!");
            }
        } catch (RegistrationFailedException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Registration failed.", e);
        } /*catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to save credenital, please try again!", e);
        }*/
    }
  /*  @GetMapping("/login")
    public String loginPage() {
        return "login";
    }*/

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping(path="/saveprofile" , consumes="application/json" )
    public ResponseEntity<Authenticator> saveProfile(){
        return null;
     }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<String> startLogin(
            @RequestBody Map<String,String> loginData,
            HttpSession session
    ) {
        try {
            AppUser user = userRepository.findAppUserByEmail(loginData.get("email)"));
           /* if(null== user){
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body("{\"error\": \"User or email not found\"}");
            }*/
            AssertionRequest request = relyingParty.startAssertion(StartAssertionOptions.builder()
                    .username(loginData.get("username"))
                    .build());
            if (request.getPublicKeyCredentialRequestOptions().getAllowCredentials().orElse(List.of()).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body("{\"error\": \"User not found\"}");
            }
            cacheManager.getCache("userAuth").put(getHash(loginData.get("username")), request.toJson());
            cacheManager.getCache("userDetail").put(getHash(loginData.get("username")),loginData.get("email"));
            //session.setAttribute(loginData.get("username"), request.toJson());
            return ResponseEntity.ok(request.toCredentialsGetJson());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \""+ e.getMessage() +"\"}");
        }
    }


/*
    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        // Invalidate the session
        session.invalidate();
        return ResponseEntity.ok().build();

          @RequestParam String credential,
            @RequestParam String username,
    }*/
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/welcome")
    public ResponseEntity finishLogin(
            @RequestBody Map<String,String> credentialaData,
            Model model,
            HttpSession session
    ) {
        try {
            String userName=credentialaData.get("username");
            PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> pkc;
            pkc = PublicKeyCredential.parseAssertionResponseJson(credentialaData.get("credential"));
            String assertionRequestJson =(String)  cacheManager.getCache("userAuth").get(getHash(userName)).get();
            AssertionRequest request = AssertionRequest.fromJson(assertionRequestJson);
            AssertionResult result = relyingParty.finishAssertion(FinishAssertionOptions.builder()
                    .request(request)
                    .response(pkc)
                    .build());
            if (result.isSuccess()) {
                //UUID sessionId = UUID.randomUUID();
                AppUser user =userRepository.findByName(credentialaData.get("username"));
                String token =tokenUtil.generateToken(user);
               // cacheManager.getCache("userSession").put(token, user.getHandle().getBase64());
                //session.setAttribute(sessionId.toString(), token);
                Map <String,String> responseToken = new HashMap<>();
                responseToken.put("token",token);
               return ResponseEntity.ok(responseToken);
            } else {
                return  ResponseEntity.status(401).build();
            }
        } catch (IOException e) {
            throw new RuntimeException("Authentication failed", e);
        } catch (AssertionFailedException e) {
            throw new RuntimeException("Authentication failed", e);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }

    }
    @GetMapping("/welcome")
    public String pwdgenPage() {
        return "pwdgen";
    }

    @PostMapping("/logout")
    public String destroySession(HttpServletRequest request) {
        //cacheManager.getCache("userSession").remove the value of user token
        request.getSession().invalidate();
        return "redirect:/";
    }

   /* @PostMapping("/evict")
    public String evictCache() {
        service.evictCache();
        return "redirect:/";
    }

    @Autowired
    public SpringSessionController(ImportantService service) {
        this.service = service;
    }*/
}
