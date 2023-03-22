package com.sample.project.api.access.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Table(name = "APPLE")
@Entity
public class AccessAppleEntity {

    @Id
    @Column(name = "BOT_ID")
    @Schema(description = "봇 ID")
    private String botId;

    @Column(name = "MALL_ID")
    private String mallId;

    @Column(name = "AGENCY_ID")
    @Schema(description = "카페24 ID")
    private String agencyId;

    @Column(name = "DELETE_YN")
    @Schema(description = "삭제여부")
    private int deleteYn;

    @OneToOne
    @JoinColumn(name = "BOT_ID")
    private AccessApplePermissionEntity accessApplePermissionEntity;

}
