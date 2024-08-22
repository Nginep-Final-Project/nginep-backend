package com.example.nginep.users.service.impl;

import com.example.nginep.exceptions.duplicateException.DuplicateException;
import com.example.nginep.exceptions.notFoundException.NotFoundException;
import com.example.nginep.users.dto.SignupRequestDto;
import com.example.nginep.users.dto.UpdateUsersRequestDto;
import com.example.nginep.users.dto.UsersResponseDto;
import com.example.nginep.users.entity.Users;
import com.example.nginep.users.repository.UsersRepository;
import com.example.nginep.users.service.UsersService;
import lombok.extern.java.Log;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@Log
public class UsersServiceImpl implements UsersService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public UsersServiceImpl(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UsersResponseDto> getAllUsers() {
        return usersRepository.findAll().stream().map(this::mapToUsersResponseDto).toList();
    }

    @Override
    public UsersResponseDto getDetailUser(String email) {
        Users emailExists = usersRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not exists"));
        return mapToUsersResponseDto(emailExists);
    }

    @Override
    public String checkDuplicateEmail(String email) {
        Optional<Users> emailExists = usersRepository.findByEmail(email);
        if (emailExists.isPresent()) {
            throw new DuplicateException("Email address is already in use");
        } else {
            return "Email validation success";
        }
    }

    @Override
    public String updatePersonalData(UpdateUsersRequestDto updateUsersRequestDto) {
        Users newUserData = usersRepository.findByEmail(updateUsersRequestDto.getEmail()).orElseThrow(() -> new NotFoundException("Email already exists"));
        newUserData.setFullName(updateUsersRequestDto.getFullName());
        newUserData.setDateOfBirth(updateUsersRequestDto.getDateOfBirth());
        newUserData.setGender(updateUsersRequestDto.getGender());
        newUserData.setPhoneNumber(updateUsersRequestDto.getPhoneNumber());

        return "Update personal data success";
    }

    @Override
    public String updateEmail(UpdateUsersRequestDto updateUsersRequestDto) {
        Users newUserData = usersRepository.findByEmail(updateUsersRequestDto.getEmail()).orElseThrow(() -> new NotFoundException("Email already exists"));
        newUserData.setEmail(updateUsersRequestDto.getEmail());
        newUserData.setIsVerified(false);
        return "Update email success";
    }

    @Override
    public String updateChangePassword(UpdateUsersRequestDto updateUsersRequestDto) {
        Users newUserData = usersRepository.findByEmail(updateUsersRequestDto.getEmail()).orElseThrow(() -> new NotFoundException("Email already exists"));
        newUserData.setPassword(updateUsersRequestDto.getPassword());
        return "Update password success";
    }

    @Override
    public String updateAboutYourself(UpdateUsersRequestDto updateUsersRequestDto) {
        Users newUserData = usersRepository.findByEmail(updateUsersRequestDto.getEmail()).orElseThrow(() -> new NotFoundException("Email already exists"));
        newUserData.setAboutYourself(updateUsersRequestDto.getAboutYourself());
        return "Update about yourself success";
    }

    @Override
    public String updateBankAccount(UpdateUsersRequestDto updateUsersRequestDto) {
        Users newUserData = usersRepository.findByEmail(updateUsersRequestDto.getEmail()).orElseThrow(() -> new NotFoundException("Email already exists"));
        newUserData.setBankName(updateUsersRequestDto.getBankName());
        newUserData.setBankAccountNumber(updateUsersRequestDto.getBankAccountNumber());
        newUserData.setBankHolderName(updateUsersRequestDto.getBankHolderName());
        return "Update bank account success";
    }

    @Override
    public String updatePropertyRules(UpdateUsersRequestDto updateUsersRequestDto) {
        Users newUserData = usersRepository.findByEmail(updateUsersRequestDto.getEmail()).orElseThrow(() -> new NotFoundException("Email already exists"));
        newUserData.setCheckinTime(updateUsersRequestDto.getCheckinTime());
        newUserData.setCheckoutTime(updateUsersRequestDto.getCheckoutTime());
        newUserData.setCancelPolicy(updateUsersRequestDto.getCancelPolicy());
        return "Update property rules success";
    }

    @Override
    public String updateProfilePicture(UpdateUsersRequestDto updateUsersRequestDto) {
        Users newUserData = usersRepository.findByEmail(updateUsersRequestDto.getEmail()).orElseThrow(() -> new NotFoundException("Email already exists"));
        newUserData.setProfilePicture(updateUsersRequestDto.getProfilePicture());
        return "Update profile picture success";
    }

    @Override
    public UsersResponseDto signup(SignupRequestDto signupRequestDto) {
        Users newUser = signupRequestDto.toEntity();
        newUser.setPassword(passwordEncoder.encode(signupRequestDto.getPassword()));
        newUser.setIsVerified(false);
        Users savedUser = usersRepository.save(newUser);
        return mapToUsersResponseDto(savedUser);
    }

    @Override
    public UsersResponseDto getProfile() {
        return null;
    }

    @Override
    public Users getDetailUserId(Long id) {
        return usersRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    public UsersResponseDto mapToUsersResponseDto(Users user) {
        UsersResponseDto response = new UsersResponseDto();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setProfilePicture(user.getProfilePicture());
        response.setIsVerified(user.getIsVerified());
        response.setDateOfBirth(user.getDateOfBirth());
        response.setGender(user.getGender());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setAboutYourself(user.getAboutYourself());
        response.setCheckinTime(user.getCheckinTime());
        response.setCheckoutTime(user.getCheckoutTime());
        response.setCancelPolicy(user.getCancelPolicy());
        response.setBankName(user.getBankName());
        response.setBankAccountNumber(user.getBankAccountNumber());
        response.setBankHolderName(user.getBankHolderName());
        return response;
    }
}
