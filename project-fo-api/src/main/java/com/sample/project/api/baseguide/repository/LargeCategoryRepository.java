package com.sample.project.api.baseguide.repository;

import com.sample.project.api.baseguide.entity.LargeCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LargeCategoryRepository extends JpaRepository<LargeCategoryEntity, String> {
    LargeCategoryEntity findByLargeCategoryCode(String largeCategoryCode);
}
