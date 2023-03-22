package com.sample.project.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class JWTConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final JWTProvider JWTProvider;

    private final ObjectMapper objectMapper;

    @Override
    public void configure(HttpSecurity http) {
        JWTFilter customFilter = new JWTFilter(JWTProvider, objectMapper);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
