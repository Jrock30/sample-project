package com.sample.project.api.marketingMessage.repository;

import com.sample.project.api.marketingMessage.entity.MarketingMessageResultEntity;
import com.sample.project.api.marketingMessage.repository.custom.MarketingMessageResultCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MarketingMessageResultRepository extends JpaRepository<MarketingMessageResultEntity, Long>, MarketingMessageResultCustomRepository {

    Optional<MarketingMessageResultEntity> findByMessageSendId (Long messageSendId);

}
