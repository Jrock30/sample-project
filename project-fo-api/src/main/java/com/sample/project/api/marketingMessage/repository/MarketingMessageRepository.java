package com.sample.project.api.marketingMessage.repository;

import com.sample.project.api.marketingMessage.entity.MarketingMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketingMessageRepository extends JpaRepository<MarketingMessageEntity, Long> {
}