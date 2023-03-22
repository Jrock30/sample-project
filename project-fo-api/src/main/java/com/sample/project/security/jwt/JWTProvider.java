package com.sample.project.security.jwt;

import com.sample.project.api.login.dto.ResponseTokenDto;
import com.sample.project.api.login.entity.LoginMemberEntity;
import com.sample.project.api.login.repository.LoginRepository;
import com.sample.project.common.exception.CustomException;
import com.sample.project.common.utils.AesUtils;
import com.sample.project.common.utils.CommonUtils;
import com.sample.project.config.PropertyConfig;
import com.sample.project.security.model.CustomUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTProvider implements InitializingBean {

    private final PropertyConfig propertyConfig;

    private final LoginRepository loginRepository;

    private Key key;

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(propertyConfig.getBase64Secret());
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public ResponseTokenDto createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(propertyConfig.getAuthoritiesKey(), authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(new Date(now + propertyConfig.getAccessTokenValidityInMilliseconds()))
                .compact();

        return ResponseTokenDto.builder()
                .accessToken(accessToken)
                .build();
    }

//    @Cacheable(cacheNames = "cacheStore", key = "#token")
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();

        LoginMemberEntity member = loginRepository.searchMember(claims.getSubject()).orElseThrow(
                () -> new CustomException(claims.getSubject() + " NOT FOUND.", HttpStatus.BAD_REQUEST));

        var grantedAuthorities = new ArrayList<GrantedAuthority>();
        String permissionGroupId = member.getLoginMemberPermissionEntity().getPermissionGroupId();
        grantedAuthorities.add(new SimpleGrantedAuthority(permissionGroupId));

        CustomUserDetails customUserDetails = new CustomUserDetails();
        customUserDetails.setUserId(member.getUserId());
        customUserDetails.setMallId(member.getMallId());
        customUserDetails.setMallName(member.getMallName());
        customUserDetails.setAgencyId(member.getAgencyId());
        customUserDetails.setAgencyUserName(member.getAgencyUserName());
        try {
            customUserDetails.setMobile(AesUtils.decrypt(member.getMobile()));
        } catch (Exception e) {
            customUserDetails.setMobile(null);
        }
        customUserDetails.setTermsTypeCode(member.getTermsTypeCode());
        customUserDetails.setMailAuthYn(member.getMailAuthYn());
        customUserDetails.setMailAuthCount(member.getMailAuthCount());
        customUserDetails.setLoginFailCount(member.getLoginFailCount());
        customUserDetails.setMemberStatusCode(member.getMemberStatusCode());
        customUserDetails.setJoinDate(member.getJoinDate());
        customUserDetails.setLastLoginDate(member.getLastLoginDate());
        customUserDetails.setRegDate(member.getRegDate());
        customUserDetails.setUpdDate(member.getUpdDate());
        customUserDetails.setAuthorities(grantedAuthorities);

        return new UsernamePasswordAuthenticationToken(customUserDetails, token, grantedAuthorities);
    }

    public boolean validateToken(String authToken) throws AuthenticationException {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException | IllegalArgumentException | UnsupportedJwtException e) {
            log.debug("JWT 잘못된 형식: {}", CommonUtils.getPrintStackTrace(e));
            throw new AuthenticationException(e.getMessage());
        } catch (ExpiredJwtException e) {
            log.debug("JWT 유효기간 만료: {}", CommonUtils.getPrintStackTrace(e));
            throw new AuthenticationException(e.getMessage());
        }
//        return false;
    }

    public Long getExpiration(String accessToken) {
        Date expiration = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(accessToken).getBody().getExpiration();
        Long now = new Date().getTime();
        return (expiration.getTime() - now);
    }
}
