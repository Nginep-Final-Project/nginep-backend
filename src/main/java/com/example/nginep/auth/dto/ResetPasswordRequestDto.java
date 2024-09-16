package com.example.nginep.auth.dto;

import lombok.Data;

@Data
public class ResetPasswordRequestDto {
    private String email;
    private String newPassword;
}
