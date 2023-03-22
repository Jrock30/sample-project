package com.sample.project.api.login.repository;

import com.sample.project.api.apple.enums.BotRole;
import com.sample.project.api.login.dto.MemberInfoDto;
import com.sample.project.api.login.dto.MemberListDto;
import com.sample.project.api.login.dto.RequestSearchMemberListDto;
import com.sample.project.api.login.type.MemberStateType;
import com.sample.project.common.utils.CommonUtils;
import com.sample.project.common.utils.StringUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

import static com.sample.project.api.apple.entity.QAppleEntity.appleEntity;
import static com.sample.project.api.apple.entity.QApplePermissionEntity.applePermissionEntity;
import static com.sample.project.api.login.entity.QMemberEntity.memberEntity;
import static com.sample.project.api.login.type.MemberSearchDateType.*;
import static com.sample.project.api.login.type.MemberSearchType.*;

@Slf4j
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<MemberListDto> searchMemberList(RequestSearchMemberListDto requestSearchMemberListDto, Pageable pageable) {
        List<MemberListDto> memberDtoList = jpaQueryFactory.select(Projections.constructor(MemberListDto.class
                        , memberEntity.userId
                        , memberEntity.memberStatusCode
                        , memberEntity.mobile
                        , memberEntity.joinDate
                        , appleEntity.botId
                        , appleEntity.botName
                        , memberEntity.agencyId
                        , memberEntity.agencyUserName
                        , memberEntity.mallId
                        , memberEntity.mallName
                        , memberEntity.adminMemo
                ))
                .from(memberEntity)
                .leftJoin(applePermissionEntity)
                .on(memberEntity.userId
                        .eq(applePermissionEntity.userId)
                        .and(applePermissionEntity.withdrawalYn.eq(0))
                        .and(applePermissionEntity.permissionGroupId.eq(BotRole.BOT_MASTER.code()))
                )
                .leftJoin(appleEntity).on(applePermissionEntity.botId.eq(appleEntity.botId)
                        .and(appleEntity.deleteYn.eq(0)))
                .where(setCondition(requestSearchMemberListDto))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
//                .orderBy(queryListSort(pageable))
                .fetch();

        return new PageImpl<>(memberDtoList, pageable, memberDtoList.size());
    }

    @Override
    public MemberInfoDto searchMemberInfo(String userId) {
        MemberInfoDto memberInfoDto = jpaQueryFactory.select(Projections.constructor(MemberInfoDto.class
                        , memberEntity.userId
                        , memberEntity.memberStatusCode
                        , memberEntity.mobile
                        , memberEntity.joinDate
                        , appleEntity.botName
                        , memberEntity.agencyId
                        , memberEntity.termsOptional1Yn
                ))
                .from(memberEntity)
                .leftJoin(applePermissionEntity)
                .on(memberEntity.userId
                        .eq(applePermissionEntity.userId)
                        .and(applePermissionEntity.withdrawalYn.eq(0))
                        .and(applePermissionEntity.permissionGroupId.eq(BotRole.BOT_MASTER.codeName())))
                .leftJoin(appleEntity)
                .on(applePermissionEntity.botId.eq(appleEntity.botId)
                        .and(appleEntity.deleteYn.eq(0)))
                .where(memberEntity.userId.eq(userId))
                .fetchOne();

        List<String> botNameList = jpaQueryFactory.select(appleEntity.botName)
                .from(appleEntity)
                .join(applePermissionEntity)
                .on(appleEntity.botId.eq(applePermissionEntity.botId))
                .where(applePermissionEntity.userId.eq(userId)
                        .and(applePermissionEntity.withdrawalYn.eq(0))
                        .and(applePermissionEntity.permissionGroupId.ne(BotRole.BOT_MASTER.code())))
                .fetch();

        if (memberInfoDto != null) {
            memberInfoDto.setBotNameList(botNameList);
        }
        return memberInfoDto;
    }

    /**
     * 검색조건에 해당하는 회원 리스트(페이징X)
     * @param requestSearchMemberListDto
     * @return
     */
    @Override
    public List<MemberListDto> getMemberList(RequestSearchMemberListDto requestSearchMemberListDto) {
        return jpaQueryFactory.select(Projections.constructor(MemberListDto.class
                        , memberEntity.userId
                        , memberEntity.memberStatusCode
                        , memberEntity.mobile
                        , memberEntity.joinDate
                        , appleEntity.botId
                        , appleEntity.botName
                        , memberEntity.agencyId
                        , memberEntity.agencyUserName
                        , memberEntity.mallId
                        , memberEntity.mallName
                        , memberEntity.adminMemo
                ))
                .from(memberEntity)
                .leftJoin(applePermissionEntity)
                .on(memberEntity.userId
                        .eq(applePermissionEntity.userId)
                        .and(applePermissionEntity.withdrawalYn.eq(0))
                        .and(applePermissionEntity.permissionGroupId.eq(BotRole.BOT_MASTER.code()))
                )
                .leftJoin(appleEntity).on(applePermissionEntity.botId.eq(appleEntity.botId)
                        .and(appleEntity.deleteYn.eq(0))
                )
                .where(setCondition(requestSearchMemberListDto))
                .fetch();
    }
    /**
     * 해당 회원이 운영자로 있는 봇 리스트가져오기
     * @param userId
     * @return
     */
    @Override
    public List<String> getManagerBotList(String userId) {
        return jpaQueryFactory.select(Projections.constructor(String.class
                        , applePermissionEntity.botId
                ))
                .from(applePermissionEntity)
                .leftJoin(memberEntity)
                .on(applePermissionEntity.userId.eq(memberEntity.userId)
                        .and(memberEntity.memberStatusCode.ne(MemberStateType.MEMBER_STATE_WITHDRAWAL.code()))
                        .and(applePermissionEntity.withdrawalYn.eq(0))
                )
                .where(setCond(userId))
                .fetch();
    }

    private BooleanBuilder setCond(String userId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(applePermissionEntity.userId.eq(userId))
                .and(applePermissionEntity.permissionGroupId.eq("MANAGER"));
        return booleanBuilder;
    }

    private BooleanBuilder setCondition(RequestSearchMemberListDto requestSearchMemberListDto) {
        return memberStatusCodeEq(requestSearchMemberListDto.getMemberStatusCode())
                .and(dateTypeBetween(requestSearchMemberListDto.getDateTypeCode()
                        , requestSearchMemberListDto.getStartDate()
                        , requestSearchMemberListDto.getEndDate())
                )
                .and(searchTypeEq(requestSearchMemberListDto.getSearchTypeCode(), requestSearchMemberListDto.getSearchText()))
                ;
    }

    private BooleanBuilder memberStatusCodeEq(String memberStatusCode) { // 회원상태 조건
        return CommonUtils.nullSafeBuilder(() -> memberEntity.memberStatusCode.eq(memberStatusCode));
    }

    private BooleanExpression dateTypeBetween(String dateTypeCode, LocalDate startDate, LocalDate endDate) { // 기간 조건
        if (StringUtils.isNotEmpty(dateTypeCode)
                && !dateTypeCode.equals(MEMBER_SEARCH_DATE_TYPE_01.code())
                && Objects.nonNull(startDate)
                && Objects.nonNull(endDate)
        ) { // null 이 아니고 선택안함이 아니고, 시작일, 종료일이 빈 값이 아닐 시
            if (dateTypeCode.equals(MEMBER_SEARCH_DATE_TYPE_02.code())) { // 가입일
                return memberEntity.joinDate.between(startDate.atStartOfDay(), LocalDateTime.of(endDate, LocalTime.MAX).withNano(0));
            } else if (dateTypeCode.equals(MEMBER_SEARCH_DATE_TYPE_03.code())) { // 가입초대일
                return memberEntity.inviteDate.between(startDate.atStartOfDay(), LocalDateTime.of(endDate, LocalTime.MAX).withNano(0));
            } else if (dateTypeCode.equals(MEMBER_SEARCH_DATE_TYPE_04.code())) { // 정지일
                return memberEntity.suspensionDate.between(startDate.atStartOfDay(), LocalDateTime.of(endDate, LocalTime.MAX).withNano(0));
            } else if (dateTypeCode.equals(MEMBER_SEARCH_DATE_TYPE_05.code())) { // 탈퇴일
                return memberEntity.withdrawalDate.between(startDate.atStartOfDay(), LocalDateTime.of(endDate, LocalTime.MAX).withNano(0));
            }
        }
        return null;
    }

    private BooleanExpression searchTypeEq(String searchTypeCode, String searchText) { // 검색어 조건
        if (StringUtils.isNotEmpty(searchTypeCode)) {
            if (searchTypeCode.equals(MEMBER_SEARCH_TYPE_01.code())) { // 상호명
                return null;
            } else if (searchTypeCode.equals(MEMBER_SEARCH_TYPE_02.code())) { // 쇼핑몰명
                return memberEntity.mallName.like("%" + searchText + "%");
            } else if (searchTypeCode.equals(MEMBER_SEARCH_TYPE_03.code())) { // 사업자등록번호
                return null;
            } else if (searchTypeCode.equals(MEMBER_SEARCH_TYPE_04.code())) { // 정산담당자이름
                return null;
            } else if (searchTypeCode.equals(MEMBER_SEARCH_TYPE_05.code())) { // 정산담당자연락처
                return null;
            } else if (searchTypeCode.equals(MEMBER_SEARCH_TYPE_06.code())) { // 정산담당자이메일
                return null;
            } else if (searchTypeCode.equals(MEMBER_SEARCH_TYPE_07.code())) { // 세금계산서이메일
                return null;
            } else if (searchTypeCode.equals(MEMBER_SEARCH_TYPE_08.code())) { // 관리자메모
                return null;
            } else if (searchTypeCode.equals(MEMBER_SEARCH_TYPE_09.code())) { // 아이디
                return memberEntity.userId.like(searchText + "%");
            } else if (searchTypeCode.equals(MEMBER_SEARCH_TYPE_10.code())) { // 마스터봇이름
                return appleEntity.botName.like(searchText + "%");
            } else if (searchTypeCode.equals(MEMBER_SEARCH_TYPE_11.code())) { // 카페24아이디
                return memberEntity.agencyId.like(searchText + "%");
            } else if (searchTypeCode.equals(MEMBER_SEARCH_TYPE_12.code())) { // 연락처
                return memberEntity.mobile.like(searchText + "%");
            }
        }
        return null;
    }

    /**
     * OrderSpecifier 를 쿼리로 반환하여 정렬조건을 맞춰준다.
     * 리스트 정렬
     * @param page
     * @return
     */
    private OrderSpecifier<?> queryListSort(Pageable page) {
        //서비스에서 보내준 Pageable 객체에 정렬조건 null 값 체크
        if (!page.getSort().isEmpty()) {
            //정렬값이 들어 있으면 for 사용하여 값을 가져온다
            for (Sort.Order order : page.getSort()) {
                // 서비스에서 넣어준 DESC or ASC 를 가져온다.
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                // 서비스에서 넣어준 정렬 조건을 스위치 케이스 문을 활용하여 셋팅하여 준다.
                switch (order.getProperty()) {
                    case "userId":
                        return new OrderSpecifier(direction, memberEntity.userId);
                }
            }
        }
        return null;
    }

}
