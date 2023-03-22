package com.sample.project.api.marketingMessage.repository.custom;

import com.sample.project.api.marketingMessage.dto.reponse.CampaignListResponseDto;
import com.sample.project.api.marketingMessage.dto.request.SearchCampaignListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CampaignCustomRepository {
    Page<CampaignListResponseDto> searchCampaignList(SearchCampaignListDto searchCampaignListDto, Pageable pageable);
}
