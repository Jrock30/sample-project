package com.sample.project.api.marketingMessage.repository;

import com.sample.project.api.marketingMessage.entity.SettlementManagementEntity;
import com.sample.project.api.marketingMessage.repository.custom.SettlementManagementCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementManagementRepository extends JpaRepository<SettlementManagementEntity, Long>, SettlementManagementCustomRepository {}