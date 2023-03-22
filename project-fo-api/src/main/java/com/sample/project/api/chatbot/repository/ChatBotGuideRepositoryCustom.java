package com.sample.project.api.apple.repository;

import com.sample.project.api.apple.dto.request.RequestSearchGuideDto;
import com.sample.project.api.apple.dto.response.ResponseGuideDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AppleGuideRepositoryCustom {

    Page<ResponseGuideDto> searchAppleByBotIdAllList(String botId, Pageable pageable);
    // 사용조건 전체
    List<ResponseGuideDto> searchAppleByBotIdAll(String botId);
    // 사용조건 1
    List<ResponseGuideDto> searchAppleByBotIdUseYAll(String botId);



    // 검색어가 없을 떄

    // 사용조건 전체
    Page<ResponseGuideDto> searchAppleByBotId(RequestSearchGuideDto searchGuideDto, String botId, Pageable pageable);
    List<ResponseGuideDto> searchAppleByBotIdALL(RequestSearchGuideDto searchGuideDto, String botId);
    List<ResponseGuideDto> searchAppleByBotIdN(RequestSearchGuideDto searchGuideDto, String botId);
    List<ResponseGuideDto> searchAppleByBotIdY(RequestSearchGuideDto searchGuideDto, String botId);
    // 사용조건 1
    Page<ResponseGuideDto> searchAppleByBotIdUseY(RequestSearchGuideDto searchGuideDto, String botId, Pageable pageable);
    // 사용조건 0
    Page<ResponseGuideDto> searchAppleByBotIdUseN(RequestSearchGuideDto searchGuideDto, String botId, Pageable pageable);


    // 검색어가 있을 때

    // 사용조건 전체
    Page<ResponseGuideDto> searchAppleByBotIdWithWordAll(RequestSearchGuideDto searchGuideDto, String botId, Pageable pageable);

    // 사용조건 1
    Page<ResponseGuideDto> searchAppleByBotIdWithWordUseY(RequestSearchGuideDto searchGuideDto, String botId, Pageable pageable);

    // 사용조건 0
    Page<ResponseGuideDto> searchAppleByBotIdWithWordUseN(RequestSearchGuideDto searchGuideDto, String botId, Pageable pageable);

}
