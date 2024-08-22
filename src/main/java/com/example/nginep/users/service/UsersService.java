package com.example.nginep.users.service;

import com.example.nginep.users.dto.SignupRequestDto;
import com.example.nginep.users.dto.UpdateUsersRequestDto;
import com.example.nginep.users.dto.UsersResponseDto;
import com.example.nginep.users.entity.Users;

import java.util.List;

public interface UsersService {
    List<UsersResponseDto> getAllUsers();
    UsersResponseDto getDetailUser(String email);
    String checkDuplicateEmail(String email);
    String updatePersonalData(UpdateUsersRequestDto updateUsersRequestDto);
    String updateEmail(UpdateUsersRequestDto updateUsersRequestDto);
    String updateChangePassword(UpdateUsersRequestDto updateUsersRequestDto);
    String updateAboutYourself(UpdateUsersRequestDto updateUsersRequestDto);
    String updateBankAccount(UpdateUsersRequestDto updateUsersRequestDto);
    String updatePropertyRules(UpdateUsersRequestDto updateUsersRequestDto);
    String updateProfilePicture(UpdateUsersRequestDto updateUsersRequestDto);
    UsersResponseDto signup(SignupRequestDto signupRequestDto);
    UsersResponseDto getProfile();
    Users getDetailUserId(Long id);

}
