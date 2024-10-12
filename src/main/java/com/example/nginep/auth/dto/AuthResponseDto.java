package com.example.nginep.auth.dto;

import com.example.nginep.users.entity.Users;
import lombok.Data;

@Data
public class AuthResponseDto {
    private Users user;
    private String message;
    private String token;
}
