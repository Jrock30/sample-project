package com.sample.project.api.marketingMessage.repository.custom.impl;

import com.sample.project.api.marketingMessage.dto.reponse.CampaignListResponseDto;
import com.sample.project.api.marketingMessage.dto.request.SearchCampaignListDto;
import com.sample.project.api.marketingMessage.entity.CampaignEntity;
import com.sample.project.api.marketingMessage.enums.CampaignPeriodType;
import com.sample.project.api.marketingMessage.enums.CampaignProgressStatus;
import com.sample.project.api.marketingMessage.enums.CampaignTargetType;
import com.sample.project.api.marketingMessage.repository.custom.CampaignCustomRepository;
import com.sample.project.common.utils.StringUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.sample.project.api.marketingMessage.entity.QCampaignEntity.campaignEntity;

@RequiredArgsConstructor
public class CampaignCustomRepositoryImpl implements CampaignCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<CampaignListResponseDto> searchCampaignList(SearchCampaignListDto searchCampaignListDto, Pageable pageable) {

        List<CampaignListResponseDto> campaignDtoList = jpaQueryFactory
                .selectFrom(campaignEntity)
                .where(setCondition(searchCampaignListDto))
                .offset(pageable.getOffset())// 페이지 번호
                .limit(pageable.getPageSize()) // 페이지 사이즈
                .orderBy(campaignListSort(pageable))
                .fetch().stream().map(CampaignListResponseDto::new).collect(Collectors.toList());

        campaignDtoList.stream().parallel().forEach(item -> {
            StringBuilder stringBuilder = new StringBuilder();
            if(item.getTargetTypeCode().equals(CampaignTargetType.TARGET_01.getCode())){
                item.setFrontTarget(item.getTargetTestMobile());
            }else{
                stringBuilder.append("최근 ");
                stringBuilder.append(item.getTargetBaseMonth());
                stringBuilder.append("개월 이내 ");
                stringBuilder.append(CampaignTargetType.valueOf(item.getTargetTypeCode()).getCodeName());
                item.setFrontTarget(stringBuilder.toString());
            }
            item.setCampaignProgessStatusName(CampaignProgressStatus.valueOf(item.getCampaignProgessStatus()).getCodeName());
        });

        //count 만 가져오는 쿼리
        JPQLQuery<CampaignEntity> count = jpaQueryFactory
                .selectFrom(campaignEntity)
                .where(setCondition(searchCampaignListDto));

        return PageableExecutionUtils.getPage(campaignDtoList,pageable, count::fetchCount);
    }

    // where절에 있는 조건 check method
    private BooleanBuilder setCondition(SearchCampaignListDto searchCampaignListDto){
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        // botId기반으로 목록 조회
        if(StringUtils.hasLength(searchCampaignListDto.getBotId())){
            booleanBuilder.and(campaignEntity.botId.eq(searchCampaignListDto.getBotId()));
            booleanBuilder.and(campaignEntity.delYn.eq("N"));
        }

        /*캠페인 명 검색 조건*/
        if(StringUtils.hasLength(searchCampaignListDto.getCampaignName())){
            booleanBuilder.and(campaignEntity.campaignName.contains(searchCampaignListDto.getCampaignName()));
        }

        /*기간 검색 조건*/
        if(StringUtils.hasLength(searchCampaignListDto.getPeriodType())){
            if(searchCampaignListDto.getPeriodType().equals(CampaignPeriodType.PERIOD_TYPE_01.code())){
                if(!ObjectUtils.isEmpty(searchCampaignListDto.getStartDate())&& !ObjectUtils.isEmpty(searchCampaignListDto.getEndDate())){
                    booleanBuilder.and(campaignEntity.sendStartDate.between(searchCampaignListDto.getStartDate(), searchCampaignListDto.getEndDate()));
                }
            }else if(searchCampaignListDto.getPeriodType().equals(CampaignPeriodType.PERIOD_TYPE_02.code())){
                if(!ObjectUtils.isEmpty(searchCampaignListDto.getStartDate())&& !ObjectUtils.isEmpty(searchCampaignListDto.getEndDate())){
                    booleanBuilder.and(campaignEntity.sendEndDate.between(searchCampaignListDto.getStartDate(), searchCampaignListDto.getEndDate()));
                }
            }
        }

        /*진행 상태 */
        if(!ObjectUtils.isEmpty(searchCampaignListDto.getCampaignProgessStatus())&&searchCampaignListDto.getCampaignProgessStatus().size()>0){
            booleanBuilder.and(campaignEntity.campaignProgessStatus.in(searchCampaignListDto.getCampaignProgessStatus()));
        }

        /*테스트발송 건 제외 여부*/
        if(StringUtils.hasLength(searchCampaignListDto.getTestSendYn())){
            if(searchCampaignListDto.getTestSendYn().equals("Y")){
                booleanBuilder.and(campaignEntity.targetTypeCode.ne(CampaignTargetType.TARGET_01.getCode()));
            }
        }

        return booleanBuilder;
    }

    /**
     * OrderSpecifier 를 쿼리로 반환하여 정렬조건을 맞춰준다.
     * 리스트 정렬
     * @param page
     * @return
     */
    private OrderSpecifier<?> campaignListSort(Pageable page) {
        //서비스에서 보내준 Pageable 객체에 정렬조건 null 값 체크
        if (!page.getSort().isEmpty()) {
            //정렬값이 들어 있으면 for 사용하여 값을 가져온다
            for (Sort.Order order : page.getSort()) {
                // 서비스에서 넣어준 DESC or ASC 를 가져온다.
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                // 서비스에서 넣어준 정렬 조건을 스위치 케이스 문을 활용하여 셋팅하여 준다.
                switch (order.getProperty()){
                    case "sendStartDate":
                        return new OrderSpecifier<>(direction, campaignEntity.sendStartDate);
                    case "sendEndDate":
                        return new OrderSpecifier<>(direction, campaignEntity.sendEndDate);
                }
            }
        }
        return null;
    }
}
