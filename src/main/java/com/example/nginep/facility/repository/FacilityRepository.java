package com.example.nginep.facility.repository;

import com.example.nginep.facility.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FacilityRepository extends JpaRepository<Facility, Long> {
    List<Facility> findAllByUserId(Long tenantId);
}
