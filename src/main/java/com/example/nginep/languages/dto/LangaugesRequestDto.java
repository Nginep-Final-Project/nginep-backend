package com.example.nginep.languages.dto;

import com.example.nginep.languages.entity.Languages;
import com.example.nginep.users.service.UsersService;
import lombok.Data;

@Data
public class LangaugesRequestDto {
    private String languageName;
    private Long tenantId;

    public Languages toEntity(UsersService usersService){
        Languages newLanguages = new Languages();
        newLanguages.setLanguageName(languageName);
        newLanguages.setUser(usersService.getDetailUserId(tenantId));
        return newLanguages;
    }
}
