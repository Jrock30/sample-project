package com.sample.project.api.apple.repository;


import com.sample.project.api.apple.dto.request.RequestAdminSearchGuideDto;
import com.sample.project.api.apple.dto.request.RequestSearchGuideDto;
import com.sample.project.api.apple.dto.response.ResponseAdminGuideDto;
import com.sample.project.api.apple.dto.response.ResponseGuideDto;
import com.sample.project.api.apple.entity.AppleGuideEntity;
import com.sample.project.api.apple.enums.SearchAppleGuideType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.sample.project.api.apple.entity.QAppleBaseGuideEntity.appleBaseGuideEntity;
import static com.sample.project.api.apple.entity.QAppleGuideEntity.appleGuideEntity;

@Slf4j
@RequiredArgsConstructor
public class AppleGuideRepositoryImpl implements AdminAppleGuideRepositoryCustom, AppleGuideRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    // 운영자 도움말 검색

    // 검색어 없이


    @Override
    public Page<ResponseGuideDto> searchAppleByBotIdAllList(String botId, Pageable pageable) {
        List<ResponseGuideDto> adminGuideList = jpaQueryFactory.select(Projections.constructor(ResponseGuideDto.class
                        , appleGuideEntity.baseBlockCode
                        , appleGuideEntity.largeCategoryName
                        , appleGuideEntity.middleCategoryName
                        , appleGuideEntity.smallCategoryName
                        , appleGuideEntity.guideContent
                        , appleGuideEntity.useYn
                        , appleGuideEntity.buttonYn
                ))
                .from(appleGuideEntity)
                .where(setAppleConditionBotIdAll(botId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(adminGuideList, pageable, adminGuideList.size());
    }

    @Override
    public List<ResponseGuideDto> searchAppleByBotIdAll(String botId) {
        return jpaQueryFactory.select(Projections.constructor(ResponseGuideDto.class
                        , appleGuideEntity.baseBlockCode
                        , appleGuideEntity.largeCategoryName
                        , appleGuideEntity.middleCategoryName
                        , appleGuideEntity.smallCategoryName
                        , appleGuideEntity.guideContent
                        , appleGuideEntity.useYn
                        , appleGuideEntity.buttonYn
                ))
                .from(appleGuideEntity)
                .where(setAppleConditionBotIdAll(botId))
                .fetch();
    }

    @Override
    public List<ResponseGuideDto> searchAppleByBotIdUseYAll(String botId) {
        return jpaQueryFactory.select(Projections.constructor(ResponseGuideDto.class
                        , appleGuideEntity.baseBlockCode
                        , appleGuideEntity.largeCategoryName
                        , appleGuideEntity.middleCategoryName
                        , appleGuideEntity.smallCategoryName
                        , appleGuideEntity.guideContent
                        , appleGuideEntity.useYn
                        , appleGuideEntity.buttonYn
                ))
                .from(appleGuideEntity)
                .where(setAppleConditionBotIdAllUseY(botId))
                .fetch();
    }
    // WHERE절 조건
    private BooleanBuilder setAppleConditionBotIdAllUseY(String botId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                .and(appleGuideEntity.useYn.eq(1));
        return booleanBuilder;
    }
    // WHERE절 조건
    private BooleanBuilder setAppleConditionBotIdAll(String botId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(appleGuideEntity.botId.eq(botId));
        return booleanBuilder;
    }

    // 검색어가 없을 때
    // 사용조건 전체
    @Override
    public Page<ResponseGuideDto> searchAppleByBotId(RequestSearchGuideDto searchGuideDto, String botId, Pageable pageable) {
        List<ResponseGuideDto> adminGuideList = jpaQueryFactory.select(Projections.constructor(ResponseGuideDto.class
                        , appleGuideEntity.baseBlockCode
                        , appleGuideEntity.largeCategoryName
                        , appleGuideEntity.middleCategoryName
                        , appleGuideEntity.smallCategoryName
                        , appleGuideEntity.guideContent
                        , appleGuideEntity.useYn
                        , appleGuideEntity.buttonYn
                ))
                .from(appleGuideEntity)
                .where(setAppleConditionWithoutAllUse(searchGuideDto, botId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(adminGuideList, pageable, adminGuideList.size());
    }
    @Override
    public List<ResponseGuideDto> searchAppleByBotIdALL(RequestSearchGuideDto searchGuideDto, String botId) {
        return jpaQueryFactory.select(Projections.constructor(ResponseGuideDto.class
                        , appleGuideEntity.baseBlockCode
                        , appleGuideEntity.largeCategoryName
                        , appleGuideEntity.middleCategoryName
                        , appleGuideEntity.smallCategoryName
                        , appleGuideEntity.guideContent
                        , appleGuideEntity.useYn
                        , appleGuideEntity.buttonYn
                ))
                .from(appleGuideEntity)
                .where(setAppleConditionWithoutAllUse(searchGuideDto, botId))
                .fetch();
    }
    @Override
    public List<ResponseGuideDto> searchAppleByBotIdN(RequestSearchGuideDto searchGuideDto, String botId) {
        return jpaQueryFactory.select(Projections.constructor(ResponseGuideDto.class
                        , appleGuideEntity.baseBlockCode
                        , appleGuideEntity.largeCategoryName
                        , appleGuideEntity.middleCategoryName
                        , appleGuideEntity.smallCategoryName
                        , appleGuideEntity.guideContent
                        , appleGuideEntity.useYn
                        , appleGuideEntity.buttonYn
                ))
                .from(appleGuideEntity)
                .where(setAppleConditionWithoutAllN(searchGuideDto, botId))
                .fetch();
    }
    @Override
    public List<ResponseGuideDto> searchAppleByBotIdY(RequestSearchGuideDto searchGuideDto, String botId) {
        return jpaQueryFactory.select(Projections.constructor(ResponseGuideDto.class
                        , appleGuideEntity.baseBlockCode
                        , appleGuideEntity.largeCategoryName
                        , appleGuideEntity.middleCategoryName
                        , appleGuideEntity.smallCategoryName
                        , appleGuideEntity.guideContent
                        , appleGuideEntity.useYn
                        , appleGuideEntity.buttonYn
                ))
                .from(appleGuideEntity)
                .where(setAppleConditionWithoutAllY(searchGuideDto, botId))
                .fetch();
    }

    // 검색어가 없을 때
    // 사용조건 1
    @Override
    public Page<ResponseGuideDto> searchAppleByBotIdUseY(RequestSearchGuideDto searchGuideDto, String botId, Pageable pageable) {
        List<ResponseGuideDto> adminGuideList = jpaQueryFactory.select(Projections.constructor(ResponseGuideDto.class
                        , appleGuideEntity.baseBlockCode
                        , appleGuideEntity.largeCategoryName
                        , appleGuideEntity.middleCategoryName
                        , appleGuideEntity.smallCategoryName
                        , appleGuideEntity.guideContent
                        , appleGuideEntity.useYn
                        , appleGuideEntity.buttonYn
                ))
                .from(appleGuideEntity)
                .where(setAppleConditionWithoutAllUseY(searchGuideDto, botId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(adminGuideList, pageable, adminGuideList.size());
    }

    // 검색어가 없을 때
    // 사용조건 0
    @Override
    public Page<ResponseGuideDto> searchAppleByBotIdUseN(RequestSearchGuideDto searchGuideDto, String botId, Pageable pageable) {
        List<ResponseGuideDto> adminGuideList = jpaQueryFactory.select(Projections.constructor(ResponseGuideDto.class
                        , appleGuideEntity.baseBlockCode
                        , appleGuideEntity.largeCategoryName
                        , appleGuideEntity.middleCategoryName
                        , appleGuideEntity.smallCategoryName
                        , appleGuideEntity.guideContent
                        , appleGuideEntity.useYn
                        , appleGuideEntity.buttonYn
                ))
                .from(appleGuideEntity)
                .where(setAppleConditionWithoutAllUseN(searchGuideDto, botId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        JPQLQuery<AppleGuideEntity> count = jpaQueryFactory
                .selectFrom(appleGuideEntity)
                .where(setAppleConditionWithoutAllUseN(searchGuideDto, botId));

        return PageableExecutionUtils.getPage(adminGuideList, pageable, count::fetchCount);
    }


    // 검색어가 있을 때
    // 사용조건 전체
    @Override
    public Page<ResponseGuideDto> searchAppleByBotIdWithWordAll(RequestSearchGuideDto searchGuideDto, String botId, Pageable pageable) {
        List<ResponseGuideDto> adminGuideList = new ArrayList<>();
        int searchWordCondition = searchGuideDto.getSearchWordCondition();

        if (searchWordCondition == 0) {
            adminGuideList = jpaQueryFactory.select(Projections.constructor(ResponseGuideDto.class
                            , appleGuideEntity.baseBlockCode
                            , appleGuideEntity.largeCategoryName
                            , appleGuideEntity.middleCategoryName
                            , appleGuideEntity.smallCategoryName
                            , appleGuideEntity.guideContent
                            , appleGuideEntity.useYn
                            , appleGuideEntity.buttonYn
                    ))
                    .from(appleGuideEntity)
                    .where(setAppleConditionWithContentAll(searchGuideDto, botId))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
            return new PageImpl<>(adminGuideList, pageable, adminGuideList.size());
        } else if (searchWordCondition == 1) {
            adminGuideList = jpaQueryFactory.select(Projections.constructor(ResponseGuideDto.class
                            , appleGuideEntity.baseBlockCode
                            , appleGuideEntity.largeCategoryName
                            , appleGuideEntity.middleCategoryName
                            , appleGuideEntity.smallCategoryName
                            , appleGuideEntity.guideContent
                            , appleGuideEntity.useYn
                            , appleGuideEntity.buttonYn
                    ))
                    .from(appleGuideEntity)
                    .where(setAppleConditionWithCatNameAll(searchGuideDto, botId))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
            return new PageImpl<>(adminGuideList, pageable, adminGuideList.size());
        }


        return new PageImpl<>(adminGuideList, pageable, 0);
    }

    // 검색어가 있을 때
    // 사용조건 1
    @Override
    public Page<ResponseGuideDto> searchAppleByBotIdWithWordUseY(RequestSearchGuideDto searchGuideDto, String botId, Pageable pageable) {
        List<ResponseGuideDto> adminGuideList = new ArrayList<>();
        int searchWordCondition = searchGuideDto.getSearchWordCondition();

        if (searchWordCondition == 0) {
            adminGuideList = jpaQueryFactory.select(Projections.constructor(ResponseGuideDto.class
                            , appleGuideEntity.baseBlockCode
                            , appleGuideEntity.largeCategoryName
                            , appleGuideEntity.middleCategoryName
                            , appleGuideEntity.smallCategoryName
                            , appleGuideEntity.guideContent
                            , appleGuideEntity.useYn
                            , appleGuideEntity.buttonYn
                    ))
                    .from(appleGuideEntity)
                    .where(setAppleConditionWithContentUseY(searchGuideDto, botId))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
            return new PageImpl<>(adminGuideList, pageable, adminGuideList.size());
        } else if (searchWordCondition == 1) {
            adminGuideList = jpaQueryFactory.select(Projections.constructor(ResponseGuideDto.class
                            , appleGuideEntity.baseBlockCode
                            , appleGuideEntity.largeCategoryName
                            , appleGuideEntity.middleCategoryName
                            , appleGuideEntity.smallCategoryName
                            , appleGuideEntity.guideContent
                            , appleGuideEntity.useYn
                            , appleGuideEntity.buttonYn
                    ))
                    .from(appleGuideEntity)
                    .where(setAppleConditionWithCatNameUseY(searchGuideDto, botId))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
            return new PageImpl<>(adminGuideList, pageable, adminGuideList.size());
        }
        return new PageImpl<>(adminGuideList, pageable, 0);
    }

    // 검색어가 있을 때
    // 사용조건 0
    @Override
    public Page<ResponseGuideDto> searchAppleByBotIdWithWordUseN(RequestSearchGuideDto searchGuideDto, String botId, Pageable pageable) {
        List<ResponseGuideDto> adminGuideList = new ArrayList<>();
        int searchWordCondition = searchGuideDto.getSearchWordCondition();
        // 도움말 응답
        if (searchWordCondition == 0) {
            adminGuideList = jpaQueryFactory.select(Projections.constructor(ResponseGuideDto.class
                            , appleGuideEntity.baseBlockCode
                            , appleGuideEntity.largeCategoryName
                            , appleGuideEntity.middleCategoryName
                            , appleGuideEntity.smallCategoryName
                            , appleGuideEntity.guideContent
                            , appleGuideEntity.useYn
                            , appleGuideEntity.buttonYn
                    ))
                    .from(appleGuideEntity)
                    .where(setAppleConditionWithContentUseN(searchGuideDto, botId))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
            return new PageImpl<>(adminGuideList, pageable, adminGuideList.size());
        } else if (searchWordCondition == 1) {
            // 분류명
            adminGuideList = jpaQueryFactory.select(Projections.constructor(ResponseGuideDto.class
                            , appleGuideEntity.baseBlockCode
                            , appleGuideEntity.largeCategoryName
                            , appleGuideEntity.middleCategoryName
                            , appleGuideEntity.smallCategoryName
                            , appleGuideEntity.guideContent
                            , appleGuideEntity.useYn
                            , appleGuideEntity.buttonYn
                    ))
                    .from(appleGuideEntity)
                    .where(setAppleConditionWithCatNameUseN(searchGuideDto, botId))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
            return new PageImpl<>(adminGuideList, pageable, adminGuideList.size());
        }
        return new PageImpl<>(adminGuideList, pageable, 0);
    }



    ///////////////////////////////////////////////////////////////////////////
    // WHERE절 조건
    // 검색어가 없을 떄
    // 사용조건 전체
    private BooleanBuilder setAppleConditionWithoutAllUse(RequestSearchGuideDto searchGuideDto, String botId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (!StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 전체 , 대중소X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId));

        } else if (!StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 전체 , 대 O, 중소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()));

        } else if (!StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 전체 , 대중 O, 소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()));

        } else if (StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 전체 , 대중소 O
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.smallCategoryCode.eq(searchGuideDto.getSmallCategoryCode()));

        }
        return booleanBuilder;
    }
    private BooleanBuilder setAppleConditionWithoutAllN(RequestSearchGuideDto searchGuideDto, String botId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (!StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 전체 , 대중소X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(0));

        } else if (!StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 전체 , 대 O, 중소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.useYn.eq(0));

        } else if (!StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 전체 , 대중 O, 소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.useYn.eq(0));

        } else if (StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 전체 , 대중소 O
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.smallCategoryCode.eq(searchGuideDto.getSmallCategoryCode()))
                    .and(appleGuideEntity.useYn.eq(0));

        }
        return booleanBuilder;
    }
    private BooleanBuilder setAppleConditionWithoutAllY(RequestSearchGuideDto searchGuideDto, String botId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (!StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 전체 , 대중소X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(1));

        } else if (!StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 전체 , 대 O, 중소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.useYn.eq(1));

        } else if (!StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 전체 , 대중 O, 소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.useYn.eq(1));

        } else if (StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 전체 , 대중소 O
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.smallCategoryCode.eq(searchGuideDto.getSmallCategoryCode()))
                    .and(appleGuideEntity.useYn.eq(1));

        }
        return booleanBuilder;
    }

    // WHERE절 조건
    // 검색어가 없을 때
    // 사용조건 1
    private BooleanBuilder setAppleConditionWithoutAllUseY(RequestSearchGuideDto searchGuideDto, String botId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (!StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 1 , 대중소X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(1));

        } else if (!StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 1 , 대 O, 중소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode())
                    .and(appleGuideEntity.useYn.eq(1)));

        } else if (!StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 1 , 대중 O, 소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.useYn.eq(1));

        } else if (StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 1 , 대중소 O
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.smallCategoryCode.eq(searchGuideDto.getSmallCategoryCode()))
                    .and(appleGuideEntity.useYn.eq(1));

        }
        return booleanBuilder;
    }

    // WHERE절 조건
    // 검색어가 없을 때
    // 사용조건 0
    private BooleanBuilder setAppleConditionWithoutAllUseN(RequestSearchGuideDto searchGuideDto, String botId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (!StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 0 , 대중소X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(searchGuideDto.getUseYn()));

        } else if (!StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 0 , 대 O, 중소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode())
                    .and(appleGuideEntity.useYn.eq(0)));

        } else if (!StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 0 , 대중 O, 소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.useYn.eq(0));

        } else if (StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 0 , 대중소 O
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.smallCategoryCode.eq(searchGuideDto.getSmallCategoryCode()))
                    .and(appleGuideEntity.useYn.eq(0));

        }
        return booleanBuilder;
    }

    // WHERE절 조건
    // 검색어 있을 때
    // 사용조건 전체
    // 도움말 응답
    private Predicate setAppleConditionWithContentAll(RequestSearchGuideDto searchGuideDto, String botId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (!StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중소X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.guideContent.contains(searchGuideDto.getSearchWord()));

        } else if (!StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대 O, 중소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.guideContent.contains(searchGuideDto.getSearchWord()));

        } else if (!StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중 O, 소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.guideContent.contains(searchGuideDto.getSearchWord()));

        } else if (StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중소 O
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.smallCategoryCode.eq(searchGuideDto.getSmallCategoryCode()))
                    .and(appleGuideEntity.guideContent.contains(searchGuideDto.getSearchWord()));;

        }
        return booleanBuilder;
    }

    // WHERE절 조건
    // 검색어 있을 때
    // 사용조건 전체
    // 분류명
    private Predicate setAppleConditionWithCatNameAll(RequestSearchGuideDto searchGuideDto, String botId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (!StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중소X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryName.contains(searchGuideDto.getSearchWord())
                            .or(appleGuideEntity.middleCategoryName.contains(searchGuideDto.getSearchWord()))
                            .or(appleGuideEntity.smallCategoryName.contains(searchGuideDto.getSearchWord()))
                    );

        } else if (!StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대 O, 중소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.largeCategoryName.contains(searchGuideDto.getSearchWord())
                            .or(appleGuideEntity.middleCategoryName.contains(searchGuideDto.getSearchWord()))
                            .or(appleGuideEntity.smallCategoryName.contains(searchGuideDto.getSearchWord()))
                    );

        } else if (!StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중 O, 소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.largeCategoryName.contains(searchGuideDto.getSearchWord())
                            .or(appleGuideEntity.middleCategoryName.contains(searchGuideDto.getSearchWord()))
                            .or(appleGuideEntity.smallCategoryName.contains(searchGuideDto.getSearchWord()))
                    );

        } else if (StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중소 O
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.smallCategoryCode.eq(searchGuideDto.getSmallCategoryCode()))
                    .and(appleGuideEntity.largeCategoryName.contains(searchGuideDto.getSearchWord())
                            .or(appleGuideEntity.middleCategoryName.contains(searchGuideDto.getSearchWord()))
                            .or(appleGuideEntity.smallCategoryName.contains(searchGuideDto.getSearchWord()))
                    );

        }
        return booleanBuilder;
    }

    // WHERE절 조건
    // 검색어 있을 때
    // 사용조건 1
    // 도움말 응답
    private Predicate setAppleConditionWithContentUseY(RequestSearchGuideDto searchGuideDto, String botId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (!StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중소X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(1))
                    .and(appleGuideEntity.guideContent.contains(searchGuideDto.getSearchWord()));

        } else if (!StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대 O, 중소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(1))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.guideContent.contains(searchGuideDto.getSearchWord()));

        } else if (!StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중 O, 소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(1))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.guideContent.contains(searchGuideDto.getSearchWord()));

        } else if (StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중소 O
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(1))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.smallCategoryCode.eq(searchGuideDto.getSmallCategoryCode()))
                    .and(appleGuideEntity.guideContent.contains(searchGuideDto.getSearchWord()));
        }
        return booleanBuilder;
    }

    // WHERE절 조건
    // 검색어 있을 때
    // 사용조건 1
    // 분류명
    private Predicate setAppleConditionWithCatNameUseY(RequestSearchGuideDto searchGuideDto, String botId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (!StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중소X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(1))
                    .and(appleGuideEntity.largeCategoryName.contains(searchGuideDto.getSearchWord())
                            .or(appleGuideEntity.middleCategoryName.contains(searchGuideDto.getSearchWord()))
                            .or(appleGuideEntity.smallCategoryName.contains(searchGuideDto.getSearchWord()))
                    );

        } else if (!StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대 O, 중소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(1))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.largeCategoryName.contains(searchGuideDto.getSearchWord())
                            .or(appleGuideEntity.middleCategoryName.contains(searchGuideDto.getSearchWord()))
                            .or(appleGuideEntity.smallCategoryName.contains(searchGuideDto.getSearchWord()))
                    );

        } else if (!StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중 O, 소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(1))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.largeCategoryName.contains(searchGuideDto.getSearchWord())
                            .or(appleGuideEntity.middleCategoryName.contains(searchGuideDto.getSearchWord()))
                            .or(appleGuideEntity.smallCategoryName.contains(searchGuideDto.getSearchWord()))
                    );

        } else if (StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중소 O
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(1))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.smallCategoryCode.eq(searchGuideDto.getSmallCategoryCode()))
                    .and(appleGuideEntity.largeCategoryName.contains(searchGuideDto.getSearchWord())
                            .or(appleGuideEntity.middleCategoryName.contains(searchGuideDto.getSearchWord()))
                            .or(appleGuideEntity.smallCategoryName.contains(searchGuideDto.getSearchWord()))
                    );

        }
        return booleanBuilder;
    }

    // WHERE절 조건
    // 검색어 있을 때
    // 사용조건 0
    // 수정응답
    private Predicate setAppleConditionWithContentUseN(RequestSearchGuideDto searchGuideDto, String botId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (!StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중소X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(1))
                    .and(appleGuideEntity.guideContent.contains(searchGuideDto.getSearchWord()));

        } else if (!StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대 O, 중소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(1))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.guideContent.contains(searchGuideDto.getSearchWord()));

        } else if (!StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중 O, 소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(1))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.guideContent.contains(searchGuideDto.getSearchWord()));

        } else if (StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중소 O
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(1))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.smallCategoryCode.eq(searchGuideDto.getSmallCategoryCode()))
                    .and(appleGuideEntity.guideContent.contains(searchGuideDto.getSearchWord()));
        }
        return booleanBuilder;
    }

    // WHERE절 조건
    // 검색어 있을 때
    // 사용조건 0
    // 분류명
    private Predicate setAppleConditionWithCatNameUseN(RequestSearchGuideDto searchGuideDto, String botId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (!StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중소X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(0))
                    .and(appleGuideEntity.largeCategoryName.contains(searchGuideDto.getSearchWord())
                            .or(appleGuideEntity.middleCategoryName.contains(searchGuideDto.getSearchWord()))
                            .or(appleGuideEntity.smallCategoryName.contains(searchGuideDto.getSearchWord()))
                    );

        } else if (!StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대 O, 중소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(0))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.largeCategoryName.contains(searchGuideDto.getSearchWord())
                            .or(appleGuideEntity.middleCategoryName.contains(searchGuideDto.getSearchWord()))
                            .or(appleGuideEntity.smallCategoryName.contains(searchGuideDto.getSearchWord()))
                    );

        } else if (!StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중 O, 소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(0))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.largeCategoryName.contains(searchGuideDto.getSearchWord())
                            .or(appleGuideEntity.middleCategoryName.contains(searchGuideDto.getSearchWord()))
                            .or(appleGuideEntity.smallCategoryName.contains(searchGuideDto.getSearchWord()))
                    );

        } else if (StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중소 O
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(0))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.smallCategoryCode.eq(searchGuideDto.getSmallCategoryCode()))
                    .and(appleGuideEntity.largeCategoryName.contains(searchGuideDto.getSearchWord())
                            .or(appleGuideEntity.middleCategoryName.contains(searchGuideDto.getSearchWord()))
                            .or(appleGuideEntity.smallCategoryName.contains(searchGuideDto.getSearchWord()))
                    );

        }
        return booleanBuilder;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // 관리자 도움말 검색

    @Override
    public Page<ResponseAdminGuideDto> searchByBotIdAllList(String botId, Pageable pageable) {
        List<ResponseAdminGuideDto> adminGuideList = jpaQueryFactory.select(Projections.constructor(ResponseAdminGuideDto.class
                        , appleGuideEntity.baseBlockCode
                        , appleGuideEntity.largeCategoryName
                        , appleGuideEntity.middleCategoryName
                        , appleGuideEntity.smallCategoryName
                        , appleGuideEntity.guideContent
                        , appleBaseGuideEntity.guideContent
                        , appleGuideEntity.useYn
                        , appleGuideEntity.buttonYn
                ))
                .from(appleGuideEntity)
                .leftJoin(appleBaseGuideEntity)
                .on(appleGuideEntity.baseBlockCode
                        .eq(appleBaseGuideEntity.baseBlockCode))
                .where(setConditionBotIdAll(botId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(adminGuideList, pageable, adminGuideList.size());
    }

    @Override
    public List<ResponseAdminGuideDto> searchByBotIdAll(String botId) {
        return jpaQueryFactory.select(Projections.constructor(ResponseAdminGuideDto.class
                        , appleGuideEntity.baseBlockCode
                        , appleGuideEntity.largeCategoryName
                        , appleGuideEntity.middleCategoryName
                        , appleGuideEntity.smallCategoryName
                        , appleGuideEntity.guideContent
                        , appleBaseGuideEntity.guideContent
                        , appleGuideEntity.useYn
                        , appleGuideEntity.buttonYn
                ))
                .from(appleGuideEntity)
                .leftJoin(appleBaseGuideEntity)
                .on(appleGuideEntity.baseBlockCode
                        .eq(appleBaseGuideEntity.baseBlockCode))
                .where(setConditionBotIdAll(botId))
                .fetch();
    }

    @Override
    public List<ResponseAdminGuideDto> searchByBotIdUseYAll(String botId) {
        return jpaQueryFactory.select(Projections.constructor(ResponseAdminGuideDto.class
                        , appleGuideEntity.baseBlockCode
                        , appleGuideEntity.largeCategoryName
                        , appleGuideEntity.middleCategoryName
                        , appleGuideEntity.smallCategoryName
                        , appleGuideEntity.guideContent
                        , appleBaseGuideEntity.guideContent
                        , appleGuideEntity.useYn
                        , appleGuideEntity.buttonYn
                ))
                .from(appleGuideEntity)
                .leftJoin(appleBaseGuideEntity)
                .on(appleGuideEntity.baseBlockCode
                        .eq(appleBaseGuideEntity.baseBlockCode))
                .where(setConditionBotIdAllUseY(botId))
                .fetch();
    }
    // WHERE절 조건
    private BooleanBuilder setConditionBotIdAllUseY(String botId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                .and(appleGuideEntity.useYn.eq(1));
        return booleanBuilder;
    }
    // WHERE절 조건
    private BooleanBuilder setConditionBotIdAll(String botId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(appleGuideEntity.botId.eq(botId));
        return booleanBuilder;
    }

    // 검색어가 없을 때
    // 사용조건 전체
    @Override
    public Page<ResponseAdminGuideDto> searchByBotId(RequestAdminSearchGuideDto searchGuideDto, String botId, Pageable pageable) {
        List<ResponseAdminGuideDto> adminGuideList = jpaQueryFactory.select(Projections.constructor(ResponseAdminGuideDto.class
                        , appleGuideEntity.baseBlockCode
                        , appleGuideEntity.largeCategoryName
                        , appleGuideEntity.middleCategoryName
                        , appleGuideEntity.smallCategoryName
                        , appleGuideEntity.guideContent
                        , appleBaseGuideEntity.guideContent
                        , appleGuideEntity.useYn
                        , appleGuideEntity.buttonYn
                ))
                .from(appleGuideEntity)
                .leftJoin(appleBaseGuideEntity)
                .on(appleGuideEntity.baseBlockCode
                        .eq(appleBaseGuideEntity.baseBlockCode))
                .where(setConditionWithoutAllUse(searchGuideDto, botId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(adminGuideList, pageable, adminGuideList.size());
    }
    @Override
    public List<ResponseAdminGuideDto> searchByBotIdALL(RequestAdminSearchGuideDto searchGuideDto, String botId) {
        return jpaQueryFactory.select(Projections.constructor(ResponseAdminGuideDto.class
                        , appleGuideEntity.baseBlockCode
                        , appleGuideEntity.largeCategoryName
                        , appleGuideEntity.middleCategoryName
                        , appleGuideEntity.smallCategoryName
                        , appleGuideEntity.guideContent
                        , appleBaseGuideEntity.guideContent
                        , appleGuideEntity.useYn
                        , appleGuideEntity.buttonYn
                ))
                .from(appleGuideEntity)
                .leftJoin(appleBaseGuideEntity)
                .on(appleGuideEntity.baseBlockCode
                        .eq(appleBaseGuideEntity.baseBlockCode))
                .where(setConditionWithoutAllUse(searchGuideDto, botId))
                .fetch();
    }
    @Override
    public List<ResponseAdminGuideDto> searchByBotIdN(RequestAdminSearchGuideDto searchGuideDto, String botId) {
        return jpaQueryFactory.select(Projections.constructor(ResponseAdminGuideDto.class
                        , appleGuideEntity.baseBlockCode
                        , appleGuideEntity.largeCategoryName
                        , appleGuideEntity.middleCategoryName
                        , appleGuideEntity.smallCategoryName
                        , appleGuideEntity.guideContent
                        , appleBaseGuideEntity.guideContent
                        , appleGuideEntity.useYn
                        , appleGuideEntity.buttonYn
                ))
                .from(appleGuideEntity)
                .leftJoin(appleBaseGuideEntity)
                .on(appleGuideEntity.baseBlockCode
                        .eq(appleBaseGuideEntity.baseBlockCode))
                .where(setConditionWithoutAllN(searchGuideDto, botId))
                .fetch();
    }

    @Override
    public List<ResponseAdminGuideDto> searchByBotIdY(RequestAdminSearchGuideDto searchGuideDto, String botId) {
        return jpaQueryFactory.select(Projections.constructor(ResponseAdminGuideDto.class
                        , appleGuideEntity.baseBlockCode
                        , appleGuideEntity.largeCategoryName
                        , appleGuideEntity.middleCategoryName
                        , appleGuideEntity.smallCategoryName
                        , appleGuideEntity.guideContent
                        , appleBaseGuideEntity.guideContent
                        , appleGuideEntity.useYn
                        , appleGuideEntity.buttonYn
                ))
                .from(appleGuideEntity)
                .leftJoin(appleBaseGuideEntity)
                .on(appleGuideEntity.baseBlockCode
                        .eq(appleBaseGuideEntity.baseBlockCode))
                .where(setConditionWithoutAllY(searchGuideDto, botId))
                .fetch();
    }

    // 검색어가 없을 때
    // 사용조건 1
    @Override
    public Page<ResponseAdminGuideDto> searchByBotIdUseY(RequestAdminSearchGuideDto searchGuideDto, String botId, Pageable pageable) {
        List<ResponseAdminGuideDto> adminGuideList = jpaQueryFactory.select(Projections.constructor(ResponseAdminGuideDto.class
                        , appleGuideEntity.baseBlockCode
                        , appleGuideEntity.largeCategoryName
                        , appleGuideEntity.middleCategoryName
                        , appleGuideEntity.smallCategoryName
                        , appleGuideEntity.guideContent
                        , appleBaseGuideEntity.guideContent
                        , appleGuideEntity.useYn
                        , appleGuideEntity.buttonYn
                ))
                .from(appleGuideEntity)
                .leftJoin(appleBaseGuideEntity)
                .on(appleGuideEntity.baseBlockCode
                        .eq(appleBaseGuideEntity.baseBlockCode))
                .where(setConditionWithoutAllUseY(searchGuideDto, botId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(adminGuideList, pageable, adminGuideList.size());
    }

    // 검색어가 없을 때
    // 사용조건 0
    @Override
    public Page<ResponseAdminGuideDto> searchByBotIdUseN(RequestAdminSearchGuideDto searchGuideDto, String botId, Pageable pageable) {
        List<ResponseAdminGuideDto> adminGuideList = jpaQueryFactory.select(Projections.constructor(ResponseAdminGuideDto.class
                        , appleGuideEntity.baseBlockCode
                        , appleGuideEntity.largeCategoryName
                        , appleGuideEntity.middleCategoryName
                        , appleGuideEntity.smallCategoryName
                        , appleGuideEntity.guideContent
                        , appleBaseGuideEntity.guideContent
                        , appleGuideEntity.useYn
                        , appleGuideEntity.buttonYn
                ))
                .from(appleGuideEntity)
                .leftJoin(appleBaseGuideEntity)
                .on(appleGuideEntity.baseBlockCode
                        .eq(appleBaseGuideEntity.baseBlockCode))
                .where(setConditionWithoutAllUseN(searchGuideDto, botId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(adminGuideList, pageable, adminGuideList.size());
    }


    // 검색어가 있을 때
    // 사용조건 전체
    @Override
    public Page<ResponseAdminGuideDto> searchByBotIdWithWordAll(RequestAdminSearchGuideDto searchGuideDto, String botId, Pageable pageable) {
        List<ResponseAdminGuideDto> adminGuideList = new ArrayList<>();
        int searchWordCondition = searchGuideDto.getSearchWordCondition();

        if (SearchAppleGuideType.CONTENT.code() == searchWordCondition) {
            adminGuideList = jpaQueryFactory.select(Projections.constructor(ResponseAdminGuideDto.class
                            , appleGuideEntity.baseBlockCode
                            , appleGuideEntity.largeCategoryName
                            , appleGuideEntity.middleCategoryName
                            , appleGuideEntity.smallCategoryName
                            , appleGuideEntity.guideContent
                            , appleBaseGuideEntity.guideContent
                            , appleGuideEntity.useYn
                            , appleGuideEntity.buttonYn
                    ))
                    .from(appleGuideEntity)
                    .leftJoin(appleBaseGuideEntity)
                    .on(appleGuideEntity.baseBlockCode
                            .eq(appleBaseGuideEntity.baseBlockCode))
                    .where(setConditionWithBaseContentAll(searchGuideDto, botId))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
            return new PageImpl<>(adminGuideList, pageable, adminGuideList.size());
        } else if (SearchAppleGuideType.MODCONTENT.code() == searchWordCondition) {
            adminGuideList = jpaQueryFactory.select(Projections.constructor(ResponseAdminGuideDto.class
                            , appleGuideEntity.baseBlockCode
                            , appleGuideEntity.largeCategoryName
                            , appleGuideEntity.middleCategoryName
                            , appleGuideEntity.smallCategoryName
                            , appleGuideEntity.guideContent
                            , appleBaseGuideEntity.guideContent
                            , appleGuideEntity.useYn
                            , appleGuideEntity.buttonYn
                    ))
                    .from(appleGuideEntity)
                    .leftJoin(appleBaseGuideEntity)
                    .on(appleGuideEntity.baseBlockCode
                            .eq(appleBaseGuideEntity.baseBlockCode))
                    .where(setConditionWithContentAll(searchGuideDto, botId))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
            return new PageImpl<>(adminGuideList, pageable, adminGuideList.size());
        } else if (SearchAppleGuideType.CATNAME.code() == searchWordCondition) {
            adminGuideList = jpaQueryFactory.select(Projections.constructor(ResponseAdminGuideDto.class
                            , appleGuideEntity.baseBlockCode
                            , appleGuideEntity.largeCategoryName
                            , appleGuideEntity.middleCategoryName
                            , appleGuideEntity.smallCategoryName
                            , appleGuideEntity.guideContent
                            , appleBaseGuideEntity.guideContent
                            , appleGuideEntity.useYn
                            , appleGuideEntity.buttonYn
                    ))
                    .from(appleGuideEntity)
                    .leftJoin(appleBaseGuideEntity)
                    .on(appleGuideEntity.baseBlockCode
                            .eq(appleBaseGuideEntity.baseBlockCode))
                    .where(setConditionWithCatNameAll(searchGuideDto, botId))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
            return new PageImpl<>(adminGuideList, pageable, adminGuideList.size());
        } else if (SearchAppleGuideType.BLOCKNO.code() == searchWordCondition) {
            adminGuideList = jpaQueryFactory.select(Projections.constructor(ResponseAdminGuideDto.class
                            , appleGuideEntity.baseBlockCode
                            , appleGuideEntity.largeCategoryName
                            , appleGuideEntity.middleCategoryName
                            , appleGuideEntity.smallCategoryName
                            , appleGuideEntity.guideContent
                            , appleBaseGuideEntity.guideContent
                            , appleGuideEntity.useYn
                            , appleGuideEntity.buttonYn
                    ))
                    .from(appleGuideEntity)
                    .leftJoin(appleBaseGuideEntity)
                    .on(appleGuideEntity.baseBlockCode
                            .eq(appleBaseGuideEntity.baseBlockCode))
                    .where(setConditionWithBlockCodeAll(searchGuideDto, botId))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
            return new PageImpl<>(adminGuideList, pageable, adminGuideList.size());
        }


        return new PageImpl<>(adminGuideList, pageable, 0);
    }

    // 검색어가 있을 때
    // 사용조건 1
    @Override
    public Page<ResponseAdminGuideDto> searchByBotIdWithWordUseY(RequestAdminSearchGuideDto searchGuideDto, String botId, Pageable pageable) {
        List<ResponseAdminGuideDto> adminGuideList = new ArrayList<>();
        int searchWordCondition = searchGuideDto.getSearchWordCondition();

        if (SearchAppleGuideType.CONTENT.code() == searchWordCondition) {
            adminGuideList = jpaQueryFactory.select(Projections.constructor(ResponseAdminGuideDto.class
                            , appleGuideEntity.baseBlockCode
                            , appleGuideEntity.largeCategoryName
                            , appleGuideEntity.middleCategoryName
                            , appleGuideEntity.smallCategoryName
                            , appleGuideEntity.guideContent
                            , appleBaseGuideEntity.guideContent
                            , appleGuideEntity.useYn
                            , appleGuideEntity.buttonYn
                    ))
                    .from(appleGuideEntity)
                    .leftJoin(appleBaseGuideEntity)
                    .on(appleGuideEntity.baseBlockCode
                            .eq(appleBaseGuideEntity.baseBlockCode))
                    .where(setConditionWithBaseContentUseY(searchGuideDto, botId))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
            return new PageImpl<>(adminGuideList, pageable, adminGuideList.size());
        } else if (SearchAppleGuideType.MODCONTENT.code() == searchWordCondition) {
            adminGuideList = jpaQueryFactory.select(Projections.constructor(ResponseAdminGuideDto.class
                            , appleGuideEntity.baseBlockCode
                            , appleGuideEntity.largeCategoryName
                            , appleGuideEntity.middleCategoryName
                            , appleGuideEntity.smallCategoryName
                            , appleGuideEntity.guideContent
                            , appleBaseGuideEntity.guideContent
                            , appleGuideEntity.useYn
                            , appleGuideEntity.buttonYn
                    ))
                    .from(appleGuideEntity)
                    .leftJoin(appleBaseGuideEntity)
                    .on(appleGuideEntity.baseBlockCode
                            .eq(appleBaseGuideEntity.baseBlockCode))
                    .where(setConditionWithContentUseY(searchGuideDto, botId))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
            return new PageImpl<>(adminGuideList, pageable, adminGuideList.size());
        } else if (SearchAppleGuideType.CATNAME.code() == searchWordCondition) {
            adminGuideList = jpaQueryFactory.select(Projections.constructor(ResponseAdminGuideDto.class
                            , appleGuideEntity.baseBlockCode
                            , appleGuideEntity.largeCategoryName
                            , appleGuideEntity.middleCategoryName
                            , appleGuideEntity.smallCategoryName
                            , appleGuideEntity.guideContent
                            , appleBaseGuideEntity.guideContent
                            , appleGuideEntity.useYn
                            , appleGuideEntity.buttonYn
                    ))
                    .from(appleGuideEntity)
                    .leftJoin(appleBaseGuideEntity)
                    .on(appleGuideEntity.baseBlockCode
                            .eq(appleBaseGuideEntity.baseBlockCode))
                    .where(setConditionWithCatNameUseY(searchGuideDto, botId))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
            return new PageImpl<>(adminGuideList, pageable, adminGuideList.size());
        } else if (SearchAppleGuideType.BLOCKNO.code() == searchWordCondition) {
            adminGuideList = jpaQueryFactory.select(Projections.constructor(ResponseAdminGuideDto.class
                            , appleGuideEntity.baseBlockCode
                            , appleGuideEntity.largeCategoryName
                            , appleGuideEntity.middleCategoryName
                            , appleGuideEntity.smallCategoryName
                            , appleGuideEntity.guideContent
                            , appleBaseGuideEntity.guideContent
                            , appleGuideEntity.useYn
                            , appleGuideEntity.buttonYn
                    ))
                    .from(appleGuideEntity)
                    .leftJoin(appleBaseGuideEntity)
                    .on(appleGuideEntity.baseBlockCode
                            .eq(appleBaseGuideEntity.baseBlockCode))
                    .where(setConditionWithBlockCodeUseY(searchGuideDto, botId))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
            return new PageImpl<>(adminGuideList, pageable, adminGuideList.size());
        }
        return new PageImpl<>(adminGuideList, pageable, 0);
    }

    // 검색어가 있을 때
    // 사용조건 0
    @Override
    public Page<ResponseAdminGuideDto> searchByBotIdWithWordUseN(RequestAdminSearchGuideDto searchGuideDto, String botId, Pageable pageable) {
        List<ResponseAdminGuideDto> adminGuideList = new ArrayList<>();
        int searchWordCondition = searchGuideDto.getSearchWordCondition();
        // 기본응답
        if (SearchAppleGuideType.CONTENT.code() == searchWordCondition) {
            adminGuideList = jpaQueryFactory.select(Projections.constructor(ResponseAdminGuideDto.class
                            , appleGuideEntity.baseBlockCode
                            , appleGuideEntity.largeCategoryName
                            , appleGuideEntity.middleCategoryName
                            , appleGuideEntity.smallCategoryName
                            , appleGuideEntity.guideContent
                            , appleBaseGuideEntity.guideContent
                            , appleGuideEntity.useYn
                            , appleGuideEntity.buttonYn
                    ))
                    .from(appleGuideEntity)
                    .leftJoin(appleBaseGuideEntity)
                    .on(appleGuideEntity.baseBlockCode
                            .eq(appleBaseGuideEntity.baseBlockCode))
                    .where(setConditionWithBaseContentUseN(searchGuideDto, botId))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
            return new PageImpl<>(adminGuideList, pageable, adminGuideList.size());
        } else if (SearchAppleGuideType.MODCONTENT.code() == searchWordCondition) {
            // 수정응답
            adminGuideList = jpaQueryFactory.select(Projections.constructor(ResponseAdminGuideDto.class
                            , appleGuideEntity.baseBlockCode
                            , appleGuideEntity.largeCategoryName
                            , appleGuideEntity.middleCategoryName
                            , appleGuideEntity.smallCategoryName
                            , appleGuideEntity.guideContent
                            , appleBaseGuideEntity.guideContent
                            , appleGuideEntity.useYn
                            , appleGuideEntity.buttonYn
                    ))
                    .from(appleGuideEntity)
                    .leftJoin(appleBaseGuideEntity)
                    .on(appleGuideEntity.baseBlockCode
                            .eq(appleBaseGuideEntity.baseBlockCode))
                    .where(setConditionWithContentUseN(searchGuideDto, botId))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
            return new PageImpl<>(adminGuideList, pageable, adminGuideList.size());
        } else if (SearchAppleGuideType.CATNAME.code() == searchWordCondition) {
            // 분류명
            adminGuideList = jpaQueryFactory.select(Projections.constructor(ResponseAdminGuideDto.class
                            , appleGuideEntity.baseBlockCode
                            , appleGuideEntity.largeCategoryName
                            , appleGuideEntity.middleCategoryName
                            , appleGuideEntity.smallCategoryName
                            , appleGuideEntity.guideContent
                            , appleBaseGuideEntity.guideContent
                            , appleGuideEntity.useYn
                            , appleGuideEntity.buttonYn
                    ))
                    .from(appleGuideEntity)
                    .leftJoin(appleBaseGuideEntity)
                    .on(appleGuideEntity.baseBlockCode
                            .eq(appleBaseGuideEntity.baseBlockCode))
                    .where(setConditionWithCatNameUseN(searchGuideDto, botId))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
            return new PageImpl<>(adminGuideList, pageable, adminGuideList.size());
        } else if (SearchAppleGuideType.BLOCKNO.code() == searchWordCondition) {
            // 블록No
            adminGuideList = jpaQueryFactory.select(Projections.constructor(ResponseAdminGuideDto.class
                            , appleGuideEntity.baseBlockCode
                            , appleGuideEntity.largeCategoryName
                            , appleGuideEntity.middleCategoryName
                            , appleGuideEntity.smallCategoryName
                            , appleGuideEntity.guideContent
                            , appleBaseGuideEntity.guideContent
                            , appleGuideEntity.useYn
                            , appleGuideEntity.buttonYn
                    ))
                    .from(appleGuideEntity)
                    .leftJoin(appleBaseGuideEntity)
                    .on(appleGuideEntity.baseBlockCode
                            .eq(appleBaseGuideEntity.baseBlockCode))
                    .where(setConditionWithBlockCodeUseN(searchGuideDto, botId))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
            return new PageImpl<>(adminGuideList, pageable, adminGuideList.size());
        }
        return new PageImpl<>(adminGuideList, pageable, 0);
    }


    ///////////////////////////////////////////////////////////////////////////
    // WHERE절 조건
    // 검색어가 없을 떄
    // 사용조건 전체
    private BooleanBuilder setConditionWithoutAllUse(RequestAdminSearchGuideDto searchGuideDto, String botId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (!StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 전체 , 대중소X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId));

        } else if (!StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 전체 , 대 O, 중소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()));

        } else if (!StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 전체 , 대중 O, 소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()));

        } else if (StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 전체 , 대중소 O
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.smallCategoryCode.eq(searchGuideDto.getSmallCategoryCode()));

        }
        return booleanBuilder;
    }
    private BooleanBuilder setConditionWithoutAllN(RequestAdminSearchGuideDto searchGuideDto, String botId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (!StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 전체 , 대중소X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(0));

        } else if (!StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 전체 , 대 O, 중소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.useYn.eq(0));

        } else if (!StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 전체 , 대중 O, 소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.useYn.eq(0));

        } else if (StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 전체 , 대중소 O
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.smallCategoryCode.eq(searchGuideDto.getSmallCategoryCode()))
                    .and(appleGuideEntity.useYn.eq(0));

        }
        return booleanBuilder;
    }
    private BooleanBuilder setConditionWithoutAllY(RequestAdminSearchGuideDto searchGuideDto, String botId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (!StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 전체 , 대중소X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(1));

        } else if (!StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 전체 , 대 O, 중소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.useYn.eq(1));

        } else if (!StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 전체 , 대중 O, 소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.useYn.eq(1));

        } else if (StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 전체 , 대중소 O
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.smallCategoryCode.eq(searchGuideDto.getSmallCategoryCode()))
                    .and(appleGuideEntity.useYn.eq(1));

        }
        return booleanBuilder;
    }

    // WHERE절 조건
    // 검색어가 없을 때
    // 사용조건 1
    private BooleanBuilder setConditionWithoutAllUseY(RequestAdminSearchGuideDto searchGuideDto, String botId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (!StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 1 , 대중소X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(1));

        } else if (!StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 1 , 대 O, 중소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode())
                            .and(appleGuideEntity.useYn.eq(1)));

        } else if (!StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 1 , 대중 O, 소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.useYn.eq(1));

        } else if (StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 1 , 대중소 O
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.smallCategoryCode.eq(searchGuideDto.getSmallCategoryCode()))
                    .and(appleGuideEntity.useYn.eq(1));

        }
        return booleanBuilder;
    }

    // WHERE절 조건
    // 검색어가 없을 때
    // 사용조건 0
    private BooleanBuilder setConditionWithoutAllUseN(RequestAdminSearchGuideDto searchGuideDto, String botId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (!StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 0 , 대중소X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(0));

        } else if (!StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 0 , 대 O, 중소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode())
                            .and(appleGuideEntity.useYn.eq(0)));

        } else if (!StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 0 , 대중 O, 소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.useYn.eq(0));

        } else if (StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 사용 0 , 대중소 O
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.smallCategoryCode.eq(searchGuideDto.getSmallCategoryCode()))
                    .and(appleGuideEntity.useYn.eq(0));

        }
        return booleanBuilder;
    }


    // WHERE절 조건
    // 검색어 있을 때
    // 사용조건 전체
    // 기본응답
    private Predicate setConditionWithBaseContentAll(RequestAdminSearchGuideDto searchGuideDto, String botId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (!StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중소X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleBaseGuideEntity.guideContent.contains(searchGuideDto.getSearchWord()));

        } else if (!StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대 O, 중소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleBaseGuideEntity.guideContent.contains(searchGuideDto.getSearchWord()));

        } else if (!StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중 O, 소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleBaseGuideEntity.guideContent.contains(searchGuideDto.getSearchWord()));

        } else if (StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중소 O
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.smallCategoryCode.eq(searchGuideDto.getSmallCategoryCode()))
                    .and(appleBaseGuideEntity.guideContent.contains(searchGuideDto.getSearchWord()));;

        }
        return booleanBuilder;
    }

    // WHERE절 조건
    // 검색어 있을 때
    // 사용조건 전체
    // 수정응답
    private Predicate setConditionWithContentAll(RequestAdminSearchGuideDto searchGuideDto, String botId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (!StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중소X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.guideContent.contains(searchGuideDto.getSearchWord()));

        } else if (!StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대 O, 중소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.guideContent.contains(searchGuideDto.getSearchWord()));

        } else if (!StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중 O, 소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.guideContent.contains(searchGuideDto.getSearchWord()));

        } else if (StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중소 O
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.smallCategoryCode.eq(searchGuideDto.getSmallCategoryCode()))
                    .and(appleGuideEntity.guideContent.contains(searchGuideDto.getSearchWord()));;

        }
        return booleanBuilder;
    }

    // WHERE절 조건
    // 검색어 있을 때
    // 사용조건 전체
    // 분류명
    private Predicate setConditionWithCatNameAll(RequestAdminSearchGuideDto searchGuideDto, String botId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (!StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중소X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryName.contains(searchGuideDto.getSearchWord())
                            .or(appleGuideEntity.middleCategoryName.contains(searchGuideDto.getSearchWord()))
                            .or(appleGuideEntity.smallCategoryName.contains(searchGuideDto.getSearchWord()))
                    );

        } else if (!StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대 O, 중소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.largeCategoryName.contains(searchGuideDto.getSearchWord())
                            .or(appleGuideEntity.middleCategoryName.contains(searchGuideDto.getSearchWord()))
                            .or(appleGuideEntity.smallCategoryName.contains(searchGuideDto.getSearchWord()))
                    );

        } else if (!StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중 O, 소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.largeCategoryName.contains(searchGuideDto.getSearchWord())
                            .or(appleGuideEntity.middleCategoryName.contains(searchGuideDto.getSearchWord()))
                            .or(appleGuideEntity.smallCategoryName.contains(searchGuideDto.getSearchWord()))
                    );

        } else if (StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중소 O
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.smallCategoryCode.eq(searchGuideDto.getSmallCategoryCode()))
                    .and(appleGuideEntity.largeCategoryName.contains(searchGuideDto.getSearchWord())
                            .or(appleGuideEntity.middleCategoryName.contains(searchGuideDto.getSearchWord()))
                            .or(appleGuideEntity.smallCategoryName.contains(searchGuideDto.getSearchWord()))
                    );

        }
        return booleanBuilder;
    }

    // WHERE절 조건
    // 검색어 있을 때
    // 사용조건 전체
    // 블럭No
    private Predicate setConditionWithBlockCodeAll(RequestAdminSearchGuideDto searchGuideDto, String botId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (!StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중소X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.baseBlockCode.eq(searchGuideDto.getSearchWord()));

        } else if (!StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대 O, 중소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.baseBlockCode.eq(searchGuideDto.getSearchWord()));

        } else if (!StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중 O, 소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.baseBlockCode.eq(searchGuideDto.getSearchWord()));

        } else if (StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중소 O
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.smallCategoryCode.eq(searchGuideDto.getSmallCategoryCode()))
                    .and(appleGuideEntity.baseBlockCode.eq(searchGuideDto.getSearchWord()));
        }
        return booleanBuilder;
    }

    // WHERE절 조건
    // 검색어 있을 때
    // 사용조건 1
    // 기본응답
    private Predicate setConditionWithBaseContentUseY(RequestAdminSearchGuideDto searchGuideDto, String botId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (!StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중소X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(1))
                    .and(appleBaseGuideEntity.guideContent.contains(searchGuideDto.getSearchWord()));

        } else if (!StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대 O, 중소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(1))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleBaseGuideEntity.guideContent.contains(searchGuideDto.getSearchWord()));

        } else if (!StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중 O, 소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(1))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleBaseGuideEntity.guideContent.contains(searchGuideDto.getSearchWord()));

        } else if (StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중소 O
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(1))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.smallCategoryCode.eq(searchGuideDto.getSmallCategoryCode()))
                    .and(appleBaseGuideEntity.guideContent.contains(searchGuideDto.getSearchWord()));
        }
        return booleanBuilder;
    }

    // WHERE절 조건
    // 검색어 있을 때
    // 사용조건 1
    // 수정응답
    private Predicate setConditionWithContentUseY(RequestAdminSearchGuideDto searchGuideDto, String botId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (!StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중소X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(1))
                    .and(appleGuideEntity.guideContent.contains(searchGuideDto.getSearchWord()));

        } else if (!StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대 O, 중소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(1))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.guideContent.contains(searchGuideDto.getSearchWord()));

        } else if (!StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중 O, 소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(1))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.guideContent.contains(searchGuideDto.getSearchWord()));

        } else if (StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중소 O
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(1))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.smallCategoryCode.eq(searchGuideDto.getSmallCategoryCode()))
                    .and(appleGuideEntity.guideContent.contains(searchGuideDto.getSearchWord()));
        }
        return booleanBuilder;
    }

    // WHERE절 조건
    // 검색어 있을 때
    // 사용조건 1
    // 블럭No
    private Predicate setConditionWithBlockCodeUseY(RequestAdminSearchGuideDto searchGuideDto, String botId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (!StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중소X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(1))
                    .and(appleGuideEntity.baseBlockCode.eq(searchGuideDto.getSearchWord()));

        } else if (!StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대 O, 중소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(1))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.baseBlockCode.eq(searchGuideDto.getSearchWord()));

        } else if (!StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중 O, 소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(1))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.baseBlockCode.eq(searchGuideDto.getSearchWord()));

        } else if (StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중소 O
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(1))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.smallCategoryCode.eq(searchGuideDto.getSmallCategoryCode()))
                    .and(appleGuideEntity.baseBlockCode.eq(searchGuideDto.getSearchWord()));
        }
        return booleanBuilder;
    }

    // WHERE절 조건
    // 검색어 있을 때
    // 사용조건 1
    // 분류명
    private Predicate setConditionWithCatNameUseY(RequestAdminSearchGuideDto searchGuideDto, String botId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (!StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중소X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(1))
                    .and(appleGuideEntity.largeCategoryName.contains(searchGuideDto.getSearchWord())
                            .or(appleGuideEntity.middleCategoryName.contains(searchGuideDto.getSearchWord()))
                            .or(appleGuideEntity.smallCategoryName.contains(searchGuideDto.getSearchWord()))
                    );

        } else if (!StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대 O, 중소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(1))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.largeCategoryName.contains(searchGuideDto.getSearchWord())
                            .or(appleGuideEntity.middleCategoryName.contains(searchGuideDto.getSearchWord()))
                            .or(appleGuideEntity.smallCategoryName.contains(searchGuideDto.getSearchWord()))
                    );

        } else if (!StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중 O, 소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(1))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.largeCategoryName.contains(searchGuideDto.getSearchWord())
                            .or(appleGuideEntity.middleCategoryName.contains(searchGuideDto.getSearchWord()))
                            .or(appleGuideEntity.smallCategoryName.contains(searchGuideDto.getSearchWord()))
                    );

        } else if (StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중소 O
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(1))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.smallCategoryCode.eq(searchGuideDto.getSmallCategoryCode()))
                    .and(appleGuideEntity.largeCategoryName.contains(searchGuideDto.getSearchWord())
                            .or(appleGuideEntity.middleCategoryName.contains(searchGuideDto.getSearchWord()))
                            .or(appleGuideEntity.smallCategoryName.contains(searchGuideDto.getSearchWord()))
                    );

        }
        return booleanBuilder;
    }


    // WHERE절 조건
    // 검색어 있을 때
    // 사용조건 0
    // 기본응답
    private Predicate setConditionWithBaseContentUseN(RequestAdminSearchGuideDto searchGuideDto, String botId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (!StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중소X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(0))
                    .and(appleBaseGuideEntity.guideContent.contains(searchGuideDto.getSearchWord()));

        } else if (!StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대 O, 중소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(0))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleBaseGuideEntity.guideContent.contains(searchGuideDto.getSearchWord()));

        } else if (!StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중 O, 소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(0))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleBaseGuideEntity.guideContent.contains(searchGuideDto.getSearchWord()));

        } else if (StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중소 O
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(0))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.smallCategoryCode.eq(searchGuideDto.getSmallCategoryCode()))
                    .and(appleBaseGuideEntity.guideContent.contains(searchGuideDto.getSearchWord()));
        }
        return booleanBuilder;
    }

    // WHERE절 조건
    // 검색어 있을 때
    // 사용조건 0
    // 수정응답
    private Predicate setConditionWithContentUseN(RequestAdminSearchGuideDto searchGuideDto, String botId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (!StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중소X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(1))
                    .and(appleGuideEntity.guideContent.contains(searchGuideDto.getSearchWord()));

        } else if (!StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대 O, 중소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(1))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.guideContent.contains(searchGuideDto.getSearchWord()));

        } else if (!StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중 O, 소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(1))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.guideContent.contains(searchGuideDto.getSearchWord()));

        } else if (StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중소 O
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(1))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.smallCategoryCode.eq(searchGuideDto.getSmallCategoryCode()))
                    .and(appleGuideEntity.guideContent.contains(searchGuideDto.getSearchWord()));
        }
        return booleanBuilder;
    }

    // WHERE절 조건
    // 검색어 있을 때
    // 사용조건 0
    // 블럭No
    private Predicate setConditionWithBlockCodeUseN(RequestAdminSearchGuideDto searchGuideDto, String botId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (!StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중소X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(0))
                    .and(appleGuideEntity.baseBlockCode.eq(searchGuideDto.getSearchWord()));

        } else if (!StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대 O, 중소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(0))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.baseBlockCode.eq(searchGuideDto.getSearchWord()));

        } else if (!StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중 O, 소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(0))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.baseBlockCode.eq(searchGuideDto.getSearchWord()));

        } else if (StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중소 O
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(0))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.smallCategoryCode.eq(searchGuideDto.getSmallCategoryCode()))
                    .and(appleGuideEntity.baseBlockCode.eq(searchGuideDto.getSearchWord()));
        }
        return booleanBuilder;
    }

    // WHERE절 조건
    // 검색어 있을 때
    // 사용조건 0
    // 분류명
    private Predicate setConditionWithCatNameUseN(RequestAdminSearchGuideDto searchGuideDto, String botId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (!StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중소X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(0))
                    .and(appleGuideEntity.largeCategoryName.contains(searchGuideDto.getSearchWord())
                            .or(appleGuideEntity.middleCategoryName.contains(searchGuideDto.getSearchWord()))
                            .or(appleGuideEntity.smallCategoryName.contains(searchGuideDto.getSearchWord()))
                    );

        } else if (!StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대 O, 중소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(0))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.largeCategoryName.contains(searchGuideDto.getSearchWord())
                            .or(appleGuideEntity.middleCategoryName.contains(searchGuideDto.getSearchWord()))
                            .or(appleGuideEntity.smallCategoryName.contains(searchGuideDto.getSearchWord()))
                    );

        } else if (!StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중 O, 소 X
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(0))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.largeCategoryName.contains(searchGuideDto.getSearchWord())
                            .or(appleGuideEntity.middleCategoryName.contains(searchGuideDto.getSearchWord()))
                            .or(appleGuideEntity.smallCategoryName.contains(searchGuideDto.getSearchWord()))
                    );


        } else if (StringUtils.hasLength(searchGuideDto.getSmallCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getMiddleCategoryCode()) &&
                StringUtils.hasLength(searchGuideDto.getLargeCategoryCode())) {
            // 대중소 O
            booleanBuilder.and(appleGuideEntity.botId.eq(botId))
                    .and(appleGuideEntity.useYn.eq(0))
                    .and(appleGuideEntity.largeCategoryCode.eq(searchGuideDto.getLargeCategoryCode()))
                    .and(appleGuideEntity.middleCategoryCode.eq(searchGuideDto.getMiddleCategoryCode()))
                    .and(appleGuideEntity.smallCategoryCode.eq(searchGuideDto.getSmallCategoryCode()))
                    .and(appleGuideEntity.largeCategoryName.contains(searchGuideDto.getSearchWord())
                            .or(appleGuideEntity.middleCategoryName.contains(searchGuideDto.getSearchWord()))
                            .or(appleGuideEntity.smallCategoryName.contains(searchGuideDto.getSearchWord()))
                    );

        }
        return booleanBuilder;
    }


}
