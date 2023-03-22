package com.sample.project.api.marketingMessage.service.util;

import com.sample.project.api.marketingMessage.dto.MarketingDetailExcelDto;
import com.sample.project.api.marketingMessage.dto.SettlementManagementDto;
import com.sample.project.api.marketingMessage.enums.CampaignColor;
import com.sample.project.view.ExcelConstant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MarketingUtilTest {

    @Test
    @DisplayName("마케팅 유틸의 LocalDateTime 변환 테스트")
    public void convertLocaldateTimeTest(){
        //given
        String baseMonth = "2022-11";

        // when
        LocalDateTime localDateTime = convertLocaldateTime(baseMonth,"start");

        //then
        assertAll(
                () -> assertTrue(isTime(localDateTime))
        );
    }


    public LocalDateTime convertLocaldateTime(String baseMonth, String type){

        LocalDate localDate;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth yearMonth = YearMonth.parse(baseMonth, dateTimeFormatter);

        if(type.equals("start")){
            localDate = yearMonth.atDay(1); // choose whatever day you want
            return localDate.atStartOfDay();
        }else{
            localDate = yearMonth.atEndOfMonth();
            return localDate.atTime(LocalTime.MAX);
        }
    }

    public static boolean isTime(LocalDateTime localDateTime){
        try{
            localDateTime.getHour();
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    @Test
    @DisplayName("colorCode값으로 BackGroundColor object변환 테스트")
    void colorCodeToObjectConvertor(){
        //given
        String colorCode = "#FFF8CC";
        colorCode= colorCode.replace("#","");
        System.out.println(CampaignColor.valueOf(colorCode).code());
        System.out.println(CampaignColor.valueOf(colorCode).codeName());
    }

    @Test
    @DisplayName("MON||TUE|| -> collectionString List로 변환 테스트")
    void sendIterationDayWeekToListConvertor(){
        String sendIterationDayWeek = "MON||TUE||";
        List<String> sendIterationDayWeekList = new ArrayList<>();
        String [] targetList = sendIterationDayWeek.split("\\|\\|");
        for(String item : targetList){
            if(com.sample.project.common.utils.StringUtils.hasLength(item)){
                sendIterationDayWeekList.add(item);
            }
        }
        sendIterationDayWeekList.forEach(item -> {
                System.out.println(item.toString());
        });
    }

    @Test
    @DisplayName("Object to List Test")
    void obejctToList(){
        //given
        Map<String,Object>resultMap = new HashMap<>();
        List<Map<String,Object>>mapList = new ArrayList<>();

        //area1
        List<MarketingDetailExcelDto> marketingExcelList = new ArrayList<>();
        marketingExcelList.add(MarketingDetailExcelDto.builder().botId("parker").calculateMonth(LocalDate.now()).build());
        marketingExcelList.add(MarketingDetailExcelDto.builder().botId("parker2").calculateMonth(LocalDate.now()).build());

        Map<String, Object> map = new HashMap<>();
        map.put(ExcelConstant.FILE_NAME, "settlementManagement_excel");
        map.put(ExcelConstant.HEAD, Arrays.asList("봇 ID","정산월"));

        List<List<String>> detailList = new ArrayList<>();
        List<String> data;
        for (MarketingDetailExcelDto code : marketingExcelList) {
            data = new ArrayList<>();
            data.add(code.getBotId());
            data.add(ObjectUtils.isEmpty(code.getCalculateMonth()) ? null : code.getCalculateMonth().format(DateTimeFormatter.ofPattern("yyyy.MM")));
            detailList.add(data);
        }
        map.put(ExcelConstant.BODY, detailList);
        mapList.add(map);

        // area2
        List<SettlementManagementDto>settlementManagementDtoList = new ArrayList();
        settlementManagementDtoList.add(SettlementManagementDto.builder().mallId("parkerMall").mallName("파커 몰").build());
        settlementManagementDtoList.add(SettlementManagementDto.builder().mallId("parkerMall1").mallName("파커 몰 1").build());

        map = new HashMap<>();
        map.put(ExcelConstant.FILE_NAME, "settlementManagement_excel");
        map.put(ExcelConstant.HEAD, Arrays.asList("mall ID","mall Name"));

        List<List<String>> detailList2 = new ArrayList<>();
        List<String> data2;

        for (SettlementManagementDto code : settlementManagementDtoList) {
            data2 = new ArrayList<>();
            data2.add(code.getMallId());
            data2.add(code.getMallName());
            detailList2.add(data2);
        }
        map.put(ExcelConstant.BODY, detailList2);
        mapList.add(map);
        resultMap.put("list",mapList);

        // convert
        List<Map<String,Object>> convertTargetList = (List<Map<String, Object>>) this.convertObjectToList(resultMap.get("list"));

        convertTargetList.forEach(item ->{
            this.mapToBodyList(item).forEach(itemSub->{
                itemSub.forEach(itemSubSub ->{
                    System.out.println(itemSubSub);
                });
            });
        });
    }

    private List<List<String>> mapToBodyList(Map<String, Object> model) {
        return (List<List<String>>) model.get(ExcelConstant.BODY);
    }

    public List<?> convertObjectToList(Object obj) {
        List<?> list = new ArrayList<>();
        if (obj.getClass().isArray()) {
            list = Arrays.asList((Object[])obj);
        } else if (obj instanceof Collection) {
            list = new ArrayList<>((Collection<?>)obj);
        }
        return list;
    }

}
