package com.sample.project.api.apple.service;

import com.sample.project.api.baseguide.dto.response.ResponseLargeCategoryDto;
import com.sample.project.api.baseguide.dto.response.ResponseMiddleCategoryDto;
import com.sample.project.api.baseguide.dto.response.ResponseSmallCategoryDto;
import com.sample.project.api.baseguide.entity.LargeCategoryEntity;
import com.sample.project.api.baseguide.entity.MiddleCategoryEntity;
import com.sample.project.api.baseguide.entity.SmallCategoryEntity;
import com.sample.project.api.baseguide.repository.LargeCategoryRepository;
import com.sample.project.api.baseguide.repository.MiddleCategoryRepository;
import com.sample.project.api.baseguide.repository.SmallCategoryRepository;
import com.sample.project.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.sample.project.common.type.ResponseErrorCode.FAIL_7012;


@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final LargeCategoryRepository largeCategoryRepository;
    private final MiddleCategoryRepository middleCategoryRepository;
    private final SmallCategoryRepository smallCategoryRepository;
    /**
     * 대분류 selectbox
     * @return
     */
    public List<ResponseLargeCategoryDto> getLargeCatList() {
        List<LargeCategoryEntity> largeCatLists = largeCategoryRepository.findAll();

        List<ResponseLargeCategoryDto> largeCategoryDtoList = new ArrayList<>();
        for (LargeCategoryEntity lce:largeCatLists) {
            largeCategoryDtoList.add(
                    ResponseLargeCategoryDto.builder()
                            .largeCategoryCode(lce.getLargeCategoryCode())
                            .largeCategoryName(lce.getLargeCategoryName())
                            .build()
            );
        }
        return largeCategoryDtoList;
    }

    /**
     * 중분류 selectbox
     * @return
     */
    public List<ResponseMiddleCategoryDto> getMiddleCatList(String largeCatCode) {
        List<MiddleCategoryEntity> middleCatLists = middleCategoryRepository.findAllByLargeCategoryCode(largeCatCode).orElseThrow(() -> new CustomException(FAIL_7012.message(), HttpStatus.INTERNAL_SERVER_ERROR));;

        List<ResponseMiddleCategoryDto> middleCategoryDtoList = new ArrayList<>();
        for (MiddleCategoryEntity mce : middleCatLists) {
            middleCategoryDtoList.add(ResponseMiddleCategoryDto.builder()
                    .middleCategoryCode(mce.getMiddleCategoryCode())
                    .middleCategoryName(mce.getMiddleCategoryName())
                    .build());
        }
        return middleCategoryDtoList;
    }

    /**
     * 소분류 selectbox
     * @return
     */
    public List<ResponseSmallCategoryDto> getSmallCatList(String largeCatCode, String middleCatCode) {
        List<SmallCategoryEntity> middleCatLists = smallCategoryRepository.findAllByLargeCategoryCodeAndMiddleCategoryCode(largeCatCode, middleCatCode).orElseThrow(() -> new CustomException(FAIL_7012.message(), HttpStatus.INTERNAL_SERVER_ERROR));

        List<ResponseSmallCategoryDto> smallCategoryDtoList = new ArrayList<>();
        for (SmallCategoryEntity sce : middleCatLists) {
            smallCategoryDtoList.add(ResponseSmallCategoryDto.builder()
                    .smallCategoryCode(sce.getSmallCategoryCode())
                    .smallCategoryName(sce.getSmallCategoryName())
                    .build());
        }
        return smallCategoryDtoList;
    }
}
