package com.sample.project.api.login.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@ToString
@Table(name = "MEMBER_PERMISSION_GROUP")
@Schema(description = "회원 권한 엔티티")
public class LoginMemberPermissionEntity {

    @Id
    @Column(name = "USER_ID")
    @Schema(description = "회원 ID")
    private String userId;

    @Column(name = "PERMISSION_GROUP_ID")
    @Schema(description = "권한그룹ID")
    private String permissionGroupId;

    @Column(name = "APPLY_START_DATE")
    @Schema(description = "적용시작일시")
    private LocalDateTime applyStartDate;

    @Column(name = "APPLY_END_DATE")
    @Schema(description = "적용종료일시")
    private LocalDateTime applyEndDate;

    @Column(name = "REG_ID")
    @Schema(description = "등록자")
    private String regId;

    @Column(name = "REG_DATE")
    @Schema(description = "등록일시")
    private LocalDateTime regDate;

    @Column(name = "UPD_ID")
    @Schema(description = "수정자")
    private String updId;

    @Column(name = "UPD_DATE")
    @Schema(description = "수정일시")
    private LocalDateTime updDate;

    @JsonIgnore
    @OneToOne(mappedBy = "loginMemberPermissionEntity", fetch = FetchType.LAZY)
    private LoginMemberEntity loginMemberEntity;

    @Builder
    public LoginMemberPermissionEntity(String userId, String permissionGroupId) {
        this.userId = userId;
        this.permissionGroupId = permissionGroupId;
        this.applyStartDate = LocalDateTime.now();
        this.applyEndDate = LocalDateTime.of(9999, 12, 31, 0, 0, 0);
        this.regDate = LocalDateTime.now();
        this.updDate = LocalDateTime.now();
    }

    public void changePermissionId(String role) {
        this.permissionGroupId = role;
        this.updDate = LocalDateTime.now();
    }
}
