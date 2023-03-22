package com.sample.project.api.apple.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class ApplePermissionEntityPK implements Serializable {

    private String botId;
    private String userId;
}
