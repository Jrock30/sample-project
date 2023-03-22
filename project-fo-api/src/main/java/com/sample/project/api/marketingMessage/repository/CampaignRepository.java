package com.sample.project.api.marketingMessage.repository;

import com.sample.project.api.marketingMessage.entity.CampaignEntity;
import com.sample.project.api.marketingMessage.repository.custom.CampaignCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CampaignRepository extends JpaRepository<CampaignEntity, Long>, CampaignCustomRepository {
        List<CampaignEntity> findAllByBotIdOrderByCampaignNoDesc(String botId);
}
