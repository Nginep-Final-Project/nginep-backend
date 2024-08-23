package com.example.nginep.languages.controller;

import com.example.nginep.languages.dto.LangaugesRequestDto;
import com.example.nginep.languages.dto.LangaugesResponseDto;
import com.example.nginep.languages.service.LanguagesService;
import com.example.nginep.response.Response;
import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/languages")
@Validated
@Log
public class LanguagesController {
    private final LanguagesService languagesService;

    public LanguagesController(LanguagesService languagesService) {
        this.languagesService = languagesService;
    }

    @PostMapping
    public ResponseEntity<Response<LangaugesResponseDto>> createLanguage(@RequestBody LangaugesRequestDto languagesRequestDto) {
        return Response.successResponse("Create language success", languagesService.createLanguage(languagesRequestDto));
    }

    @GetMapping("/{tenantId}")
    public ResponseEntity<Response<List<LangaugesResponseDto>>> getLanguagesByTenantId(@PathVariable Long tenantId) {
        return Response.successResponse("List language by tenant id: " + tenantId, languagesService.getLanguagesByTenantId(tenantId));
    }

    @DeleteMapping("/{languageId}")
    public ResponseEntity<Response<String>> createLanguage(@PathVariable Long languageId) {
        return Response.successResponse("Delete language success", languagesService.deleteLanguage(languageId));
    }
}
