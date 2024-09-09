package com.example.nginep.auth.service;

import com.example.nginep.users.dto.VerifyRequestDto;
import org.springframework.security.core.Authentication;

public interface AuthService {
    String generateToken(Authentication authentication);

    String generateVerificationEmail(String email);

    Boolean verifyAccount(VerifyRequestDto verifyRequestDto);

    String logout();
}
