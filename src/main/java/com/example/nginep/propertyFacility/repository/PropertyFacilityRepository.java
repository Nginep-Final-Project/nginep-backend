package com.example.nginep.propertyFacility.repository;

import com.example.nginep.propertyFacility.dto.PropertyFacilityResponseDto;
import com.example.nginep.propertyFacility.entity.PropertyFacility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyFacilityRepository extends JpaRepository<PropertyFacility, Long> {
    List<PropertyFacility> findAllByPropertyId(Long propertyId);
}
