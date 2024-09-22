package com.example.nginep.property.repository;

import com.example.nginep.property.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long>, JpaSpecificationExecutor<Property> {
    List<Property> findAllByUserId(Long tenantId);

    @Query("SELECT DISTINCT p.propertyCity FROM Property p")
    List<String> findDistinctCities();
}
