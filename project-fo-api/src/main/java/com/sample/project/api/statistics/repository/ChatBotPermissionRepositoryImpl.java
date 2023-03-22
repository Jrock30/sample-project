package com.project.project.api.statistics.repository;

import com.project.project.api.statistics.dto.AppleMemberDto;
import com.project.project.api.statistics.type.DailyReportStatus;
import com.project.project.api.statistics.type.MemberStateType;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.project.project.api.statistics.entity.QAppleMemberEntity.appleMemberEntity;
import static com.project.project.api.statistics.entity.QApplePermissionEntity.applePermissionEntity;

@Slf4j
@RequiredArgsConstructor
public class ApplePermissionRepositoryImpl implements ApplePermissionRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<AppleMemberDto> searchAppleMemberList(String botId) {
        return jpaQueryFactory.select(Projections.constructor(AppleMemberDto.class
                        , applePermissionEntity.botId
                        , applePermissionEntity.permissionGroupId
                        , applePermissionEntity.reportAcceptStatus
                        , applePermissionEntity.withdrawalYn
                        , applePermissionEntity.userId
                        , appleMemberEntity.mallId
                        , appleMemberEntity.mallName
                        , appleMemberEntity.agencyId
                        , appleMemberEntity.agencyUserName
                        , appleMemberEntity.mobile
                        , appleMemberEntity.memberStatusCode
                        )
                )
                .from(applePermissionEntity)
                .leftJoin(appleMemberEntity)
                .on(applePermissionEntity.userId.eq(appleMemberEntity.userId))
                .where(applePermissionEntity.botId.eq(botId)
                        .and(applePermissionEntity.withdrawalYn.eq(0))
                        .and(applePermissionEntity.reportAcceptStatus.eq(DailyReportStatus.DR_RECEIVING.code()))
                        .and(appleMemberEntity.memberStatusCode.eq(MemberStateType.MEMBER_STATE_NORMAL.code()))
                        .and(appleMemberEntity.mobile.isNotNull())
                )
                .fetch();
    }
}
