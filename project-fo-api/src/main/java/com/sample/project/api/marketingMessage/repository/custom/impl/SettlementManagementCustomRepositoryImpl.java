package com.sample.project.api.marketingMessage.repository.custom.impl;

import com.sample.project.api.marketingMessage.dto.SettlementManagementDto;
import com.sample.project.api.marketingMessage.dto.request.SearchSettlementDto;
import com.sample.project.api.marketingMessage.entity.CampaignEntity;
import com.sample.project.api.marketingMessage.repository.custom.SettlementManagementCustomRepository;
import com.sample.project.security.SecurityUtils;
import com.sample.project.security.model.CustomUserDetails;
import com.sample.project.security.type.RoleType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static com.sample.project.api.marketingMessage.entity.QCampaignEntity.campaignEntity;
import static com.sample.project.api.marketingMessage.entity.QSettlementManagementEntity.settlementManagementEntity;

@RequiredArgsConstructor
public class SettlementManagementCustomRepositoryImpl implements SettlementManagementCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<SettlementManagementDto> searchSettlementManagementList(SearchSettlementDto searchSettlementDto, Pageable pageable) {
        List<SettlementManagementDto> settlementManagementDtos = jpaQueryFactory
                .selectFrom(settlementManagementEntity)
                .where(setCondition(searchSettlementDto))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(settlementManagementEntity.settlementManagementNo.desc())
                .fetch().stream().map(SettlementManagementDto::new).collect(Collectors.toList());

        //count 만 가져오는 쿼리
        JPQLQuery<CampaignEntity> count = jpaQueryFactory
                .selectFrom(campaignEntity)
                .where(setCondition(searchSettlementDto));

        return PageableExecutionUtils.getPage(settlementManagementDtos,pageable, count::fetchCount);
    }

    private BooleanBuilder setCondition(SearchSettlementDto searchSettlementDto){
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (SecurityUtils.getUserDetails().isPresent()) {
            CustomUserDetails userDetails = (CustomUserDetails) SecurityUtils.getUserDetails().get();
            if(!userDetails.getAuthorities().stream().anyMatch(item -> (item.getAuthority().equals(RoleType.SUPER.code())||item.getAuthority().equals(RoleType.ADMIN.code())))){
                booleanBuilder.and(settlementManagementEntity.mallId.eq(userDetails.getMallId()));
            }
        }

        // 조회 기간
        if(StringUtils.hasLength(searchSettlementDto.getPeriodYear())){
            String startDateString = searchSettlementDto.getPeriodYear()+"-01"+"-01";
            String endDateString = searchSettlementDto.getPeriodYear()+"-12"+"-31";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            LocalDate startDate = LocalDate.parse(startDateString, formatter);
            LocalDate endDate = LocalDate.parse(endDateString, formatter);
            booleanBuilder.and(settlementManagementEntity.startDate.between(startDate, endDate));
        }

        // 정산 상태
        if(StringUtils.hasLength(searchSettlementDto.getPaymentStatus())){
            booleanBuilder.and(settlementManagementEntity.paymentStatus.eq(searchSettlementDto.getPaymentStatus()));
        }
        return booleanBuilder;
    }
}
