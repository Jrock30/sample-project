package com.sample.project.api.login.repository;

import com.sample.project.api.login.entity.LoginMemberEntity;
import com.sample.project.api.login.type.MemberStateType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import static com.sample.project.api.login.entity.QLoginMemberEntity.loginMemberEntity;
import static com.sample.project.api.login.entity.QLoginMemberPermissionEntity.loginMemberPermissionEntity;

@Slf4j
@RequiredArgsConstructor
public class LoginRepositoryImpl implements LoginRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<LoginMemberEntity> searchMember(String userId) {
        return Optional.ofNullable(jpaQueryFactory
                .select(loginMemberEntity, loginMemberPermissionEntity)
                .from(loginMemberEntity)
                .join(loginMemberEntity.loginMemberPermissionEntity, loginMemberPermissionEntity).fetchJoin()
                .where(loginMemberEntity.userId.eq(userId)
                        .and(loginMemberEntity.memberStatusCode.eq(MemberStateType.MEMBER_STATE_NORMAL.code()))) // 회원정상상태
                .fetchOne())
                .map(m -> m.get(loginMemberEntity));
    }
}
