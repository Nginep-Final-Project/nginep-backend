package com.example.nginep.auth.controller;

import com.example.nginep.auth.dto.AuthResponseDto;
import com.example.nginep.auth.dto.LoginRequestDto;
import com.example.nginep.auth.entity.UserAuth;
import com.example.nginep.auth.service.AuthService;
import com.example.nginep.response.Response;
import jakarta.servlet.http.Cookie;
import lombok.extern.java.Log;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Validated
@Log
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDto.getEmail(),
                            loginRequestDto.getPassword()
                    ));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserAuth userDetails = (UserAuth) authentication.getPrincipal();
            String token = authService.generateToken(authentication);

            AuthResponseDto response = new AuthResponseDto();
            response.setUser(userDetails.getUser());
            response.setMessage("Login success");
            response.setToken(token);

            Cookie cookie = new Cookie("sid", token);
            cookie.setMaxAge(24 * 60 * 60);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            HttpHeaders headers = new HttpHeaders();
            headers.add("Set-Cookie", cookie.getName() + "=" + cookie.getValue());

            return ResponseEntity.status(HttpStatus.OK).headers(headers).body(Response.successResponse(response).getBody());
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Response.failedResponse("Invalid email or password").getBody());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        Cookie cookie = new Cookie("sid", null);
        cookie.setMaxAge(0);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", cookie.getName() + "=" + cookie.getValue() + "; Path=/; HttpOnly; Max-Age=0");
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(Response.successResponse(authService.logout()).getBody());
    }
}
