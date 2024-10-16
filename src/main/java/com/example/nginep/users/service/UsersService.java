package com.example.nginep.users.service;

import com.example.nginep.users.dto.*;
import com.example.nginep.users.entity.Users;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UsersService {
    List<UsersResponseDto> getAllUsers();
    UsersResponseDto getDetailUser(String email);
    Users getDetailUserByEmail(String email);
    String checkDuplicateEmail(String email);
    String updatePersonalData(UpdateUsersRequestDto updateUsersRequestDto);
    String updateEmail(UpdateUsersRequestDto updateUsersRequestDto);
    String updateChangePassword(UpdateUsersRequestDto updateUsersRequestDto);
    String updateAboutYourself(UpdateUsersRequestDto updateUsersRequestDto);
    String updateBankAccount(UpdateUsersRequestDto updateUsersRequestDto);
    String updatePropertyRules(UpdateUsersRequestDto updateUsersRequestDto);
    String updateProfilePicture(MultipartFile file, String publicId);
    UsersResponseDto signup(SignupRequestDto signupRequestDto);
    UsersResponseDto signinGoogle(SigninGoogleRequestDto signinGoogleRequestDto);
    UsersResponseDto getProfile();
    Users getDetailUserId(Long id);
    String sendVerificationCode(SendVerifyRequestDto sendVerifyRequestDto);
    String sendVerifyResetPassword(String email);
    String verifyUser(VerifyRequestDto verifyRequestDto);
}
