package com.project.project.api.statistics.repository;

import com.project.project.api.statistics.dto.AppleHistoryDto;
import com.project.project.api.statistics.entity.QAppleReqeustHistoryEntity;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.MathExpressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.project.project.api.statistics.entity.QAppleBaseGuideEntity.appleBaseGuideEntity;
import static com.project.project.api.statistics.entity.QAppleReqeustHistoryEntity.appleReqeustHistoryEntity;

@Slf4j
@RequiredArgsConstructor
public class AppleRequestHistoryRepositoryImpl implements AppleRequestHistoryRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    String[] notCountBlocks = { // 제외할 인텐트 ID (블록 ID)
            "635b3d6d7d0dc94f4d60ad1a"    //	챗봇홈
            , "63719a6d23b1d500155498cd"  //	웰컴블록
            , "6272005a04a7d7314aeaf6a2"  //	폴백블록
            , "63689d8fb10fe25f796d7681"  //	탈출블록
            , "638007a5e53cb5068561f28a"  //	인증블록
            , "636463f7c50b0864836e4c79"  //	주문정보
            , "636463fea5e36f33209bf52d"  //	구매한품목_1
            , "638ef9d16b749e1b173679cf"  //	구매한품목_2
            , "636464f8a5e36f33209bf540"  //	안내_가이드
            , "63646503a5e36f33209bf542"  //	전체 도움말
            , "6364652b0acf1b07afad65c6"  //	1 취소_변경 문의
            , "6364656ba5e36f33209bf546"  //	2 반품_교환 문의
            , "6364657bc50b0864836e4c9d"  //	3 결제_환불 문의
            , "63646583862f77129379ce37"  //	4 주문_상품 문의
            , "636465900acf1b07afad65c8"  //	5 배송문의
            , "6364659ca5e36f33209bf54c"  //	6 회원문의
            , "636465a50acf1b07afad65ca"  //	7 쇼핑몰 문의
            , "636465b8862f77129379ce39"  //	1-1 취소 문의
            , "63646f080acf1b07afad66fb"  //	1-2 변경 문의
            , "63646896c50b0864836e4cd8"  //	2-1 반품교환 방법
            , "636468a80acf1b07afad65ec"  //	2-2 반품교환 정보
            , "63646b200acf1b07afad663b"  //	3-1 결제 문의
            , "63646b3fc50b0864836e4d23"  //	3-2환불 문의
            , "63646bb90acf1b07afad6652"  //	4-1 주문 문의
            , "63646bcb862f77129379cf03"  //	4-2 상품 문의
            , "63646bf2a5e36f33209bf5ee"  //	5-1 배송조회 방법
            , "63646c31a5e36f33209bf5f1"  //	5-2 배송정보
            , "63646d15a5e36f33209bf636"  //	5-3 배송이슈
            , "63646d82862f77129379cfa9"  //	5-4 배송지
            , "63646db2a5e36f33209bf697"  //	6-1 로그인
            , "6364796a58fb1158c09b382c"  //	6-2 회원가입
            , "6364797643c794560531ed5a"  //	6-3 본인인증
            , "63647982b9c5cc349076cd1d"  //	6-4 회원정보
            , "636479a1b9c5cc349076cd1f"  //	6-6 회원혜택
            , "63647acc58fb1158c09b383f"  //	7-1 이벤트안내
            , "63647adc181fef3a7d256209"  //	7-2 쇼핑몰문의
    };

    @Override
    public List<AppleHistoryDto> searchAppleDayHistory(LocalDate yesterday) { // 일 스킬 통계 조회
        QAppleReqeustHistoryEntity appleHistorySub = new QAppleReqeustHistoryEntity("appleHistorySub");

        return jpaQueryFactory.select(Projections.constructor(AppleHistoryDto.class
                        , appleReqeustHistoryEntity.intentId
                        , appleReqeustHistoryEntity.botId
                        , appleReqeustHistoryEntity.historyRegDate
                        , appleReqeustHistoryEntity.entityEntry
                        , appleBaseGuideEntity.baseBlockCode
                        , appleBaseGuideEntity.largeCategoryCode
                        , appleBaseGuideEntity.largeCategoryName
                        , appleBaseGuideEntity.middleCategoryCode
                        , appleBaseGuideEntity.middleCategoryName
                        , appleBaseGuideEntity.smallCategoryCode
                        , appleBaseGuideEntity.smallCategoryName
                        , appleReqeustHistoryEntity.count()
                        , MathExpressions.round( // 비율
                                appleReqeustHistoryEntity.count().divide(
                                        JPAExpressions
                                                .select(appleHistorySub.count())
                                                .from(appleHistorySub)
                                                .where(appleHistorySub.historyRegDate.between(
                                                        yesterday.atStartOfDay(),
                                                                LocalDateTime.of(yesterday, LocalTime.MAX).withNano(0))
                                                        .and(appleHistorySub.botId.eq(appleReqeustHistoryEntity.botId))
                                                        .and(appleHistorySub.intentId.notIn(notCountBlocks))  // 통계에 포함되지 않는 블록)
                                                )
                                        )
                                        .multiply(100))
                ))
                .from(appleReqeustHistoryEntity)
                .leftJoin(appleBaseGuideEntity)
                .on(appleReqeustHistoryEntity.intentId.eq(appleBaseGuideEntity.builderBLockId))
                .where(appleReqeustHistoryEntity.historyRegDate.between(yesterday.atStartOfDay(), LocalDateTime.of(yesterday, LocalTime.MAX).withNano(0))
                        .and(appleReqeustHistoryEntity.intentId.notIn(notCountBlocks))  // 통계에 포함되지 않는 블록)
                )
                .groupBy(appleReqeustHistoryEntity.intentId, appleReqeustHistoryEntity.botId)
                .orderBy(appleReqeustHistoryEntity.botId.asc(), appleBaseGuideEntity.baseBlockCode.asc())
                .fetch();
    }

    @Override
    public Long searchBotConsultingUserCountByDay(String botId, LocalDate yesterday) { // 해당일 봇 별 고객수
        return jpaQueryFactory.select(appleReqeustHistoryEntity.appUserId.countDistinct())
                .from(appleReqeustHistoryEntity)
                .where(appleReqeustHistoryEntity.historyRegDate.between(yesterday.atStartOfDay(), LocalDateTime.of(yesterday, LocalTime.MAX).withNano(0))
                        .and(appleReqeustHistoryEntity.botId.eq(botId))
                        .and(appleReqeustHistoryEntity.intentId.notIn(notCountBlocks))
                )
                .fetchOne();
    }
}
