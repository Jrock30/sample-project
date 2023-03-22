package com.sample.project.api.apple.repository;


import com.sample.project.api.apple.entity.AppleGuideEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppleGuideRepository extends JpaRepository<AppleGuideEntity, String>, JpaSpecificationExecutor<AppleGuideEntity>, AdminAppleGuideRepositoryCustom, AppleGuideRepositoryCustom {
    Optional<AppleGuideEntity> findByBotIdAndBaseBlockCode(String botId, String baseBlockCode);

    List<AppleGuideEntity> findAllByBotIdAndBaseBlockCode(String botId, String baseBlockCode);

    List<AppleGuideEntity> findAllByBotId(String botId);
}