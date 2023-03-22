package com.project.project.api.statistics.repository;

import com.project.project.api.statistics.dto.AppleBlockInquiryCategoryStatisticsDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;

import static com.project.project.api.statistics.entity.QAppleBlockStatisticsEntity.appleBlockStatisticsEntity;

@Slf4j
@RequiredArgsConstructor
public class AppleBlockStatisticsRepositoryImpl implements AppleBlockStatisticsRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public Long searchStatisticsDateCount(LocalDate yesterday) { // 집계일자 로우 체크
        return jpaQueryFactory
                .select(appleBlockStatisticsEntity.count())
                .from(appleBlockStatisticsEntity)
                .where(appleBlockStatisticsEntity.statisticsDate.eq(yesterday))
                .fetchOne();
    }

    @Override
    public List<AppleBlockInquiryCategoryStatisticsDto> searchBlockInquiryCategoryStatisticsByDate(LocalDate yesterday) {
        return jpaQueryFactory.select(Projections.constructor(AppleBlockInquiryCategoryStatisticsDto.class,
                                appleBlockStatisticsEntity.botId
                                , appleBlockStatisticsEntity.statisticsDate
                                , appleBlockStatisticsEntity.inquiryCategoryCode
                                , appleBlockStatisticsEntity.totalCount.sum()
                                , appleBlockStatisticsEntity.totalRate.sum()
                ))
                .from(appleBlockStatisticsEntity)
                .where(appleBlockStatisticsEntity.statisticsDate.eq(yesterday))
                .groupBy(appleBlockStatisticsEntity.inquiryCategoryCode, appleBlockStatisticsEntity.botId)
                .fetch();
    }
}
