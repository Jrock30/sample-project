package com.sample.project.common.jpa.listener;

import com.sample.project.common.jpa.entity.AdminBaseEntity;
import com.sample.project.common.jpa.enums.AdminBaseCreation;
import com.sample.project.security.SecurityUtils;
import com.sample.project.security.model.CustomUserDetails;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 * @author   	: user
 * @since    	: 2022/11/21
 * @desc     	: 해당 entity의 save또는 저장 시 별도의 setting없이 데이터를 설정해 주는 리스너.
 */
public class AdminBaseListener {

    @PrePersist
    void onSave(AdminBaseEntity entity){
        this.setLoginMemberInfo(AdminBaseCreation.SAVE, entity);
    }

    @PreUpdate
    void onUpdate(AdminBaseEntity entity){
        this.setLoginMemberInfo(AdminBaseCreation.UPDATE, entity);
    }

    private void setLoginMemberInfo(AdminBaseCreation adminBaseCreation, AdminBaseEntity entity) {
        if(SecurityUtils.getUserDetails().isPresent()){
            CustomUserDetails customUserDetails = (CustomUserDetails) SecurityUtils.getUserDetails().get();
            switch (adminBaseCreation){
                case UPDATE:
                    entity.setUpdId(customUserDetails.getUserId());
                    break;
                default:
                    entity.setRegId(customUserDetails.getUserId());
                    break;
            }
        }
    }
}
