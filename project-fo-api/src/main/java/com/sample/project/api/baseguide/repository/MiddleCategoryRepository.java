package com.sample.project.api.baseguide.repository;

import com.sample.project.api.baseguide.entity.MiddleCategoryEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface MiddleCategoryRepository extends CrudRepository<MiddleCategoryEntity, String> {
    Optional<MiddleCategoryEntity> findByLargeCategoryCodeAndMiddleCategoryCode(String largeCategoryCode, String middleCategoryCode);
    Optional<List<MiddleCategoryEntity>> findAllByLargeCategoryCode(String largeCategoryCode);
    MiddleCategoryEntity findByMiddleCategoryCode(String middleCategoryCode);
}
