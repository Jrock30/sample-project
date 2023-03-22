package com.project.project.api.statistics.repository;

import com.project.project.api.statistics.entity.AppleReqeustHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppleRequestHistoryRepository extends JpaRepository<AppleReqeustHistoryEntity, String>, AppleRequestHistoryRepositoryCustom {

}
