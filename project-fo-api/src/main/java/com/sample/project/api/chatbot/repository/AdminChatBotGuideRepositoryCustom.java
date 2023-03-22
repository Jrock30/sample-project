package com.sample.project.api.apple.repository;

import com.sample.project.api.apple.dto.request.RequestAdminSearchGuideDto;
import com.sample.project.api.apple.dto.response.ResponseAdminGuideDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminAppleGuideRepositoryCustom {

        Page<ResponseAdminGuideDto> searchByBotIdAllList(String botId, Pageable pageable);
        // 사용조건 전체
        List<ResponseAdminGuideDto> searchByBotIdAll(String botId);
        // 사용조건 1
        List<ResponseAdminGuideDto> searchByBotIdUseYAll(String botId);



    // 검색어가 없을 떄

        // 사용조건 전체
        Page<ResponseAdminGuideDto> searchByBotId(RequestAdminSearchGuideDto searchGuideDto, String botId, Pageable pageable);
        List<ResponseAdminGuideDto> searchByBotIdALL(RequestAdminSearchGuideDto searchGuideDto, String botId);
        List<ResponseAdminGuideDto> searchByBotIdN(RequestAdminSearchGuideDto searchGuideDto, String botId);
        List<ResponseAdminGuideDto> searchByBotIdY(RequestAdminSearchGuideDto searchGuideDto, String botId);
        // 사용조건 1
        Page<ResponseAdminGuideDto> searchByBotIdUseY(RequestAdminSearchGuideDto searchGuideDto, String botId, Pageable pageable);
        // 사용조건 0
        Page<ResponseAdminGuideDto> searchByBotIdUseN(RequestAdminSearchGuideDto searchGuideDto, String botId, Pageable pageable);


    // 검색어가 있을 때

        // 사용조건 전체
        Page<ResponseAdminGuideDto> searchByBotIdWithWordAll(RequestAdminSearchGuideDto searchGuideDto, String botId, Pageable pageable);

        // 사용조건 1
        Page<ResponseAdminGuideDto> searchByBotIdWithWordUseY(RequestAdminSearchGuideDto searchGuideDto, String botId, Pageable pageable);

        // 사용조건 0
        Page<ResponseAdminGuideDto> searchByBotIdWithWordUseN(RequestAdminSearchGuideDto searchGuideDto, String botId, Pageable pageable);


}
