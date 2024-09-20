package com.example.nginep.auth.service.impl;

import com.example.nginep.auth.dto.ResetPasswordRequestDto;
import com.example.nginep.auth.helpers.Claims;
import com.example.nginep.auth.repository.AuthRedisRepository;
import com.example.nginep.auth.service.AuthService;
import com.example.nginep.exceptions.applicationException.ApplicationException;
import com.example.nginep.exceptions.notFoundException.NotFoundException;
import com.example.nginep.users.dto.VerifyRequestDto;
import com.example.nginep.users.entity.Users;
import com.example.nginep.users.repository.UsersRepository;
import lombok.extern.java.Log;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Log
public class AuthServiceImpl implements AuthService {
    private final JwtEncoder jwtEncoder;
    private final PasswordEncoder passwordEncoder;
    private final UsersRepository usersRepository;
    private final AuthRedisRepository authRedisRepository;

    public AuthServiceImpl(JwtEncoder jwtEncoder, PasswordEncoder passwordEncoder, UsersRepository usersRepository, AuthRedisRepository authRedisRepository) {
        this.jwtEncoder = jwtEncoder;
        this.passwordEncoder = passwordEncoder;
        this.usersRepository = usersRepository;
        this.authRedisRepository = authRedisRepository;
    }

    @Override
    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();

        String scope = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        var existingKey = authRedisRepository.getJwtKey(authentication.getName());
        if (existingKey != null) {
            log.info("Token already exists for user: " + authentication.getName());
            return existingKey;
        }

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(24, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();



        var jwt = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        if (authRedisRepository.isKeyBlacklisted(jwt)) {
            throw new ApplicationException("Token has been blacklisted");
        }

        authRedisRepository.saveJwtKey(authentication.getName(), jwt);
        return jwt;
    }

    @Override
    public String generateVerificationEmail(String email) {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        authRedisRepository.saveVerificationKey(email, code.toString());
        return code.toString();
    }

    @Override
    public Boolean verifyAccount(VerifyRequestDto verifyRequestDto) {
        var existingCode = authRedisRepository.getVerificationKey(verifyRequestDto.getEmail());
        log.info("code: "+verifyRequestDto.getCode() + "redis code: " + existingCode);
        if (existingCode == null) {
            throw new NotFoundException("Verification code not found for email: " + verifyRequestDto.getEmail());
        }
        if (!Objects.equals(verifyRequestDto.getCode(), existingCode)) {
            throw new ApplicationException("Verification code invalid");
        }
        authRedisRepository.deleteVerificationKey(verifyRequestDto.getEmail());
        return true;
    }

    @Override
    public String resetPassword(ResetPasswordRequestDto resetPasswordRequestDto) {
        Users newUserData = usersRepository.findByEmail(resetPasswordRequestDto.getEmail()).orElseThrow(() -> new NotFoundException("User not found"));
        newUserData.setPassword(passwordEncoder.encode(resetPasswordRequestDto.getNewPassword()));
        usersRepository.save(newUserData);
        return "Reset password success. Please try to login again";
    }


    @Override
    public String logout() {
        var claims = Claims.getClaimsFromJwt();
        var email = (String) claims.get("sub");
        String jwt = authRedisRepository.getJwtKey(email);
        authRedisRepository.blackListJwt(email, jwt);
        authRedisRepository.deleteJwtKey(email);
        return "Logout success";
    }
}
