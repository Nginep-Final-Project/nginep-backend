package com.example.nginep.users.dto;

import lombok.Data;

@Data
public class VerifyRequestDto {
    private String email;
    private String code;
}
