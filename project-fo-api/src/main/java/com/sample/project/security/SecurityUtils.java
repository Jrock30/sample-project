package com.sample.project.security;

import com.sample.project.security.model.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
public class SecurityUtils {

    /**
     * 스프링 시큐리티 유저 정보
     *
     * @return UserDetails
     */
    public static Optional<Object> getUserDetails() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (ObjectUtils.isEmpty(authentication)) {
            log.debug("no authentication in security context found");
            return Optional.empty();
        }
        return Optional.ofNullable(authentication.getPrincipal());
    }

    /**
     * 회원 ID
     */
    public static Optional<String> getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.debug("no authentication in security context found");
            return Optional.empty();
        }

        String userId = null;
        if (authentication.getPrincipal() instanceof UserDetails) {
            CustomUserDetails springSecurityUser = (CustomUserDetails) authentication.getPrincipal();
            userId = springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            userId = (String) authentication.getPrincipal();
        }

        log.debug("found username '{}' in security context", userId);

        assert userId != null;
        if (userId.equals("anonymousUser")) {
            return Optional.empty();
        }

        return Optional.of(userId);
    }

    /**
     * 쇼핑몰 ID
     */
    public static Optional<String> getMallId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.debug("no authentication in security context found");
            return Optional.empty();
        }

        String mallId = null;
        if (authentication.getPrincipal() instanceof UserDetails) {
            CustomUserDetails springSecurityUser = (CustomUserDetails) authentication.getPrincipal();
            mallId = springSecurityUser.getMallId();
        } else if (authentication.getPrincipal() instanceof String) {
            mallId = (String) authentication.getPrincipal();
        }

        log.debug("found mallId '{}' in security context", mallId);

        if (ObjectUtils.isEmpty(mallId)) {
            return Optional.empty();
        }
        return Optional.of(mallId);
    }

    /**
     * 쇼핑몰 이름
     */
    public static Optional<String> getMallName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.debug("no authentication in security context found");
            return Optional.empty();
        }

        String mallName = null;
        if (authentication.getPrincipal() instanceof UserDetails) {
            CustomUserDetails springSecurityUser = (CustomUserDetails) authentication.getPrincipal();
            mallName = springSecurityUser.getMallName();
        } else if (authentication.getPrincipal() instanceof String) {
            mallName = (String) authentication.getPrincipal();
        }

        log.debug("found mallName '{}' in security context", mallName);

        if (ObjectUtils.isEmpty(mallName)) {
            return Optional.empty();
        }
        return Optional.of(mallName);
    }

    /**
     * Apple24 ID
     */
    public static Optional<String> getAgencyId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.debug("no authentication in security context found");
            return Optional.empty();
        }

        String agencyId = null;
        if (authentication.getPrincipal() instanceof UserDetails) {
            CustomUserDetails springSecurityUser = (CustomUserDetails) authentication.getPrincipal();
            agencyId = springSecurityUser.getAgencyId();
        } else if (authentication.getPrincipal() instanceof String) {
            agencyId = (String) authentication.getPrincipal();
        }

        log.debug("found agencyId '{}' in security context", agencyId);

        if (ObjectUtils.isEmpty(agencyId)) {
            return Optional.empty();
        }
        return Optional.of(agencyId);
    }

    /**
     * Apple24 회원명
     */
    public static Optional<String> getAgencyUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.debug("no authentication in security context found");
            return Optional.empty();
        }

        String agencyUsername = null;
        if (authentication.getPrincipal() instanceof UserDetails) {
            CustomUserDetails springSecurityUser = (CustomUserDetails) authentication.getPrincipal();
            agencyUsername = springSecurityUser.getAgencyUserName();
        } else if (authentication.getPrincipal() instanceof String) {
            agencyUsername = (String) authentication.getPrincipal();
        }

        log.debug("found agencyUsername '{}' in security context", agencyUsername);

        if (ObjectUtils.isEmpty(agencyUsername)) {
            return Optional.empty();
        }
        return Optional.of(agencyUsername);
    }

    /**
     * 등록일자
     */
    public static Optional<LocalDateTime> getRegDate() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.debug("no authentication in security context found");
            return Optional.empty();
        }

        CustomUserDetails springSecurityUser = (CustomUserDetails) authentication.getPrincipal();
        LocalDateTime regDate = springSecurityUser.getRegDate();

        log.debug("found regDt '{}' in security context", regDate);

        return Optional.ofNullable(regDate);
    }

    /**
     * 핸드폰 번호
     */
    public static Optional<String> getMobile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.debug("no authentication in security context found");
            return Optional.empty();
        }

        String mobile = null;
        if (authentication.getPrincipal() instanceof UserDetails) {
            CustomUserDetails springSecurityUser = (CustomUserDetails) authentication.getPrincipal();
            mobile = springSecurityUser.getMobile();
        } else if (authentication.getPrincipal() instanceof String) {
            mobile = (String) authentication.getPrincipal();
        }

        log.debug("found mobile '{}' in security context", mobile);

        if (ObjectUtils.isEmpty(mobile)) {
            return Optional.empty();
        }

        return Optional.of(mobile);
    }

}
