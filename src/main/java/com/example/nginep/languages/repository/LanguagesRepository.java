package com.example.nginep.languages.repository;

import com.example.nginep.languages.entity.Languages;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LanguagesRepository extends JpaRepository<Languages, Long> {
    List<Languages> findAllByUserId(Long tenantId);
}
