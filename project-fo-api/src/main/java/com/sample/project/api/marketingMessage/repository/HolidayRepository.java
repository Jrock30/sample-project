package com.sample.project.api.marketingMessage.repository;

import com.sample.project.api.marketingMessage.entity.HolidayEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HolidayRepository extends JpaRepository<HolidayEntity, Long> {
    List<HolidayEntity> findAllByOrderByDateAsc();
}
