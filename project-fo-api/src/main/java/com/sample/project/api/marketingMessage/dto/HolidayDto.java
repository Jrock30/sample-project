package com.sample.project.api.marketingMessage.dto;

import com.sample.project.api.marketingMessage.entity.HolidayEntity;
import com.sample.project.common.jpa.intf.ChangeableToFromEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class HolidayDto implements ChangeableToFromEntity<HolidayEntity> {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "캠페인 시퀀스")
    private Long holidayNo;

    @NotEmpty(message = "공휴일 명은 필수 값입니다.")
    @Schema(description = "공휴일 명")
    private String holidayName;

    @NotNull(message = "공휴일 날짜는 필수 값입니다.")
    @Schema(description = "공휴일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate date;
    public HolidayDto(HolidayEntity holidayEntity){
        from(holidayEntity);
    }

    @Override
    public HolidayEntity to() {
        return HolidayEntity.builder()
                .holidayNo(holidayNo)
                .holidayName(holidayName)
                .date(date)
                .build();
    }


    @Override
    public void from(HolidayEntity holidayEntity) {
        this.holidayNo = holidayEntity.getHolidayNo();
        this.holidayName = holidayEntity.getHolidayName();
        this.date = holidayEntity.getDate();
    }
}
