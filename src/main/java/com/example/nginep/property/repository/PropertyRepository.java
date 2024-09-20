package com.example.nginep.property.repository;

import com.example.nginep.property.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findAllByUserId(Long tenantId);
}
