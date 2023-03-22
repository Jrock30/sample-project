package com.sample.project.api.marketingMessage.repository;

import com.sample.project.api.marketingMessage.entity.AttachFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachFileRepository extends JpaRepository<AttachFileEntity, Long> {
}
