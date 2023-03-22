package com.sample.project.common.utils;

import com.sample.project.config.PropertyConfig;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthTokenUtils {

    private final PropertyConfig propertyConfig;

    public String createToken(String userId) {
        byte[] keyBytes = Decoders.BASE64.decode(propertyConfig.getMailBase64Secret());
        Key key = Keys.hmacShaKeyFor(keyBytes);

        long now = (new Date()).getTime();

        return Jwts.builder()
                .setSubject(userId)
                .claim("userId", userId)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(new Date(now + propertyConfig.getMailTokenValidityInMilliseconds()))
                .compact();
    }

    public String validateToken(String authToken) throws AuthenticationException {
        byte[] keyBytes = Decoders.BASE64.decode(propertyConfig.getMailBase64Secret());
        Key key = Keys.hmacShaKeyFor(keyBytes);

        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken).getBody().getSubject();
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException | IllegalArgumentException |
                 UnsupportedJwtException e) {
            log.debug("JWT 잘못된 형식: {}", CommonUtils.getPrintStackTrace(e));
            return null;
        } catch (ExpiredJwtException e) {
            log.debug("JWT 유효기간 만료: {}", CommonUtils.getPrintStackTrace(e));
            return null;
        }
    }

    public Long getExpiration(String accessToken) {
        byte[] keyBytes = Decoders.BASE64.decode(propertyConfig.getMailBase64Secret());
        Key key = Keys.hmacShaKeyFor(keyBytes);

        Date expiration = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(accessToken).getBody().getExpiration();
        Long now = new Date().getTime();
        return (expiration.getTime() - now);
    }
}
