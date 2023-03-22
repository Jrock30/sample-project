package com.sample.project.common.jpa.entity;


import com.sample.project.common.jpa.listener.AdminBaseListener;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;


@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@EntityListeners({AuditingEntityListener.class, AdminBaseListener.class})
public class AdminBaseEntity {

    @Column(name = "REG_ID", columnDefinition = "varchar(80) comment '등록자'")
    private String regId;

    @CreatedDate
    @Column(name = "REG_DATE")
    private LocalDateTime regDate;

    @Column(name = "UPD_ID", columnDefinition = "varchar(80) comment '수정자'")
    private String updId;

    @LastModifiedDate
    @Column(name = "UPD_DATE")
    private LocalDateTime updDate;
}
