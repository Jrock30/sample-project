package com.sample.project.api.access.service;

import com.sample.project.api.access.dto.RequestAccessEntryDto;
import com.sample.project.api.access.entity.AccessAppleEntity;
import com.sample.project.api.access.entity.AccessMemberEntity;
import com.sample.project.api.access.entity.AgencyAccessHistoryEntity;
import com.sample.project.api.access.repository.AccessAppleRepository;
import com.sample.project.api.access.repository.AccessMemberRepository;
import com.sample.project.api.access.repository.AgencyAccessRepository;
import com.sample.project.api.access.utils.Apple24Validator;
import com.sample.project.common.exception.CustomException;
import com.sample.project.common.service.web.WebClientService;
import com.sample.project.common.utils.CommonUtils;
import com.sample.project.security.SecurityUtils;
import com.sample.project.security.model.CustomUserDetails;
import com.sample.project.security.type.RoleType;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;

import static com.sample.project.common.type.ResponseErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessService {

    private final AgencyAccessRepository agencyAccessRepository;

    private final WebClientService webClientService;

    private final Apple24Validator apple24Validator;

    private final MessageSource messageSource;

    private final AccessMemberRepository accessMemberRepository;

    private final AccessAppleRepository accessAppleRepository;

    /**
     * Apple24 를 통한 로그 적재 및 HMAC 유효성 검사
     */
    @Transactional
    public void entryAccess(RequestAccessEntryDto requestAccessEntryDto) {
        Locale userLocale = LocaleContextHolder.getLocale();
        if (!apple24Validator.timestampValidate(requestAccessEntryDto.getTimestamp())) {
            throw new CustomException(messageSource.getMessage("apple24.access.time.expire.error", null, userLocale), HttpStatus.FORBIDDEN); // 카페24를 통한 접근이 2시간이 지났습니다. 카페24를 통해 다시 접근해 주세요.
        }

        if (apple24Validator.hmacValidate(requestAccessEntryDto.getQueryString(), requestAccessEntryDto.getHmac())) {
            throw new CustomException(messageSource.getMessage("apple24.access.valid.error", null, userLocale), HttpStatus.UNAUTHORIZED); // // 카페24를 통한 접근이 유효하지 않습니다.
        }

        AgencyAccessHistoryEntity agencyAccessHistoryEntity = AgencyAccessHistoryEntity.builder()
                .hmac(requestAccessEntryDto.getHmac())
                .authConfig(requestAccessEntryDto.getAuth_config())
                .isMultiShop(requestAccessEntryDto.getIs_multi_shop())
                .lang(requestAccessEntryDto.getLang())
                .mallId(requestAccessEntryDto.getMall_id())
                .nation(requestAccessEntryDto.getNation())
                .shopNo(Integer.parseInt(requestAccessEntryDto.getShop_no()))
                .timestamp(Long.parseLong(requestAccessEntryDto.getTimestamp()))
                .userId(requestAccessEntryDto.getUser_id())
                .userName(requestAccessEntryDto.getUser_name())
                .userType(requestAccessEntryDto.getUser_type())
                .queryString(requestAccessEntryDto.getQueryString())
                .build();

        agencyAccessRepository.save(agencyAccessHistoryEntity);
    }

    /**
     * Apple24 접근 후 Root 패스에서 Hmac을 다시 올려 검증하고 Apple24 접근 히스토리 조회 후 Apple24 회원정보 등록
     *
     * 파라메터로 hmac 이 있을 경우 (Apple24 접근) 히스토리가 적재 되어 있으니, 히스토리 조회를 통해 hmac 유효성 검증 후 Apple24정보 회원에 등록
     */
//    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @Transactional(rollbackFor = Exception.class)
    public boolean hmacValidAndApple24InfoRegit(String hmac) {

        Locale userLocale = LocaleContextHolder.getLocale();

        if (!hmac.contains("%")) {
            hmac = URLEncoder.encode(hmac, StandardCharsets.UTF_8);
        }

        AgencyAccessHistoryEntity agencyAccessHistoryEntity =
                agencyAccessRepository.searchAccessHistory(hmac).orElseThrow(
                        () -> new CustomException(FAIL_400.message(), HttpStatus.BAD_REQUEST));
//                        () -> new CustomException(messageSource.getMessage("apple24.access.valid.error", null, userLocale), HttpStatus.BAD_REQUEST));

        if (!apple24Validator.timestampValidate(String.valueOf(agencyAccessHistoryEntity.getTimestamp()))) {
            return false; // 요청한 곳에서 처리
//            throw new CustomException(messageSource.getMessage("apple24.access.time.expire.error", null, userLocale), HttpStatus.FORBIDDEN); // 카페24를 통한 접근이 2시간이 지났습니다. 카페24를 통해 다시 접근해 주세요.
        }

        if (apple24Validator.hmacValidate(agencyAccessHistoryEntity.getQueryString(), agencyAccessHistoryEntity.getHmac())) {
            throw new CustomException(messageSource.getMessage("apple24.access.valid.error", null, userLocale), HttpStatus.UNAUTHORIZED); // // 카페24를 통한 접근이 유효하지 않습니다.
        }

        String currentUserId = SecurityUtils.getCurrentUserId().get();

        AccessMemberEntity accessMemberEntity = accessMemberRepository.findById(currentUserId).get();

        String role;
        var grantedAuthorities = new ArrayList<GrantedAuthority>();

        if (agencyAccessHistoryEntity.getUserType().equals("P")) { // 대표 사용자 (점주)
            role = RoleType.MASTER.code();
            grantedAuthorities.add(new SimpleGrantedAuthority(RoleType.MASTER.code()));
        } else {
            role = RoleType.USER.code();
            grantedAuthorities.add(new SimpleGrantedAuthority(RoleType.USER.code()));
        }

        if (ObjectUtils.isEmpty(accessMemberEntity.getAgencyId())) {
            String mallName = "";
            try {
                JsonNode jsonNode = webClientService.getApple24JsonAuth("/api/v2/admin/store", agencyAccessHistoryEntity.getMallId()); // 몰 이름 조회
                mallName = jsonNode.get("store").get("shop_name").toString().replaceAll("\"", "");
            } catch (IOException e) {
                CommonUtils.getPrintStackTrace(e);
            }

            accessMemberEntity.getLoginMemberPermissionEntity().changePermissionId(role);
            accessMemberEntity.changeAgencyInfo(agencyAccessHistoryEntity.getMallId(), mallName, agencyAccessHistoryEntity.getUserName(), agencyAccessHistoryEntity.getUserId(), role);
        }

        // Security Update
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(currentUserId);
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        userDetails.setAgencyId(agencyAccessHistoryEntity.getUserId());
        userDetails.setMallId(agencyAccessHistoryEntity.getMallId());
        userDetails.setAgencyUserName(agencyAccessHistoryEntity.getUserName());
        userDetails.setMallName(accessMemberEntity.getMallName());

//        Authentication newAuth = new UsernamePasswordAuthenticationToken(userDetails, auth.getCredentials(), grantedAuthorities);
        Authentication newAuth = new UsernamePasswordAuthenticationToken(userDetails, userDetails, grantedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        return true;
    }

    /**
     * 봇 생성 시 맨 처음에 넣을 것
     * 봇을 만들 때 현재 회원이 카페24 정보를 가지고 있지 않으면 CustomException 발생 (봇 생성 x)
     * AND
     * HMAC 으로 Apple24 데이터를 넣을 시 Mall Id 가 기존에 있다면 에러 발생 (봇 ID당 몰 ID 가 하나만 있어야 한다.)
     *
     */
    @Transactional
    public void validateCreateBotHmac() {

        String currentUserId = SecurityUtils.getCurrentUserId().orElseThrow(() -> new CustomException(FAIL_4006.message(), HttpStatus.UNAUTHORIZED));

        Optional<AccessMemberEntity> accessMemberEntity = accessMemberRepository.findById(currentUserId);

        if (accessMemberEntity.isPresent()) {

            String role = accessMemberEntity.get().getLoginMemberPermissionEntity().getPermissionGroupId();

            if (!role.equals(RoleType.MASTER.code())) { // 대표 운영자 여부
                throw new CustomException(FAIL_4016.message(), HttpStatus.FORBIDDEN);
            }

            if (ObjectUtils.isEmpty(accessMemberEntity.get().getAgencyId())) { // Apple24 아이디가 없으면 Apple24를 통한 접근 Exception
                throw new CustomException(FAIL_4004.message(), HttpStatus.FORBIDDEN);
            } else {
                Optional<AccessAppleEntity> accessAppleEntity = accessAppleRepository.searchApple(accessMemberEntity.get().getMallId());

                if (accessAppleEntity.isPresent()) { // 몰 아이디로 등록된 챗봇이 있다면 Exception
                    String userId = accessAppleEntity.get().getAccessApplePermissionEntity().getUserId();
                    throw new CustomException("이미 Apple24 몰 ID 로 등록 된 봇이 있습니다. { " + userId + " }", HttpStatus.BAD_REQUEST);
                }
            }
        } else {
            throw new CustomException(FAIL_4004.message(), HttpStatus.FORBIDDEN);
        }
    }
}