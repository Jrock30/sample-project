package com.sample.project.api.marketingMessage.repository.custom;

import com.sample.project.api.marketingMessage.dto.SettlementManagementDto;
import com.sample.project.api.marketingMessage.dto.request.SearchSettlementDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SettlementManagementCustomRepository {
    Page<SettlementManagementDto> searchSettlementManagementList(SearchSettlementDto searchSettlementDto, Pageable pageable);
}
