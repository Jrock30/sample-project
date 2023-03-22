package com.sample.project.api.baseguide.repository;

import com.sample.project.api.baseguide.entity.SmallCategoryEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SmallCategoryRepository extends CrudRepository<SmallCategoryEntity, String> {
    Optional<List<SmallCategoryEntity>> findAllByLargeCategoryCodeAndMiddleCategoryCode(String largeCatCode, String middleCatCode);
    SmallCategoryEntity findBySmallCategoryCode(String smallCategoryCode);

}
