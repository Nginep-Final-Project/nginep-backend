package com.example.nginep.users.controller;

import com.example.nginep.response.Response;
import com.example.nginep.users.dto.*;
import com.example.nginep.users.entity.Users;
import com.example.nginep.users.service.UsersService;
import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<Response<UsersResponseDto>> signup(@RequestBody SignupRequestDto signupRequestDto) {
        return Response.successResponse("Sign up success", usersService.signup(signupRequestDto));
    }

    @PostMapping("/send-verification")
    public ResponseEntity<Response<String>> verifyAccount(@RequestBody SendVerifyRequestDto sendVerifyRequestDto) {
        return Response.successResponse("Account verification success", usersService.sendVerificationCode(sendVerifyRequestDto));
    }

    @PostMapping("/forgot-password-verification/{email}")
    public ResponseEntity<Response<String>> verifyForgotPassword(@PathVariable String email) {
        return Response.successResponse("Send forgot password verification success", usersService.sendVerifyResetPassword(email));
    }

    @PostMapping("/verification")
    public ResponseEntity<Response<String>> verifyAccount(@RequestBody VerifyRequestDto verifyRequestDto) {
        return Response.successResponse("Account verification success", usersService.verifyUser(verifyRequestDto));
    }

    @GetMapping("/profile")
    public ResponseEntity<Response<UsersResponseDto>> getProfile() {
        return Response.successResponse("Get profile success", usersService.getProfile());
    }

    @PostMapping("/update/personal-data")
    public ResponseEntity<Response<String>> updatePersonalData(@RequestBody UpdateUsersRequestDto updateUsersRequestDto) {
        return Response.successResponse("Update personal info success", usersService.updatePersonalData(updateUsersRequestDto));
    }

    @PostMapping("/update/email")
    public ResponseEntity<Response<String>> updateEmail(@RequestBody UpdateUsersRequestDto updateUsersRequestDto) {
        return Response.successResponse("Update email success", usersService.updateEmail(updateUsersRequestDto));
    }

    @PostMapping("update/change-password")
    public ResponseEntity<Response<String>> updateChangePassword(@RequestBody UpdateUsersRequestDto updateUsersRequestDto) {
        return Response.successResponse("Change password success", usersService.updateChangePassword(updateUsersRequestDto));
    }

    @PostMapping("/update/about-yourself")
    public ResponseEntity<Response<String>> updateAboutYourself(@RequestBody UpdateUsersRequestDto updateUsersRequestDto) {
        return Response.successResponse("Update about yourself success", usersService.updateAboutYourself(updateUsersRequestDto));
    }

    @PostMapping("/update/bank-account")
    public ResponseEntity<Response<String>> updateBankAccount(@RequestBody UpdateUsersRequestDto updateUsersRequestDto) {
        return Response.successResponse("Update bank account success", usersService.updateBankAccount(updateUsersRequestDto));
    }

    @PostMapping("/update/property-rules")
    public ResponseEntity<Response<String>> updatePropertyRules(@RequestBody UpdateUsersRequestDto updateUsersRequestDto) {
        return Response.successResponse("Update property rules success", usersService.updatePropertyRules(updateUsersRequestDto));
    }

    @PostMapping("/update/profile-picture")
    public ResponseEntity<Response<String>> updateProfilePicture(@RequestParam(value = "publicId", required = false) String publicId,
                                                                 @RequestParam("file") MultipartFile file) {
        return Response.successResponse("Update profile picture success", usersService.updateProfilePicture(file, publicId));
    }
}
