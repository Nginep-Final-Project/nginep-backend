package com.example.nginep.auth.controller;

import com.example.nginep.auth.dto.AuthResponseDto;
import com.example.nginep.auth.dto.GoogleLoginRequestDto;
import com.example.nginep.auth.dto.LoginRequestDto;
import com.example.nginep.auth.dto.ResetPasswordRequestDto;
import com.example.nginep.auth.entity.UserAuth;
import com.example.nginep.auth.service.AuthService;
import com.example.nginep.response.Response;
import com.example.nginep.users.dto.SigninGoogleRequestDto;
import com.example.nginep.users.dto.UsersResponseDto;
import com.example.nginep.users.entity.Users;
import com.example.nginep.users.service.UsersService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.Value;
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

import java.util.Collections;

@RestController
@RequestMapping("/api/v1/auth")
@Validated
@Log
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final UsersService usersService;


    public AuthController(AuthService authService, AuthenticationManager authenticationManager, UsersService usersService) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.usersService = usersService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserAuth userDetails = (UserAuth) authentication.getPrincipal();
            String token = authService.generateToken(authentication);

            AuthResponseDto response = new AuthResponseDto();
            response.setUser(userDetails.getUser());
            response.setMessage("Login success");
            response.setToken(token);

            Cookie cookie = new Cookie("sid", token);
            cookie.setMaxAge(24 * 60 * 60);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Set-Cookie", cookie.getName() + "=" + cookie.getValue() + "; Path=/; HttpOnly");

            return ResponseEntity.status(HttpStatus.OK).headers(headers).body(Response.successResponse(response).getBody());
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Response.failedResponse("Invalid email or password").getBody());
        }
    }

    @PostMapping("/google-login")
    public ResponseEntity<?> googleLogin(@RequestBody GoogleLoginRequestDto googleLoginRequestDto) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory()).setAudience(Collections.singletonList("")).build();

            GoogleIdToken idToken = verifier.verify(googleLoginRequestDto.getIdToken());

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();

            UsersResponseDto user = usersService.getDetailUser(email);

            if (user == null) {
                SigninGoogleRequestDto newUser = new SigninGoogleRequestDto();
                newUser.setEmail(email);
                newUser.setFullName((String) payload.get("name"));
                newUser.setProfilePicture((String) payload.get("profilePicture"));
                newUser.setPassword(googleLoginRequestDto.getIdToken());
                user = usersService.signinGoogle(newUser);
            }

            // Create authentication object
            Authentication authentication =  new UsernamePasswordAuthenticationToken(user.getEmail(), googleLoginRequestDto.getIdToken());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = authService.generateToken(authentication);

            AuthResponseDto response = new AuthResponseDto();
            Users responseUser = new Users();
            responseUser.setId(user.getId());
            responseUser.setFullName(user.getFullName());
            responseUser.setEmail(user.getEmail());
            responseUser.setProfilePicture(user.getProfilePicture());
            responseUser.setRole(Users.Role.valueOf(user.getRole()));
            response.setUser(responseUser);
            response.setMessage("Google login success");
            response.setToken(token);

            Cookie cookie = new Cookie("sid", token);
            cookie.setMaxAge(24 * 60 * 60);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Set-Cookie", cookie.getName() + "=" + cookie.getValue() + "; Path=/; HttpOnly");

            return ResponseEntity.status(HttpStatus.OK).headers(headers).body(Response.successResponse(response).getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Response.failedResponse("Error during Google login: " + e.getMessage()).getBody());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Response<String>> resetPassword(@RequestBody ResetPasswordRequestDto resetPasswordRequestDto) {
        return Response.successResponse("Reset password success", authService.resetPassword(resetPasswordRequestDto));
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
