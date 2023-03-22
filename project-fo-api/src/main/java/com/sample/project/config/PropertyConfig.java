package com.sample.project.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class PropertyConfig {

    @Value("${jwt.header}")
    private String authorizationHeader;

    @Value("${jwt.authorities-key}")
    private String authoritiesKey;

    @Value("${jwt.base64-secret}")
    private String base64Secret;

    @Value("${jwt.access-token-validity-in-seconds}")
    private long accessTokenValidityInMilliseconds;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenValidityInMilliseconds;

    @Value("${jwt.token-validity-in-seconds-for-remember-me}")
    private long tokenValidityInMillisecondsForRememberMe;

    @Value("${jwt.mail-authorities-key}")
    private String mailAuthoritiesKey;

    @Value("${jwt.mail-base64-secret}")
    private String mailBase64Secret;

    @Value("${jwt.mail-token-validity-in-seconds}")
    private long mailTokenValidityInMilliseconds;

}
