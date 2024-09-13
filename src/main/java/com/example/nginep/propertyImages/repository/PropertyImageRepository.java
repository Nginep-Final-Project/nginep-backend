package com.example.nginep.propertyImages.repository;


import com.example.nginep.propertyImages.entity.PropertyImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyImageRepository extends JpaRepository<PropertyImage, Long> {
    List<PropertyImage> findAllByPropertyId(Long propertyId);
}
