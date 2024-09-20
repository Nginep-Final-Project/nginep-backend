package com.example.nginep.property.repository;

import com.example.nginep.property.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findAllByUserId(Long tenantId);
}
