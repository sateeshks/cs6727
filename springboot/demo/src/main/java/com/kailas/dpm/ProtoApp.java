package com.kailas.dpm;

import com.kailas.dpm.config.WebAuthnProperties;
import com.kailas.dpm.repo.RegistrationServiceRepo;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.data.RelyingPartyIdentity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

@EnableCaching
@SpringBootApplication
public class ProtoApp {

    public static void main(String[] args) {
        SpringApplication.run(ProtoApp.class, args);
    }

    @Bean
    @Autowired
    public RelyingParty relyingParty(RegistrationServiceRepo regisrationRepository, WebAuthnProperties properties) {
        RelyingPartyIdentity rpIdentity = RelyingPartyIdentity.builder()
                .id(properties.getHostName())
                .name(properties.getDisplay())
                .build();

        return RelyingParty.builder()
                .identity(rpIdentity)
                .credentialRepository(regisrationRepository)
                .origins(properties.getOrigin())
                .build();
    }

}

