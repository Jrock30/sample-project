package com.sample.project.api.marketingMessage.repository.custom;

import com.sample.project.api.marketingMessage.dto.MarketingMessageResultDto;
import com.sample.project.api.marketingMessage.dto.request.SearchMessageResultDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MarketingMessageResultCustomRepository {
    Page<MarketingMessageResultDto> searchMarketingMessageResultList(SearchMessageResultDto messageResult, Pageable pageable);
    List<MarketingMessageResultDto> searchMarketingMessageResultListNoPaging(SearchMessageResultDto messageResult);
}
