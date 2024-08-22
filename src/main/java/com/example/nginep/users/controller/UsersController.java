package com.example.nginep.users.controller;

import com.example.nginep.response.Response;
import com.example.nginep.users.dto.SignupRequestDto;
import com.example.nginep.users.dto.UsersResponseDto;
import com.example.nginep.users.entity.Users;
import com.example.nginep.users.service.UsersService;
import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@Validated
@Log
public class UsersController {
    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping
    public ResponseEntity<Response<List<UsersResponseDto>>> getAllUsers() {
        return Response.successResponse("All users", usersService.getAllUsers());
    }

    @GetMapping("/email-validation/{email}")
    public ResponseEntity<Response<String>> validateEmail(@PathVariable String email) {
        return Response.successResponse("Email valid", usersService.checkDuplicateEmail(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<Users>> getUserDetailById(@PathVariable Long id) {
        return Response.successResponse("Email valid", usersService.getDetailUserId(id));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<Response<UsersResponseDto>> signup (@RequestBody SignupRequestDto signupRequestDto) {
        return Response.successResponse("Sign up success", usersService.signup(signupRequestDto));
    }
}
