package com.project.project.api.statistics.repository;

import com.project.project.api.statistics.dto.AppleHistoryDto;

import java.time.LocalDate;
import java.util.List;

public interface AppleRequestHistoryRepositoryCustom {

    List<AppleHistoryDto> searchAppleDayHistory(LocalDate yesterday);

    Long searchBotConsultingUserCountByDay(String botId, LocalDate yesterday);
}
