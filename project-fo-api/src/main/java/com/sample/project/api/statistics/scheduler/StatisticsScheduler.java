package com.project.project.api.statistics.scheduler;

import com.project.project.api.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "P1D")
//@EnableSchedulerLock(defaultLockAtMostFor = "PT5M")
@RequiredArgsConstructor
@Component
public class StatisticsScheduler {

    private final StatisticsService statisticsService;

//    @Scheduled(cron = "0 0/05 * * * ?") // 테스트 (5분마다)
    @Scheduled(cron = "0 01 00 * * *") // 매일 0시 1분에 실행
    @SchedulerLock(
            name = "scheduler_load_statistics_task",
            lockAtLeastFor = "P1D",
            lockAtMostFor = "P1D"
//            lockAtLeastFor = "PT5M",
//            lockAtMostFor = "PT5M"
    )
    public void makeStatisticsAppleSkillTask() {
        log.debug("statisticsAppleSkillTask Start");
        log.debug("LocalDateTime >>> {} ", LocalDateTime.now());

        statisticsService.makeStatisticsAppleSkillTask();
    }

   
//    @Scheduled(cron = "0 0/05 * * * ?") // 테스트 (5분마다)
//    @Scheduled(cron = "0 00 09 * * *") // 매일 오전 9시 실행
//    @SchedulerLock(
//            name = "scheduler_send_statistics_task",
//            lockAtLeastFor = "P1D",
//            lockAtMostFor = "P1D"
//            lockAtLeastFor = "PT5M",
//            lockAtMostFor = "PT5M"
//    )
    public void sendStatisticsAppleSkillTask() {
        log.debug("statisticsAppleSkillTask Start");
        log.debug("LocalDateTime >>> {} ", LocalDateTime.now());

        statisticsService.sendStatisticsAppleSkillTask();
    }


}
