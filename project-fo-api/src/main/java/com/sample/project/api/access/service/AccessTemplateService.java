package com.sample.project.api.access.service;

import com.sample.project.api.access.dto.ResponseTemplateDto;
import com.sample.project.api.access.entity.AccessMemberEntity;
import com.sample.project.api.access.repository.AccessMemberRepository;
import com.sample.project.api.login.type.MemberStateType;
import com.sample.project.common.utils.AuthTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessTemplateService {

    private final AuthTokenUtils authTokenUtils;

    private final AccessMemberRepository accessMemberRepository;

    private final MessageSource messageSource;

    @Transactional
    public ResponseTemplateDto authMail(String token, int type) {
        ResponseTemplateDto responseTemplateDto = new ResponseTemplateDto();
        Locale userLocale = LocaleContextHolder.getLocale();
        try {
            String userId = authTokenUtils.validateToken(token);

            if (userId == null) {
                responseTemplateDto.setResult(false);
                responseTemplateDto.setResultMessage(messageSource.getMessage("auth.token.mail.fail", null, userLocale));
                return responseTemplateDto;
            }

            if (type == 1) { // 1. 회원가입
//                AccessMemberEntity member = accessMemberRepository.findById(userId).get();
                AccessMemberEntity member = accessMemberRepository.findByUserId(userId);

                responseTemplateDto.setResult(true);
                responseTemplateDto.setUserId(userId);
                responseTemplateDto.setType(1);

                if (member.getMemberStatusCode().equals(MemberStateType.MEMBER_STATE_NORMAL.code())) { // 회원가입이 이미 완료된 상태
                    responseTemplateDto.setResultMessage(messageSource.getMessage("auth.token.mail.success.already", null, userLocale));
                    return responseTemplateDto;
                }

                member.changeMemberState(MemberStateType.MEMBER_STATE_NORMAL.code());
                responseTemplateDto.setResultMessage(messageSource.getMessage("auth.token.mail.success", null, userLocale));
                return responseTemplateDto;

            } else if (type == 2) { // 2. 비밀번호 재설정
                responseTemplateDto.setResult(true);
                responseTemplateDto.setUserId(userId);
                responseTemplateDto.setType(2);
                responseTemplateDto.setResultMessage(messageSource.getMessage("auth.token.password.change.mail.success", null, userLocale));
                responseTemplateDto.setToken(token);
                return responseTemplateDto;
            } else { // 4. 운영자 초대를 통한 회원가입
//                AccessMemberEntity member = accessMemberRepository.findById(userId).get();
                AccessMemberEntity member = accessMemberRepository.findByUserId(userId);

                responseTemplateDto.setResult(true);
                responseTemplateDto.setUserId(userId);
                responseTemplateDto.setType(4);

                if (member.getMemberStatusCode().equals(MemberStateType.MEMBER_STATE_NORMAL.code())) { // 회원가입이 이미 완료된 상태
                    responseTemplateDto.setResultMessage(messageSource.getMessage("auth.token.mail.success.already", null, userLocale));
                    return responseTemplateDto;
                }

                member.changeMemberState(MemberStateType.MEMBER_STATE_NORMAL.code());
                responseTemplateDto.setResultMessage(messageSource.getMessage("auth.token.mail.success", null, userLocale));
                return responseTemplateDto;

            }

        } catch (AuthenticationException e) {
            responseTemplateDto.setResult(false);
            responseTemplateDto.setResultMessage(messageSource.getMessage("auth.token.mail.fail", null, userLocale));
            return responseTemplateDto;
        }
    }
}