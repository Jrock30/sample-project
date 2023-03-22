package com.sample.project.api.baseguide.repository;

import com.sample.project.api.baseguide.entity.AppleBaseGuideEntity;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminBaseGuideRepository extends JpaRepository<AppleBaseGuideEntity, String> {

    Optional<Page<AppleBaseGuideEntity>> findAllByGuideContentContaining(String searchWord,
                                                                      Pageable pageable);

    Optional<Page<AppleBaseGuideEntity>> findAllByLargeCategoryCode(String largeCategoryCode,
                                                                      Pageable pageable);
    Optional<Page<AppleBaseGuideEntity>> findAllByLargeCategoryCodeAndGuideContentContaining(String largeCategoryCode,
                                                                                               String searchWord,
                                                                                               Pageable pageable);

    @Query("select bg from AppleBaseGuideEntity bg where bg.largeCategoryCode=:largeCategoryCode " +
            " and ( bg.largeCategoryName like %:largeCategoryName% or bg.middleCategoryName like %:middleCategoryName% or bg.smallCategoryName  like %:smallCategoryName%) ")
    Optional<Page<AppleBaseGuideEntity>> findAllByLCodeAndNameContains(@Param("largeCategoryCode") String largeCategoryCode,
                                                                                                                                                               @Param("largeCategoryName") String largeCategoryName,
                                                                                                                                                               @Param("middleCategoryName") String middleCategoryName,
                                                                                                                                                               @Param("smallCategoryName") String smallCategoryName,
                                                                                                                                                               Pageable pageable);

    Optional<Page<AppleBaseGuideEntity>> findAllByLargeCategoryCodeAndMiddleCategoryCode(String largeCategoryCode,
                                                                                        String middleCategoryCode,
                                                                                        Pageable pageable);
    Optional<Page<AppleBaseGuideEntity>> findAllByLargeCategoryCodeAndMiddleCategoryCodeAndGuideContentContaining(String largeCategoryCode,
                                                                                                                 String middleCategoryCode,
                                                                                                                 String searchWord,
                                                                                                                 Pageable pageable);

    @Query("select bg from AppleBaseGuideEntity bg where bg.largeCategoryCode=:largeCategoryCode and bg.middleCategoryCode=:middleCategoryCode" +
            " and ( bg.largeCategoryName like %:largeCategoryName% or bg.middleCategoryName like %:middleCategoryName% or bg.smallCategoryName  like %:smallCategoryName%) ")
    Optional<Page<AppleBaseGuideEntity>> findAllByLMCodeAndNameContains(@Param("largeCategoryCode") String largeCategoryCode,
                                                                          @Param("middleCategoryCode") String middleCategoryCode,
                                                                          @Param("largeCategoryName") String largeCategoryName,
                                                                          @Param("middleCategoryName") String middleCategoryName,
                                                                          @Param("smallCategoryName") String smallCategoryName,
                                                                          Pageable pageable);

    Optional<Page<AppleBaseGuideEntity>> findAllByLargeCategoryCodeAndMiddleCategoryCodeAndSmallCategoryCode(String largeCategoryCode,
                                                                                                            String middleCategoryCode,
                                                                                                            String smallCategoryCode,
                                                                                                            Pageable pageable);
    Optional<Page<AppleBaseGuideEntity>> findAllByLargeCategoryCodeAndMiddleCategoryCodeAndSmallCategoryCodeAndGuideContentContaining(String largeCategoryCode,
                                                                                                                                     String middleCategoryCode,
                                                                                                                                     String smallCategoryCode,
                                                                                                                                     String searchWord,
                                                                                                                                     Pageable pageable);

    @Query("select bg from AppleBaseGuideEntity bg where bg.largeCategoryCode=:largeCategoryCode and bg.middleCategoryCode=:middleCategoryCode and bg.smallCategoryCode=:smallCategoryCode" +
            " and ( bg.largeCategoryName like %:largeCategoryName% or bg.middleCategoryName like %:middleCategoryName% or bg.smallCategoryName  like %:smallCategoryName%) ")
    Optional<Page<AppleBaseGuideEntity>> findAllByLMSCodeAndNameContains(@Param("largeCategoryCode") String largeCategoryCode,
                                                                           @Param("middleCategoryCode") String middleCategoryCode,
                                                                           @Param("smallCategoryCode") String smallCategoryCode,
                                                                           @Param("largeCategoryName") String largeCategoryName,
                                                                           @Param("middleCategoryName") String middleCategoryName,
                                                                           @Param("smallCategoryName") String smallCategoryName,
                                                                           Pageable pageable);

    AppleBaseGuideEntity findByBaseBlockCode(String baseBlockCode);
}
