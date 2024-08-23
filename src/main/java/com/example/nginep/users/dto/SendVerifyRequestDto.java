package com.example.nginep.users.dto;

import lombok.Data;

@Data
public class SendVerifyRequestDto {
    private String email;
    private String name;
}
