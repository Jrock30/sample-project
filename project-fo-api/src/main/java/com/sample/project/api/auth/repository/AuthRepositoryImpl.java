package com.sample.project.api.auth.repository;

import com.sample.project.api.auth.dto.BizTalkAuthNumberDto;
import com.sample.project.api.auth.type.AuthCodeType;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import static com.sample.project.api.auth.entity.QAuthManagementEntity.authManagementEntity;

@Slf4j
@RequiredArgsConstructor
public class AuthRepositoryImpl implements AuthRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<BizTalkAuthNumberDto> searchAuthCodeCreateDateTime(String userId, String code) {
        if (AuthCodeType.AUTH_TYPE_MOBILE.code().equals(code)) { // 모바일 인증
            return Optional.ofNullable(jpaQueryFactory.select(Projections.constructor(BizTalkAuthNumberDto.class
                            , authManagementEntity.mobileAuthCode
                            , authManagementEntity.mobileAuthCodeCreateDateTime))
                    .from(authManagementEntity)
                    .where(authManagementEntity.authMeansCode.eq(code)
                            .and(authManagementEntity.userId.eq(userId)))
                    .orderBy(authManagementEntity.mobileAuthCodeCreateDateTime.desc())
                    .fetchOne());
        } else { // 메일 인증
            return Optional.ofNullable(jpaQueryFactory.select(Projections.constructor(BizTalkAuthNumberDto.class
                            , authManagementEntity.mailAuthCode
                            , authManagementEntity.mailAuthCodeCreateDateTime))
                    .where(authManagementEntity.authMeansCode.eq(code)
                            .and(authManagementEntity.userId.eq(userId)))
                    .orderBy(authManagementEntity.mailAuthCodeCreateDateTime.desc())
                    .fetchOne());
        }
    }
}
