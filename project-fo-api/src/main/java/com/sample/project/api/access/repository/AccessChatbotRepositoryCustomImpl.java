package com.sample.project.api.access.repository;

import com.sample.project.api.access.entity.AccessAppleEntity;
import com.sample.project.api.apple.enums.BotRole;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import static com.sample.project.api.access.entity.QAccessAppleEntity.accessAppleEntity;
import static com.sample.project.api.access.entity.QAccessApplePermissionEntity.accessApplePermissionEntity;

@Slf4j
@RequiredArgsConstructor
public class AccessAppleRepositoryCustomImpl implements AccessAppleRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<AccessAppleEntity> searchApple(String mallId) {
        Optional<Tuple> appleTuple = Optional.ofNullable(
                jpaQueryFactory.select(accessAppleEntity, accessApplePermissionEntity)
                        .from(accessAppleEntity)
                        .join(accessAppleEntity.accessApplePermissionEntity, accessApplePermissionEntity).fetchJoin()
                        .where(accessAppleEntity.mallId.eq(mallId)
                                .and(accessApplePermissionEntity.permissionGroupId.eq(BotRole.BOT_MASTER.code()))
                                .and(accessAppleEntity.deleteYn.eq(0))
                        )
                        .limit(1)
                        .fetchOne());
        return appleTuple.map(m -> m.get(accessAppleEntity));
    }
}
