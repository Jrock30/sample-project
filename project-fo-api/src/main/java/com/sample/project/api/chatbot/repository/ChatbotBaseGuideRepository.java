package com.sample.project.api.apple.repository;


import com.sample.project.api.apple.entity.AppleBaseGuideEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AppleBaseGuideRepository extends JpaRepository<AppleBaseGuideEntity, String>, JpaSpecificationExecutor<AppleBaseGuideEntity> {


}