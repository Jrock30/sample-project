package com.sample.project.api.access.entity;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Table(name = "APPLE_PERMISSION")
@Entity
public class AccessApplePermissionEntity {

    @Id
    @Column(name = "BOT_ID")
    private String botId;

    @Column(name = "PERMISSION_GROUP_ID")
    private String permissionGroupId;

    @Column(name = "USER_ID", nullable = false)
    private String userId;

    @OneToOne
    @JoinColumn(name = "BOT_ID")
    private AccessAppleEntity accessAppleEntity;

}
