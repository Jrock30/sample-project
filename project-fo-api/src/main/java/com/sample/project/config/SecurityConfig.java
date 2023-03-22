package com.sample.project.config;

import com.sample.project.security.JwtAccessDeniedHandler;
import com.sample.project.security.JwtAuthenticationEntryPoint;
import com.sample.project.security.jwt.JWTConfigurer;
import com.sample.project.security.jwt.JWTProvider;
import com.sample.project.security.type.RoleType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;

import static org.springframework.http.HttpMethod.*;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JWTProvider JWTProvider;

    private final ObjectMapper objectMapper;

    private final JwtAuthenticationEntryPoint authenticationErrorHandler;

    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(
                "/v3/api-docs", "/configuration/ui",
                "/swagger-resources", "/configuration/security",
                "/swagger-ui.html", "/webjars/**", "/swagger/**",
                "/swagger-ui/**", "/api-docs", "/api/api-docs", "/v3/api-docs/swagger-config"
        );
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .httpBasic().disable()
                .csrf().disable()
                .addFilterBefore(corsFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(authenticationErrorHandler)
                .accessDeniedHandler(jwtAccessDeniedHandler)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/v1/sample/super").hasAnyAuthority(RoleType.SUPER.code())
                .antMatchers(
                        "/v1/admin/**"
//                        , "/api/v1/admin/**"
                )
                .hasAnyAuthority(RoleType.SUPER.code(), RoleType.ADMIN.code())
                .antMatchers("/v1/sample/master").hasAnyAuthority(RoleType.SUPER.code(), RoleType.ADMIN.code(), RoleType.MASTER.code())
                .antMatchers(
                        "/v1/user/**"
//                        , "/api/v1/user/**"
                )
                .hasAnyAuthority(RoleType.SUPER.code(), RoleType.ADMIN.code(), RoleType.MASTER.code(), RoleType.USER.code())
                .antMatchers(
                        "/v1/login/**"
                        , "/v1/auth/**"
                        , "/v1/access/**"
                ).permitAll()
                .anyRequest().authenticated()
                .and()
                .apply(new JWTConfigurer(JWTProvider, objectMapper));
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setAllowedMethods(Arrays.asList(HEAD.name(), GET.name(), POST.name(), PUT.name(), DELETE.name(), OPTIONS.name()));
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return new CorsFilter(source);
    }

}
