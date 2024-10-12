package com.example.nginep.languages.service.impl;

import com.example.nginep.auth.helpers.Claims;
import com.example.nginep.exceptions.notFoundException.NotFoundException;
import com.example.nginep.languages.dto.LangaugesRequestDto;
import com.example.nginep.languages.dto.LangaugesResponseDto;
import com.example.nginep.languages.entity.Languages;
import com.example.nginep.languages.repository.LanguagesRepository;
import com.example.nginep.languages.service.LanguagesService;
import com.example.nginep.users.service.UsersService;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log
public class LanguagesServiceImpl implements LanguagesService {
    private final LanguagesRepository languagesRepository;
    private final UsersService usersService;

    public LanguagesServiceImpl(LanguagesRepository languagesRepository, UsersService usersService) {
        this.languagesRepository = languagesRepository;
        this.usersService = usersService;
    }


    @Override
    public LangaugesResponseDto createLanguage(LangaugesRequestDto langaugesRequestDto) {
        log.info(langaugesRequestDto.toString());
        Languages newLanguage = languagesRepository.save(langaugesRequestDto.toEntity(usersService));
        return mapToLanguagesResponseDto(newLanguage);
    }

    @Override
    public List<LangaugesResponseDto> getLanguagesByTenantId(Long tenantId) {
        return languagesRepository.findAllByUserId(tenantId).stream().map(this::mapToLanguagesResponseDto).toList();
    }

    @Override
    public String deleteLanguage(Long languageId) {
        languagesRepository.findById(languageId).orElseThrow(() -> new NotFoundException("Language with id: " + languageId + " not found"));
        languagesRepository.deleteById(languageId);
        return "Language with id: " + languageId + " has deleted successfully";
    }

    public LangaugesResponseDto mapToLanguagesResponseDto(Languages language) {
        LangaugesResponseDto response = new LangaugesResponseDto();
        response.setId(language.getId());
        response.setLanguageName(language.getLanguageName());
        return response;
    }
}
