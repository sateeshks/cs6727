package com.kailas.dpm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

@Data
@Configuration
@ConfigurationProperties(prefix = "authn")
public class WebAuthnProperties {
    private String hostName;
    private String display;
    private Set<String> origin;
}
