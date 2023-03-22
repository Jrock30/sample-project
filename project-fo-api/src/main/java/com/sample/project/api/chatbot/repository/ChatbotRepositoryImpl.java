package com.sample.project.api.apple.repository;

import com.sample.project.api.apple.dto.AdminAppleListDto;
import com.sample.project.api.apple.dto.request.RequestAppleDelegateDto;
import com.sample.project.api.apple.dto.request.RequestSearchBotListDto;
import com.sample.project.api.apple.dto.response.ResponseBotUserListDto;
import com.sample.project.api.apple.entity.ApplePermissionEntity;
import com.sample.project.api.apple.enums.BotRole;
import com.sample.project.api.apple.enums.DelegationStatus;
import com.sample.project.common.utils.StringUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.sample.project.api.apple.entity.QAppleEntity.appleEntity;
import static com.sample.project.api.apple.entity.QApplePermissionEntity.applePermissionEntity;
import static com.sample.project.api.apple.enums.BotSearchDateType.*;
import static com.sample.project.api.apple.enums.BotSearchType.*;
import static com.sample.project.api.login.entity.QMemberEntity.memberEntity;

@Slf4j
@RequiredArgsConstructor
public class AppleRepositoryImpl implements AppleRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<ResponseBotUserListDto> searchUserBotInfo(RequestAppleDelegateDto requestAppleDelegateDto) {
        return Optional.ofNullable(jpaQueryFactory
                .select(Projections.constructor(ResponseBotUserListDto.class,
                        appleEntity.botId,
                        applePermissionEntity.userId,
                        appleEntity.botName,
                        appleEntity.mallId,
                        appleEntity.mallName,
                        applePermissionEntity.permissionGroupId,
                        appleEntity.delegateAgencyStatus,
                        appleEntity.delegationProgress,
                        appleEntity.delegateAgencyFailReason,
                        applePermissionEntity.reportAcceptStatus,
                        applePermissionEntity.withdrawalYn))
                .from(appleEntity)
                .leftJoin(applePermissionEntity)
                .on(appleEntity.botId.eq(applePermissionEntity.botId))
                .where(applePermissionEntity.userId.eq(requestAppleDelegateDto.getUserId())
                        .and(applePermissionEntity.botId.eq(requestAppleDelegateDto.getBotId()))
                        .and(appleEntity.deleteYn.eq(0))
                        .and(applePermissionEntity.withdrawalYn.eq(0))
                )
                .fetchOne());
    }

    @Override
    public List<ResponseBotUserListDto> searchUserBotList(String userId) {
        return jpaQueryFactory
                .select(Projections.constructor(ResponseBotUserListDto.class,
                        appleEntity.botId,
                        applePermissionEntity.userId,
                        appleEntity.botName,
                        appleEntity.mallId,
                        appleEntity.mallName,
                        applePermissionEntity.permissionGroupId,
                        appleEntity.delegateAgencyStatus,
                        appleEntity.delegationProgress,
                        appleEntity.delegateAgencyFailReason,
                        applePermissionEntity.reportAcceptStatus,
                        applePermissionEntity.withdrawalYn))
                .from(appleEntity)
                .leftJoin(applePermissionEntity)
                .on(appleEntity.botId.eq(applePermissionEntity.botId))
                .where(applePermissionEntity.userId.eq(userId)
                        .and(appleEntity.deleteYn.eq(0))
                        .and(applePermissionEntity.withdrawalYn.eq(0))
                        .and(appleEntity.delegateAgencyStatus.ne(DelegationStatus.DELEGATION_DELETE_ADMIN.code()))
                )
                .orderBy(applePermissionEntity.permissionGroupId.desc())
                .fetch();
    }

    @Override
    public ApplePermissionEntity searchUserApplePermission(RequestAppleDelegateDto requestAppleDelegateDto) {
        return jpaQueryFactory
                .selectFrom(applePermissionEntity)
                .where(applePermissionEntity.botId.eq(requestAppleDelegateDto.getBotId())
                        .and(applePermissionEntity.userId.eq(requestAppleDelegateDto.getUserId())))
                .fetchOne();
    }

    @Override
    public Page<AdminAppleListDto> searchBotList(RequestSearchBotListDto requestSearchBotListDto, Pageable pageable) {
        List<AdminAppleListDto> adminAppleList = jpaQueryFactory.select(Projections.constructor(AdminAppleListDto.class
                        , appleEntity.botId
                        , applePermissionEntity.userId
                        , memberEntity.mobile
                        , appleEntity.botName
                        , appleEntity.mallId
                        , appleEntity.agencyId
                        , appleEntity.appleSearchId
                        , applePermissionEntity.permissionGroupId
                        , appleEntity.delegateAgencyStatus
                        , appleEntity.adminKey
                        , appleEntity.regDate
                        , appleEntity.delegateRequestDate
                        , appleEntity.delegateUnderDate
                        , appleEntity.delegateStopDate
                        , appleEntity.delegateFailDate
                ))
                .from(appleEntity)
                .leftJoin(applePermissionEntity)
                .on(appleEntity.botId.eq(applePermissionEntity.botId))
                .leftJoin(memberEntity).on(applePermissionEntity.userId.eq(memberEntity.userId))
                .where(setCondition(requestSearchBotListDto))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(adminAppleList, pageable, adminAppleList.size());
    }

    @Override
    public List<AdminAppleListDto> appleList(RequestSearchBotListDto requestSearchBotListDto) {
        return jpaQueryFactory.select(Projections.constructor(AdminAppleListDto.class
                        , appleEntity.botId
                        , applePermissionEntity.userId
                        , memberEntity.mobile
                        , appleEntity.botName
                        , appleEntity.mallId
                        , appleEntity.agencyId
                        , appleEntity.appleSearchId
                        , applePermissionEntity.permissionGroupId
                        , appleEntity.delegateAgencyStatus
                        , appleEntity.adminKey
                        , appleEntity.regDate
                        , appleEntity.delegateRequestDate
                        , appleEntity.delegateUnderDate
                        , appleEntity.delegateStopDate
                        , appleEntity.delegateFailDate
                ))
                .from(appleEntity)
                .leftJoin(applePermissionEntity)
                .on(appleEntity.botId.eq(applePermissionEntity.botId))
                .leftJoin(memberEntity).on(applePermissionEntity.userId.eq(memberEntity.userId))
                .where(setCondition(requestSearchBotListDto))
                .fetch();
    }

    private BooleanExpression setCondition(RequestSearchBotListDto requestSearchBotListDto) {
        return appleEntity.deleteYn.eq(0)
                .and(applePermissionEntity.permissionGroupId.eq(BotRole.BOT_MASTER.code()))
                .and(dateTypeBetween(requestSearchBotListDto.getDateTypeCode()
                        , requestSearchBotListDto.getStartDate()
                        , requestSearchBotListDto.getEndDate())
                )
                .and(searchTypeEq(requestSearchBotListDto.getSearchTypeCode(), requestSearchBotListDto.getSearchText()))
                .and(searchBotStatusIn(requestSearchBotListDto.getDelegationStatus()))
                ;
    }

    private BooleanExpression dateTypeBetween(String dateTypeCode, LocalDate startDate, LocalDate endDate) { // 기간 조건
        if (StringUtils.isNotEmpty(dateTypeCode)
                && !dateTypeCode.equals(BOT_SEARCH_DATE_TYPE_01.code())
                && Objects.nonNull(startDate)
                && Objects.nonNull(endDate)
        ) { // null 이 아니고 선택안함이 아니고, 시작일, 종료일이 빈 값이 아닐 시
            if (dateTypeCode.equals(BOT_SEARCH_DATE_TYPE_02.code())) { // 봇 생성일
                return appleEntity.regDate.between(startDate.atStartOfDay(), LocalDateTime.of(endDate, LocalTime.MAX).withNano(0));
            } else if (dateTypeCode.equals(BOT_SEARCH_DATE_TYPE_03.code())) { // 위임-대행 요청일
                return appleEntity.delegateRequestDate.between(startDate.atStartOfDay(), LocalDateTime.of(endDate, LocalTime.MAX).withNano(0));
            } else if (dateTypeCode.equals(BOT_SEARCH_DATE_TYPE_04.code())) { // 위임-대행 연결 성공일
                return appleEntity.delegateUnderDate.between(startDate.atStartOfDay(), LocalDateTime.of(endDate, LocalTime.MAX).withNano(0));
            } else if (dateTypeCode.equals(BOT_SEARCH_DATE_TYPE_05.code())) { // 위임-대행 연결 실패일
                return appleEntity.delegateFailDate.between(startDate.atStartOfDay(), LocalDateTime.of(endDate, LocalTime.MAX).withNano(0));
            } else if (dateTypeCode.equals(BOT_SEARCH_DATE_TYPE_06.code())) { // 위임-대행 중지일
                return appleEntity.delegateStopDate.between(startDate.atStartOfDay(), LocalDateTime.of(endDate, LocalTime.MAX).withNano(0));
            }
        }
        return null;
    }

    private BooleanExpression searchTypeEq(String searchTypeCode, String searchText) { // 검색어 조건
        if (StringUtils.isNotEmpty(searchTypeCode) && !searchTypeCode.equals(BOT_SEARCH_TYPE_01.code())) { // null 이 아니고 선택안함이 아닐 시
             if (searchTypeCode.equals(BOT_SEARCH_TYPE_02.code())) { // 봇 이름
                return appleEntity.botName.like(searchText + "%");
            } else if (searchTypeCode.equals(BOT_SEARCH_TYPE_03.code())) { // 봇 마스터 회원 아이디
                 return applePermissionEntity.userId.like(searchText + "%");
            } else if (searchTypeCode.equals(BOT_SEARCH_TYPE_04.code())) { // 쇼핑몰 명
                 return appleEntity.mallId.like(searchText + "%");
            } else if (searchTypeCode.equals(BOT_SEARCH_TYPE_05.code())) { // 카페24 ID
                 return appleEntity.agencyId.like(searchText + "%");
            } else if (searchTypeCode.equals(BOT_SEARCH_TYPE_06.code())) { // (위임)봇 ID
                 return appleEntity.botId.like(searchText + "%");
            } else if (searchTypeCode.equals(BOT_SEARCH_TYPE_07.code())) { // 봇 마스터 연락처
                 return memberEntity.mobile.like(searchText + "%");
            } else if (searchTypeCode.equals(BOT_SEARCH_TYPE_08.code())) { // Admin Key
                 return appleEntity.adminKey.like(searchText + "%");
            } else if (searchTypeCode.equals(BOT_SEARCH_TYPE_09.code())) { // 카카오채널 검색용 ID
                 return appleEntity.appleSearchId.like(searchText + "%");
            }
        }
        return null;
    }

    private BooleanExpression searchBotStatusIn(String botStatus) { // 봇 상태 값 조건
        if (StringUtils.isNotEmpty(botStatus)) {
            String[] botStatusList = botStatus.split(",");
            return appleEntity.delegateAgencyStatus.in(botStatusList);
        }
        return null;
    }

}
