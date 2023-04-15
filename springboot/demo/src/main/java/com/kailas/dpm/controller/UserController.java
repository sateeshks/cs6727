package com.kailas.dpm.controller;

import com.kailas.dpm.domain.*;
import com.kailas.dpm.entities.AppUser;
import com.kailas.dpm.entities.UserProfile;
import com.kailas.dpm.repo.UserProfileRepo;
import com.kailas.dpm.repo.UserRepository;
import com.kailas.dpm.service.PasswordGenerator;
import com.kailas.dpm.util.TokenUtil;
import com.nimbusds.jose.JOSEException;
import io.micrometer.common.util.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserProfileRepo userProfileRepo;

    private final UserRepository userRepo;

    public UserController(UserProfileRepo userProfileRepo, UserRepository userRepo) {
        this.userProfileRepo=userProfileRepo;
        this.userRepo = userRepo;
    }


    @GetMapping
    public List<UserProfile> getUser(String name, String email) {
        //TODO Validate email
        AppUser user =userRepo.findByEmail(email);
        return userProfileRepo.findByUser(user);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping(path="/password",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity  generatePassword(@RequestBody UserProfile userProfile,@RequestHeader HttpHeaders header) throws URISyntaxException {
        if(    !validate(userProfile))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        String authToken = header.getFirst(HttpHeaders.AUTHORIZATION);
        AppUser user = getAppUser(authToken);
        userProfile.setUser(user);
        //Persist the new profile - TODO check revison if new , just insert if old, create new record with new revison
        UserProfile dbProfile = getDbProfiles(userProfile.getDomain(),authToken);
        //horrible logic code, need to cleanaup
        if(null!= dbProfile ) {
            if (!userProfile.equals(dbProfile) || userProfile.getRevision() == (dbProfile.getRevision() + 1)) {
                //profile present but not matching , so bump version and insert new record
                userProfile.setRevision(dbProfile.getRevision() + 1);
                userProfile = userProfileRepo.save(userProfile);
            }else{
                //Client sending old revsion as ui objects are dffieretn for profile and revision
                // ..(should be handled in client ,but for now handling in server)
                userProfile =dbProfile;
            }

        }else {
            //First time domain used and hence inserting record
            userProfile = userProfileRepo.save(userProfile);
        }

        String result= null;
        try {
            result =PasswordGenerator.generate_password(userProfile);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (PasswordGenerator.ExcludeAllCharsAvailable e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        Map<String,String> resultMap = new HashMap<>();
        resultMap.put("password",result);
        resultMap.put("revision",userProfile.getRevision()+"");
        return ResponseEntity.ok(resultMap);
        //return ResponseEntity.ok(Map.of("password",result));
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping(path="/profile",
           produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity  getProfile(@RequestParam String domain, @RequestHeader HttpHeaders header) throws URISyntaxException {
        String authToken = header.getFirst(HttpHeaders.AUTHORIZATION);
        Map result= new HashMap();
        UserProfile profile = getDbProfiles(domain, authToken);
        if(null!=profile){
            getProfileMap(domain, result, profile);
        }else{
            //Get default saved profile if exist or give universal default
            profile =getDbProfiles("default",authToken);
            getProfileMap(domain, result, profile!=null?profile:new UserProfile());
        }
        System.out.println(" in side get  Profile");
        return ResponseEntity.ok(result);
    }

    private void getProfileMap(String domain, Map result, UserProfile profile) {
        result.put("domain", domain);
        result.put("lowerCase", profile.isLowerCase());
        result.put("upperCase", profile.isUpperCase());
        result.put("digits", profile.isDigits());
        result.put("symbols", profile.isSymbols());
        result.put("exclude", profile.getExclude());
        result.put("revision", profile.getRevision());
        result.put("length", profile.getLength());
    }

    private UserProfile getDbProfiles(String domain, String authToken) {
        AppUser user = getAppUser(authToken);
        List<UserProfile> profiles = userProfileRepo.findByUser(user);
        if(!profiles.isEmpty()) {
            profiles =profiles.stream().filter(t->t.getDomain().equals(domain.trim())).collect(Collectors.toList());
            if(!profiles.isEmpty()) {
                if (domain.equals("default"))
                    return profiles.get(0);
                else {
                   return profiles.stream().max(Comparator.comparing(UserProfile::getRevision))
                            .orElseThrow(NoSuchElementException::new);
                }
            }
        }
        return null;
    }



    private AppUser getAppUser(String authToken) {
        authToken = authToken.replace("Bearer ","");
        String username = null;
        try {
            username = TokenUtil.parseToken(authToken);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        //ByteArray userHandle = new ByteArray(userHandleBytes.getBytes());
        AppUser user =userRepo.findByName(username);
        return user;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping(path="/saveprofile",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity  saveProfile(@RequestBody UserProfile profile,@RequestHeader HttpHeaders header) throws URISyntaxException {
        String authToken = header.getFirst(HttpHeaders.AUTHORIZATION);
        AppUser user = getAppUser(authToken);
        profile.setUser(user);
        System.out.println(" in side save  Profile");
        UserProfile dbProfile = getDbProfiles(profile.getDomain(),authToken);
        //logic duplicate , need externlize and re use
        if(null!= dbProfile ) {
            if (!profile.equals(dbProfile)) {
                profile.setCreatedDateTime(dbProfile.getCreatedDateTime());
                if (dbProfile.getDomain().equals("default")) {
                    profile.setP_id(dbProfile.getP_id());
                } else {
                    //other than default profile we always increment the revision and insert new record
                    profile.setRevision(dbProfile.getRevision() + 1);
                }
            }
        }
        profile =userProfileRepo.save(profile);
        Map<String,String> result = new HashMap<>();
        getProfileMap(profile.getDomain(),result,profile);
        return ResponseEntity.ok(result);
    }

     private User translate(UserProfile profile){

         User u= new User();
      /*  u.setEmail(profile.getUser().getEmail());
        u.setName(profile.getName());
        u.setDevices(null);*/
         PasswordProfile pp =new PasswordProfile();
        pp.setDigits(profile.isDigits());
        pp.setSymbols(profile.isSymbols());
        pp.setDomain(profile.getDomain());
        pp.setExlude(profile.getExclude());
        pp.setLength(profile.getLength());
        pp.setRevision(profile.getRevision());
        pp.setLowerCase(profile.isLowerCase());
        pp.setUpperCase(profile.isUpperCase());
       // u.setProfile(pp);
        return u;
     }

     private Boolean validate(UserProfile profile){
        if(StringUtils.isBlank(profile.getDomain())) {
             //Throw error
             return false;
         }
         if(profile.getRevision() <=0 ) {
             //Throw error
             return false;

         }
        return true;
     }

}
