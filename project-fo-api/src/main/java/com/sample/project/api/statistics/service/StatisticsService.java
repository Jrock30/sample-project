package com.project.project.api.statistics.service;

import com.project.project.api.statistics.dto.*;
import com.project.project.api.statistics.entity.AppleBlockStatisticsEntity;
import com.project.project.api.statistics.entity.AppleDomainStatisticsEntity;
import com.project.project.api.statistics.entity.AppleStatisticsEntity;
import com.project.project.api.statistics.entity.AppleReqeustHistoryEntity;
import com.project.project.api.statistics.property.Cafe24Propertey;
import com.project.project.api.statistics.repository.*;
import com.project.project.common.dto.BizTalkBaseTemplate;
import com.project.project.common.dto.BizTalkMmsBaseTemplate;
import com.project.project.common.dto.ResponseBizTalkBaseDto;
import com.project.project.common.enums.CountryCodeType;
import com.project.project.common.exception.CustomException;
import com.project.project.common.property.AccessProperty;
import com.project.project.common.property.BizTalkTemplateProperty;
import com.project.project.common.service.WebClientService;
import com.project.project.common.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.project.project.api.statistics.type.AppleDomainType.*;
import static com.project.project.api.statistics.type.AppleScenarioLargeCategoryType.*;
import static com.project.project.api.statistics.type.AppleScenarioSmallCategoryType.*;
import static com.project.project.common.type.ResponseErrorCode.FAIL_500;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsService {

    /**
     * @desc : 챗봇 스킬서버에 적재 된 로그를 통해 일 스케쥴링으로 통계를 만든다.
     */
    @Transactional(rollbackFor = Exception.class)
    public void makeStatisticsAppleSkillTask() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        List<AppleHistoryDto> appleHistoryList = appleRequestHistoryRepository.searchAppleDayHistory(yesterday);

        // 어제자
        Long yesterdayStatisticsCount = appleBlockStatisticsRepository.searchStatisticsDateCount(yesterday);
        log.info("AppleHistoryList Count >>> {} ", appleHistoryList.size());

        if (yesterdayStatisticsCount > 0) { // 집계내역이 있으면
            log.error("========================================");
            log.error("이미 집계 내역이 존재 합니다.");
            log.error("========================================");
        } else { // 집계내역이 없으면
          

            //////////////////////////////////////////////////////
            // 통계 업무 구분 적재 Start
            //////////////////////////////////////////////////////

            // 도움말 시나리오 응답 봇 별 대분류 분류 / 메인 시나리오 응답 봇별 체크
            Map<String, Map<String, List<AppleHistoryDto>>> blockLargeCategoryListMap = appleHistoryList.stream()
                    .filter(m -> m.getLargeCategoryCode() != null)
                    .collect(Collectors.groupingBy(AppleHistoryDto::getBotId
                            , Collectors.groupingBy(AppleHistoryDto::getLargeCategoryCode)));

            for (String botId : blockLargeCategoryListMap.keySet()) {

                Map<String, List<AppleHistoryDto>> botAppleHistoryDtoListMap = blockLargeCategoryListMap.get(botId);

                for (String largeCategoryCode : botAppleHistoryDtoListMap.keySet()) {
                    List<AppleHistoryDto> appleHistoryDtoList = botAppleHistoryDtoListMap.get(largeCategoryCode);

                    long sumBlockCount = appleHistoryDtoList.stream().mapToLong(AppleHistoryDto::getBlockCount).sum();
                    BigDecimal sumBlockRate = BigDecimal.valueOf(appleHistoryDtoList.stream().mapToLong(AppleHistoryDto::getBlockRate).sum());

                    AppleDomainStatisticsEntity appleDomainStatisticsEntity = AppleDomainStatisticsEntity.builder()
                            .botId(botId)
                            .largeCategoryCode(largeCategoryCode)
                            .largeCategoryName(appleHistoryDtoList.get(0).getLargeCategoryName())
                            .statisticsDate(yesterday)
                            .totalCount(sumBlockCount)
                            .totalRate(sumBlockRate)
                            .build();
                    appleDomainStatisticsRepository.save(appleDomainStatisticsEntity);
                }
            }
            //////////////////////////////////////////////////////
            // 통계 적재(문의) Start
            //////////////////////////////////////////////////////

            // 블록 집계 -> 문의 통계 별 집계 조회
            List<AppleBlockInquiryCategoryStatisticsDto> appleBlockInquiryCategoryStatisticsList =
                    appleBlockStatisticsRepository.searchBlockInquiryCategoryStatisticsByDate(yesterday);

            log.debug("blockInquiryCategoryListMap >> {}", appleBlockInquiryCategoryStatisticsList);

            // 봇 ID 별 그룹핑
            Map<String, List<AppleBlockInquiryCategoryStatisticsDto>> BlockInquiryCategoryStatisticsListBotIdGroup = appleBlockInquiryCategoryStatisticsList.stream()
                    .collect(Collectors.groupingBy(AppleBlockInquiryCategoryStatisticsDto::getBotId));

            // 봇 ID 별 (문의) 통계 적재
            for (String botId : BlockInquiryCategoryStatisticsListBotIdGroup.keySet()) {
                // 봇 별 문의 통계 목록
                List<AppleBlockInquiryCategoryStatisticsDto> appleBlockInquiryCategoryStatisticsGroupList = BlockInquiryCategoryStatisticsListBotIdGroup.get(botId);

                // 총 문의 집계 건수
                long totalCount = appleBlockInquiryCategoryStatisticsGroupList.stream()
                        .mapToLong(AppleBlockInquiryCategoryStatisticsDto::getTotalCount).sum();
                long processCount = 0;  // 처리건수
                long viewCount    = 0;  // 조회건수
                long guideCount   = 0;  // 안내건수
                long costSavings  = 0;  // 절감비용
                // 봇 별 상담 고객수
                long consultingUserCount = appleRequestHistoryRepository.searchBotConsultingUserCountByDay(botId, yesterday);

                // 문의 통계 목록 계산
                for (AppleBlockInquiryCategoryStatisticsDto inquiryCategoryStatisticsDto : appleBlockInquiryCategoryStatisticsGroupList) {
                    String inquiryCategoryCode = inquiryCategoryStatisticsDto.getInquiryCategoryCode();
                    long inquiryCategoryTotalCount = inquiryCategoryStatisticsDto.getTotalCount();

                    if (inquiryCategoryCode.equals(DOMAIN_GUIDE.code())) { // 단순 안내 (도움말 시나리오)
                        guideCount = inquiryCategoryTotalCount;
                        costSavings += guideCount * 1300 * 0.5;
                    } else if (inquiryCategoryCode.equals(DOMAIN_VIEW.code())) { // 조회 완료 (메인 시나리오)
                        viewCount = inquiryCategoryTotalCount;
                        costSavings += viewCount * 1300 * 0.5;
                    } else if (inquiryCategoryCode.equals(DOMAIN_PROCESSING.code())) { // 처리 완료 (메인 시나리오)
                        processCount = inquiryCategoryTotalCount;
                        costSavings += viewCount * 1300;
                    }
                }

                AppleStatisticsEntity appleStatisticsEntity = AppleStatisticsEntity.builder()
                        .botId(botId)
                        .statisticsDate(yesterday)
                        .consultingUserCount(consultingUserCount)
                        .totalCount(totalCount)
                        .processCount(processCount)
                        .viewCount(viewCount)
                        .guideCount(guideCount)
                        .costSavings(costSavings)
                        .build();
                appleStatisticsRepository.save(appleStatisticsEntity);
            }

            //////////////////////////////////////////////////////
            // 통계 적재 End
            //////////////////////////////////////////////////////
        }
    }

}
