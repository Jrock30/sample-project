package com.sample.project.api.marketingMessage.repository.custom.impl;

import com.sample.project.api.apple.entity.AppleEntity;
import com.sample.project.api.apple.repository.AppleRepository;
import com.sample.project.api.marketingMessage.dto.MarketingMessageResultDto;
import com.sample.project.api.marketingMessage.dto.request.SearchMessageResultDto;
import com.sample.project.api.marketingMessage.entity.MarketingMessageResultEntity;
import com.sample.project.api.marketingMessage.enums.CampaignMessageResultPeriodType;
import com.sample.project.api.marketingMessage.repository.custom.MarketingMessageResultCustomRepository;
import com.sample.project.common.utils.QueryDslUtil;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.sample.project.api.marketingMessage.entity.QMarketingMessageResultEntity.marketingMessageResultEntity;
import static org.springframework.util.ObjectUtils.isEmpty;

@RequiredArgsConstructor
public class MarketingMessageResultCustomRepositoryImpl implements MarketingMessageResultCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final AppleRepository appleRepository;

    @Override
    public Page<MarketingMessageResultDto> searchMarketingMessageResultList(SearchMessageResultDto searchMessageResult, Pageable pageable) {
        List<OrderSpecifier> orders = getAllOrderSpecifiers(pageable);
        List<MarketingMessageResultDto> marketingMessageResultDtoList = jpaQueryFactory
                .selectFrom(marketingMessageResultEntity)
                .where(setCondition(searchMessageResult))
                .offset(pageable.getOffset())// 페이지 번호
                .limit(pageable.getPageSize()) // 페이지 사이즈
                .orderBy(orders.stream().toArray(OrderSpecifier[]::new))
                .fetch().stream().map(MarketingMessageResultDto::new).collect(Collectors.toList());

        //count 만 가져오는 쿼리
        JPQLQuery<MarketingMessageResultEntity> count = jpaQueryFactory
                .selectFrom(marketingMessageResultEntity)
                .where(setCondition(searchMessageResult));

        return PageableExecutionUtils.getPage(marketingMessageResultDtoList,pageable,()-> count.fetchCount());
    }
    @Override
    public List<MarketingMessageResultDto> searchMarketingMessageResultListNoPaging(SearchMessageResultDto searchMessageResult) {

        return jpaQueryFactory
                .selectFrom(marketingMessageResultEntity)
                .where(setCondition(searchMessageResult))
                .fetch().stream().map(MarketingMessageResultDto::new).collect(Collectors.toList());

    }

    // where절에 있는 조건 check method
    private BooleanBuilder setCondition(SearchMessageResultDto searchMessageResult){
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        // botId기반으로 목록 조회
        if(StringUtils.hasLength(searchMessageResult.getBotId())){
            booleanBuilder.and(marketingMessageResultEntity.botId.eq(searchMessageResult.getBotId()));
        }else if(StringUtils.isNotEmpty(searchMessageResult.getMallId())){// 정산관리 상세내역 보기(관리자 페이지에서는 슈퍼, 관리자 권한이기에 따로 mallId가 없다)
            List<AppleEntity> appleEntityList = appleRepository.findByMallId(searchMessageResult.getMallId());
            appleEntityList.stream().findFirst().ifPresent(
                    item -> booleanBuilder.and(marketingMessageResultEntity.botId.eq(item.getBotId()))
            );
        }

        // 조회 기간
        if(StringUtils.hasLength(searchMessageResult.getPeriodType())){
            if(searchMessageResult.getPeriodType().equals(CampaignMessageResultPeriodType.RESULT_PERIOD_TYPE_01.getCode())) {
                if (!isEmpty(searchMessageResult.getStartDate()) && !isEmpty(searchMessageResult.getEndDate())) {
                    booleanBuilder.and(marketingMessageResultEntity.sendDate.between(searchMessageResult.getStartDate(), searchMessageResult.getEndDate()));
                }
            }else{
                if(StringUtils.hasLength(searchMessageResult.getCalculateYear())){
                    // 정산월 필드의 연도, 월이 있을 경우
                    if(StringUtils.hasLength(searchMessageResult.getCalculateMonth())){
                        int yearMonth = Integer.parseInt(searchMessageResult.getCalculateYear()+searchMessageResult.getCalculateMonth());
                        booleanBuilder.and(marketingMessageResultEntity.calculateMonth.yearMonth().eq(yearMonth));
                    }else{
                        // 정산월 필드의 연도만 잇을 경우
                        int year = Integer.parseInt(searchMessageResult.getCalculateYear());
                        booleanBuilder.and(marketingMessageResultEntity.calculateMonth.year().eq(year));
                    }
                }
            }
        }

        /*캠페인 명 검색 조건*/
        if(StringUtils.hasLength(searchMessageResult.getCampaignName())){
            booleanBuilder.and(marketingMessageResultEntity.campaignName.contains(searchMessageResult.getCampaignName()));
        }
        return booleanBuilder;
    }

    /**
     * OrderSpecifier 를 쿼리로 반환하여 정렬조건을 맞춰준다.
     * 리스트 정렬
     * @param pageable
     * @return
     */

    private List<OrderSpecifier> getAllOrderSpecifiers(Pageable pageable) {
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();
        OrderSpecifier<?> orderMessageResultNo;
        //서비스에서 보내준 Pageable 객체에 정렬조건 null 값 체크
        if (!isEmpty(pageable.getSort())) {
            //정렬값이 들어 있으면 for 사용하여 값을 가져온다
            for (Sort.Order order : pageable.getSort()) {
                // 서비스에서 넣어준 DESC or ASC 를 가져온다.
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                // 서비스에서 넣어준 정렬 조건을 스위치 케이스 문을 활용하여 셋팅하여 준다.
                switch (order.getProperty()){
                    case "requestCnt":
                        OrderSpecifier<?> orderRequestCnt = QueryDslUtil.getSortedColumn(direction, marketingMessageResultEntity.requestCnt, "requestCnt");
                        orderSpecifiers.add(orderRequestCnt);
                        // 기본적으로 messageResultNo이 가장 최신인 것으로 정렬 거기에 아래의 정렬 조건을 취합
                        orderMessageResultNo = QueryDslUtil.getSortedColumn(Order.DESC, marketingMessageResultEntity.messageResultNo, "messageResultNo");
                        orderSpecifiers.add(orderMessageResultNo);
                        break;
                    case "successCnt":
                        OrderSpecifier<?> orderSuccessCnt = QueryDslUtil.getSortedColumn(direction, marketingMessageResultEntity.successCnt, "successCnt");
                        orderSpecifiers.add(orderSuccessCnt);
                        orderMessageResultNo = QueryDslUtil.getSortedColumn(Order.DESC, marketingMessageResultEntity.messageResultNo, "messageResultNo");
                        orderSpecifiers.add(orderMessageResultNo);
                        break;
                    case "failCnt":
                        OrderSpecifier<?> orderFailCnt = QueryDslUtil.getSortedColumn(direction, marketingMessageResultEntity.failCnt, "failCnt");
                        orderSpecifiers.add(orderFailCnt);
                        orderMessageResultNo = QueryDslUtil.getSortedColumn(Order.DESC, marketingMessageResultEntity.messageResultNo, "messageResultNo");
                        orderSpecifiers.add(orderMessageResultNo);
                        break;
                    case "cost":
                        OrderSpecifier<?> orderCost = QueryDslUtil.getSortedColumn(direction, marketingMessageResultEntity.cost, "cost");
                        orderSpecifiers.add(orderCost);
                        orderMessageResultNo = QueryDslUtil.getSortedColumn(Order.DESC, marketingMessageResultEntity.messageResultNo, "messageResultNo");
                        orderSpecifiers.add(orderMessageResultNo);
                        break;
                    case "clickCnt":
                        OrderSpecifier<?> orderClickCnt = QueryDslUtil.getSortedColumn(direction, marketingMessageResultEntity.clickCnt, "clickCnt");
                        orderSpecifiers.add(orderClickCnt);
                        orderMessageResultNo = QueryDslUtil.getSortedColumn(Order.DESC, marketingMessageResultEntity.messageResultNo, "messageResultNo");
                        orderSpecifiers.add(orderMessageResultNo);
                        break;
                    case "clickRate":
                        OrderSpecifier<?> orderClickRate = QueryDslUtil.getSortedColumn(direction, marketingMessageResultEntity.clickRate, "clickRate");
                        orderSpecifiers.add(orderClickRate);
                        orderMessageResultNo = QueryDslUtil.getSortedColumn(Order.DESC, marketingMessageResultEntity.messageResultNo, "messageResultNo");
                        orderSpecifiers.add(orderMessageResultNo);
                        break;
                    case "buyCnt":
                        OrderSpecifier<?> orderBuyCnt = QueryDslUtil.getSortedColumn(direction, marketingMessageResultEntity.buyCnt, "buyCnt");
                        orderSpecifiers.add(orderBuyCnt);
                        orderMessageResultNo = QueryDslUtil.getSortedColumn(Order.DESC, marketingMessageResultEntity.messageResultNo, "messageResultNo");
                        orderSpecifiers.add(orderMessageResultNo);
                        break;
                    case "buyAmount":
                        OrderSpecifier<?> orderBuyAmount = QueryDslUtil.getSortedColumn(direction, marketingMessageResultEntity.buyAmount, "buyAmount");
                        orderSpecifiers.add(orderBuyAmount);
                        orderMessageResultNo = QueryDslUtil.getSortedColumn(Order.DESC, marketingMessageResultEntity.messageResultNo, "messageResultNo");
                        orderSpecifiers.add(orderMessageResultNo);
                        break;
                    case "roas":
                        OrderSpecifier<?> orderRroas = QueryDslUtil.getSortedColumn(direction, marketingMessageResultEntity.roas, "roas");
                        orderSpecifiers.add(orderRroas);
                        orderMessageResultNo = QueryDslUtil.getSortedColumn(Order.DESC, marketingMessageResultEntity.messageResultNo, "messageResultNo");
                        orderSpecifiers.add(orderMessageResultNo);
                        break;
                    case "cpa":
                        OrderSpecifier<?> orderCpa = QueryDslUtil.getSortedColumn(direction, marketingMessageResultEntity.cpa, "cpa");
                        orderSpecifiers.add(orderCpa);
                        orderMessageResultNo = QueryDslUtil.getSortedColumn(Order.DESC, marketingMessageResultEntity.messageResultNo, "messageResultNo");
                        orderSpecifiers.add(orderMessageResultNo);
                        break;
                    default:
                        OrderSpecifier<?> orderSendDate = QueryDslUtil.getSortedColumn(direction, marketingMessageResultEntity.sendDate, "sendDate");
                        orderSpecifiers.add(orderSendDate);
                        break;
                }
            }
        }
        return orderSpecifiers;
    }

}
