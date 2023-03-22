package com.sample.project.api.marketingMessage.service;

import com.sample.project.projectFoApiApplication;
import com.sample.project.api.marketingMessage.abst.sub.Apple24MarketingMessageSub;
import com.sample.project.api.marketingMessage.dto.HolidayDto;
import com.sample.project.api.marketingMessage.repository.HolidayRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = projectFoApiApplication.class)
public class HolidayTest {

    @Autowired
    private Apple24MarketingMessageSub apple24MarketingMessageSub;
    @Autowired
    private HolidayRepository holidayRepository;

    @Test
    @DisplayName("공휴일 등록 테스트")
    void saveHolidayInfo(){
        //given
        HolidayDto holidayDto = HolidayDto.builder()
                .holidayNo(0L)
                .holidayName("크리스마스")
                .date(LocalDate.now())
                .build();

        //when
        apple24MarketingMessageSub.saveHolidayInfo(holidayDto);

        //then
        assertAll(
                () -> assertTrue(holidayRepository.findAll().size()>0)
        );
    }

    @Test
    @DisplayName("공휴일 조회 테스트")
    void searchHolidyList(){
        //given
        String baseMonth = "2023-01";

        //when
        List<HolidayDto> holidayDtos = apple24MarketingMessageSub.getHolidayList();
        holidayDtos.stream().parallel().forEach(item ->{
            System.out.println(item);
        });

        //then
        assertAll(
                () -> assertTrue(holidayDtos.size()>0)
        );
    }
}
