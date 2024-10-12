package com.example.nginep.languages.service;

import com.example.nginep.languages.dto.LangaugesRequestDto;
import com.example.nginep.languages.dto.LangaugesResponseDto;

import java.util.List;

public interface LanguagesService {
    LangaugesResponseDto createLanguage(LangaugesRequestDto langaugesRequestDto);
    List<LangaugesResponseDto> getLanguagesByTenantId(Long tenantId);
    String deleteLanguage(Long languageId);
}
