package com.sample.project.common.jpa.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AdminBaseDto {

    @JsonIgnore
    @Schema(description = "등록자")
    private String regId;


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "등록일시")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime regDate;

    @JsonIgnore
    @Schema(description = "수정자")
    private String updId;

    @JsonIgnore
    @Schema(description = "수정일시")
    private LocalDateTime updDate;
}
