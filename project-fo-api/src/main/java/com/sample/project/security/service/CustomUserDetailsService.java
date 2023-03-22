package com.sample.project.security.service;

import com.sample.project.api.login.entity.LoginMemberEntity;
import com.sample.project.api.login.repository.LoginRepository;
import com.sample.project.api.login.type.MemberStateType;
import com.sample.project.common.exception.CustomException;
import com.sample.project.common.type.ResponseErrorCode;
import com.sample.project.common.utils.AesUtils;
import com.sample.project.security.model.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Slf4j
@Service("userDetailsService")
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final LoginRepository loginRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String userId) {
        log.debug("Authenticating user = {}", userId);

        LoginMemberEntity member = this.loginRepository.searchMember(userId).orElseThrow(
                () -> new BadCredentialsException(ResponseErrorCode.FAIL_4002.message()));

        if (member.getMemberStatusCode().equals(MemberStateType.MEMBER_STATE_UNCERTIFIED.code())) {
            throw new CustomException(ResponseErrorCode.FAIL_4011.message(), HttpStatus.UNAUTHORIZED); // 가입이 완료되지 않았습니다.
        }

        return createSecurityUser(member);
    }

    private UserDetails createSecurityUser(LoginMemberEntity member) {
        var grantedAuthorities = new ArrayList<GrantedAuthority>();
        String permissionGroupId = member.getLoginMemberPermissionEntity().getPermissionGroupId();
        grantedAuthorities.add(new SimpleGrantedAuthority(permissionGroupId));

        CustomUserDetails customUserDetails = new CustomUserDetails();
        customUserDetails.setUserId(member.getUserId());
        customUserDetails.setMallId(member.getMallId());
        customUserDetails.setMallName(member.getMallName());
        customUserDetails.setAgencyId(member.getAgencyId());
        customUserDetails.setAgencyUserName(member.getAgencyUserName());
        customUserDetails.setPassword(passwordEncoder.encode(member.getPassword()));
        try {
            customUserDetails.setMobile(AesUtils.decrypt(member.getMobile()));
        } catch (Exception e) {
            customUserDetails.setMobile((null));
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

        return customUserDetails;
    }
}