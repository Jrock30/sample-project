package com.sample.project.api.login.service;

import com.sample.project.api.access.service.AccessService;
import com.sample.project.api.login.dto.*;
import com.sample.project.api.login.entity.LoginMemberEntity;
import com.sample.project.api.login.repository.LoginRepository;
import com.sample.project.common.dto.MailAuthDto;
import com.sample.project.common.exception.CustomException;
import com.sample.project.common.service.email.EmailService;
import com.sample.project.common.type.ResponseErrorCode;
import com.sample.project.common.utils.AuthTokenUtils;
import com.sample.project.common.utils.CommonUtils;
import com.sample.project.common.utils.EncryptUtils;
import com.sample.project.common.utils.StringUtils;
import com.sample.project.common.wrapper.CommonSuccessResponse;
import com.sample.project.config.PropertyConfig;
import com.sample.project.security.SecurityUtils;
import com.sample.project.security.jwt.JWTProvider;
import com.sample.project.security.model.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Locale;

import static com.sample.project.common.type.ResponseErrorCode.*;
import static com.sample.project.security.type.RoleType.MASTER;
import static com.sample.project.security.type.RoleType.USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final LoginRepository loginRepository;

    private final JWTProvider jwtProvider;

    private final PropertyConfig propertyConfig;

    private final EmailService emailService;

    private final MessageSource messageSource;

    private final AuthTokenUtils authTokenUtils;

    private final AccessService accessService;

    @Value("${template.mail.url}")
    private String mailTemplateUrl;

    /**
     * 로그인
     */
    @Transactional(noRollbackFor = BadCredentialsException.class)
    public ResponseEntity<?> login(RequestLoginDto requestLoginDto) {

        String enPassword = EncryptUtils.passwordEncrypt(requestLoginDto.getPassword());

        LoginMemberEntity member = this.loginRepository.searchMember(requestLoginDto.getUserId()).orElseThrow(
                () -> new BadCredentialsException(ResponseErrorCode.FAIL_4002.message()));

        String permissionGroupId = member.getLoginMemberPermissionEntity().getPermissionGroupId();
        if (!permissionGroupId.equals(USER.code()) && !permissionGroupId.equals(MASTER.code())) {
            throw new BadCredentialsException(ResponseErrorCode.FAIL_4002.message());
        }

        // 로그인 시도 30분 안에 5회 시도하였을 시 실패
        if (member.getLoginFailCount() > 4) { // 로그인 실패 5번 이상 체크
            if (timestampValidate(member.getLoginFailDate())) { // 로그인 시도 30분 체크
                throw new BadCredentialsException(ResponseErrorCode.FAIL_4027.message());
            } else {
                member.resetLoginFailCount(); // 로그인 실패 횟수 리셋
            }
        }

        // 비밀번호가 틀렸을 시 로그인 실패 횟수 증가
        if (!member.getPassword().equals(enPassword)) {
            member.addLoginFailCount();
            throw new BadCredentialsException(ResponseErrorCode.FAIL_4002.message());
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(requestLoginDto.getUserId(), enPassword);

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResponseTokenDto tokenInfo = jwtProvider.createToken(authentication);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(propertyConfig.getAuthorizationHeader(), "Bearer " + tokenInfo.getAccessToken());

        LoginMemberDto responseLoginDto = null;

        if (SecurityUtils.getUserDetails().isPresent()) {
            CustomUserDetails userDetails = (CustomUserDetails) SecurityUtils.getUserDetails().get();

            responseLoginDto = LoginMemberDto.builder()
                    .userId(CommonUtils.maskingUserId(userDetails.getUserId()))
                    .mobile(CommonUtils.maskingPhoneNumber(userDetails.getMobile()))
                    .termsTypeCode(userDetails.getTermsTypeCode())
                    .mailAuthYn(userDetails.getMailAuthYn())
                    .mailAuthCount(userDetails.getMailAuthCount())
                    .loginFailCount(userDetails.getLoginFailCount())
                    .memberStatusCode(userDetails.getMemberStatusCode())
                    .joinDate(userDetails.getJoinDate())
                    .lastLoginDate(userDetails.getLastLoginDate())
                    .regDate(userDetails.getRegDate())
                    .updDate(userDetails.getUpdDate())
                    .grantType(String.valueOf(userDetails.getAuthorities().stream().findFirst().orElse(null)))
                    .accessToken(tokenInfo.getAccessToken())
                    .build();
        }

        if (StringUtils.isNotEmpty(requestLoginDto.getHmac())) { // hmac 이 있으면 hmac 검증 후 apple24 정보 저장
            accessService.hmacValidAndApple24InfoRegit(requestLoginDto.getHmac());
        }
        member.resetLoginFailCount(); // 로그인 실패 횟수 리셋
        member.changeLastLogin(); // 최근 로그인 일자 업데이트

        return new ResponseEntity<>(new CommonSuccessResponse<>(responseLoginDto), httpHeaders, HttpStatus.OK);
    }

    /**
     * 회원가입
     */
    @Transactional
    public void signup(RequestSignupDto requestSignupDto) {

        Locale userLocale = LocaleContextHolder.getLocale();

        if (!requestSignupDto.getPasswordConfirm().equals(requestSignupDto.getPassword())) { // 비밀번호 체크
            throw new CustomException(FAIL_4009.message(), HttpStatus.BAD_REQUEST);
        }

        if (loginRepository.findByUserId(requestSignupDto.getUserId()).isPresent()) { // 아이디가 있으면
            throw new CustomException(messageSource.getMessage("member.email.duplicated", null, userLocale), HttpStatus.BAD_REQUEST); // 사용할 수 없는 이메일 주소 입니다. 이메일 주소를 확인한 다음 다시 입력해 주세요.
        }

        requestSignupDto.setPassword(EncryptUtils.passwordEncrypt(requestSignupDto.getPassword()));
        loginRepository.save(requestSignupDto.toEntity());

        final String subject = "[ 다날 ] 회원가입을 위해 메일을 인증해주세요.";    // 메일 제목
        final String template = "auth-mail-template.html";  // 리포트 전송 템플릿

        String token = authTokenUtils.createToken(requestSignupDto.getUserId());
//        String uri = String.format("http://localhost:8080/api/v1/access/template/auth-mail?type=%s&token=%s", 1, token);
        String uri = String.format(mailTemplateUrl, 1, token);

        MailAuthDto mailAUth = MailAuthDto.builder()
                .userId(requestSignupDto.getUserId())
                .token(token) // 토큰 유효시간 3분
                .uri(uri)
                .type(1) // 1.회원가입, 2. 비밀번호 재설정, 3.비회원 운영자 초대, 4.회원 운영자 초대
                .build();

        if (StringUtils.isNotEmpty(requestSignupDto.getHmac())) { // hmac 이 있으면 hmac 검증 후 apple24 정보 저장
            accessService.hmacValidAndApple24InfoRegit(requestSignupDto.getHmac());
        }
        emailService.sendMail(subject, requestSignupDto.getUserId(), template, mailAUth);
    }

    /**
     * 아이디 중복 체크
     */
    public boolean checkDuplicatedUserId(String userId) {
        return loginRepository.findByUserId(userId).isPresent();
    }

    /**
     * 비밀번호 변경
     */
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(RequestChangePasswordDto requestChangePasswordDto) {
        String userId;

        try {
            userId = authTokenUtils.validateToken(requestChangePasswordDto.getToken());
            if (ObjectUtils.isEmpty(userId)) {
                throw new CustomException(FAIL_4015.message(), HttpStatus.BAD_REQUEST); // 인증토큰이 유효하지 않습니다. 다시 요청해주세요.
            }
        } catch (AuthenticationException e) {
            throw new CustomException(FAIL_4015.message(), HttpStatus.BAD_REQUEST); // 인증토큰이 유효하지 않습니다. 다시 요청해주세요.
        }

        if (!requestChangePasswordDto.getPasswordConfirm().equals(requestChangePasswordDto.getPassword())) { // 비밀번호 체크
            throw new CustomException(FAIL_4009.message(), HttpStatus.BAD_REQUEST);
        }

        LoginMemberEntity loginMemberEntity = loginRepository.findByUserId(userId).orElseThrow(
                () -> new CustomException(FAIL_4007.message(), HttpStatus.BAD_REQUEST));

        loginMemberEntity.changePassword(EncryptUtils.passwordEncrypt(requestChangePasswordDto.getPassword()));
    }

    private boolean timestampValidate(LocalDateTime time) {
        long now = Timestamp.valueOf(LocalDateTime.now()).getTime() / 1000;
        long diffTime = now - (Timestamp.valueOf(time).getTime() / 1000);

        // ±30분 이내의 요청만 허용
        return Math.abs(diffTime) < 1800;
    }
}
