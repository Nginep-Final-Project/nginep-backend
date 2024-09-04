package com.example.nginep.users.service.impl;

import com.example.nginep.auth.service.AuthService;
import com.example.nginep.exceptions.applicationException.ApplicationException;
import com.example.nginep.exceptions.duplicateException.DuplicateException;
import com.example.nginep.exceptions.notFoundException.NotFoundException;
import com.example.nginep.users.dto.*;
import com.example.nginep.users.entity.Users;
import com.example.nginep.users.repository.UsersRepository;
import com.example.nginep.users.service.UsersService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.java.Log;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;


@Service
@Log
public class UsersServiceImpl implements UsersService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;
    private final AuthService authService;

    public UsersServiceImpl(UsersRepository usersRepository, PasswordEncoder passwordEncoder, JavaMailSender javaMailSender, AuthService authService) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.javaMailSender = javaMailSender;
        this.authService = authService;
    }

    @Override
    public List<UsersResponseDto> getAllUsers() {
        return usersRepository.findAll().stream().map(this::mapToUsersResponseDto).toList();
    }

    @Override
    public UsersResponseDto getDetailUser(String email) {
        Users emailExists = usersRepository.findByEmail(email).orElse(null);
       if(emailExists!=null) {
           return mapToUsersResponseDto(emailExists);
       }
        return null;
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
        usersRepository.save(newUserData);
        return "Update personal data success";
    }

    @Override
    public String updateEmail(UpdateUsersRequestDto updateUsersRequestDto) {
        Users newUserData = usersRepository.findByEmail(updateUsersRequestDto.getEmail()).orElseThrow(() -> new NotFoundException("Email already exists"));
        newUserData.setEmail(updateUsersRequestDto.getEmail());
        newUserData.setIsVerified(false);
        usersRepository.save(newUserData);
        return "Update email success";
    }

    @Override
    public String updateChangePassword(UpdateUsersRequestDto updateUsersRequestDto) {
        Users newUserData = usersRepository.findByEmail(updateUsersRequestDto.getEmail()).orElseThrow(() -> new NotFoundException("Email already exists"));
        newUserData.setPassword(passwordEncoder.encode(updateUsersRequestDto.getPassword()));
        usersRepository.save(newUserData);
        return "Update password success";
    }

    @Override
    public String updateAboutYourself(UpdateUsersRequestDto updateUsersRequestDto) {
        Users newUserData = usersRepository.findByEmail(updateUsersRequestDto.getEmail()).orElseThrow(() -> new NotFoundException("Email already exists"));
        newUserData.setAboutYourself(updateUsersRequestDto.getAboutYourself());
        usersRepository.save(newUserData);
        return "Update about yourself success";
    }

    @Override
    public String updateBankAccount(UpdateUsersRequestDto updateUsersRequestDto) {
        Users newUserData = usersRepository.findByEmail(updateUsersRequestDto.getEmail()).orElseThrow(() -> new NotFoundException("Email already exists"));
        newUserData.setBankName(updateUsersRequestDto.getBankName());
        newUserData.setBankAccountNumber(updateUsersRequestDto.getBankAccountNumber());
        newUserData.setBankHolderName(updateUsersRequestDto.getBankHolderName());
        usersRepository.save(newUserData);
        return "Update bank account success";
    }

    @Override
    public String updatePropertyRules(UpdateUsersRequestDto updateUsersRequestDto) {
        Users newUserData = usersRepository.findByEmail(updateUsersRequestDto.getEmail()).orElseThrow(() -> new NotFoundException("Email already exists"));
        newUserData.setCheckinTime(updateUsersRequestDto.getCheckinTime());
        newUserData.setCheckoutTime(updateUsersRequestDto.getCheckoutTime());
        newUserData.setCancelPolicy(updateUsersRequestDto.getCancelPolicy());
        usersRepository.save(newUserData);
        return "Update property rules success";
    }

    @Override
    public String updateProfilePicture(UpdateUsersRequestDto updateUsersRequestDto) {
        Users newUserData = usersRepository.findByEmail(updateUsersRequestDto.getEmail()).orElseThrow(() -> new NotFoundException("Email already exists"));
        newUserData.setProfilePicture(updateUsersRequestDto.getProfilePicture());
        usersRepository.save(newUserData);
        return "Update profile picture success";
    }

    @Override
    public UsersResponseDto signup(SignupRequestDto signupRequestDto) {
        Users newUser = signupRequestDto.toEntity();
        newUser.setPassword(passwordEncoder.encode(signupRequestDto.getPassword()));
        newUser.setIsVerified(true);
        Users savedUser = usersRepository.save(newUser);

        return mapToUsersResponseDto(savedUser);
    }

    @Override
    public UsersResponseDto signinGoogle(SigninGoogleRequestDto signinGoogleRequestDto) {
        Users newUser = signinGoogleRequestDto.toEntity();
        newUser.setPassword(passwordEncoder.encode(signinGoogleRequestDto.getPassword()));
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

    @Override
    public String sendVerificationCode(SendVerifyRequestDto sendVerifyRequestDto) {
        try {
            String toAddress = sendVerifyRequestDto.getEmail();
            String fromAddress = "projectnginep@gmail.com";
            String senderName = "Nginep";
            String subject = "Please verify your registration";
            String content = "Dear [[name]],<br>" + "Please Insert the verification code below:<br>" + "<h3>[[code]]</h3>" + "Thank you,<br>" + "Nginep.";

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);


            helper.setFrom(new InternetAddress(fromAddress, senderName));
            helper.setTo(toAddress);
            helper.setSubject(subject);

            content = content.replace("[[name]]", sendVerifyRequestDto.getName());
            content = content.replace("[[code]]", authService.generateVerificationEmail(sendVerifyRequestDto.getEmail()));
            helper.setText(content, true);

            javaMailSender.send(message);

            return "Send verification code success";
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.info(e.toString());
            throw new ApplicationException("Send verification code failed");
        }
    }

    @Override
    public String sendVerifyResetPassword(String email) {
        try {
            String fromAddress = "projectnginep@gmail.com";
            String senderName = "Nginep";
            String subject = "Reset password through this link";

            // Create reset password URL with email parameter
            String resetPasswordUrl = "http://localhost:3000/reset-password?email=" + URLEncoder.encode(email, StandardCharsets.UTF_8.toString());

            String content = "Dear [[email]],<br><br>" + "Please click button below to re-direct to reset password page and reset your password:<br><br>" + "<a href=\"[[resetPasswordUrl]]\" style=\"background-color: #FF385C; border: none; color: white; padding: 15px 32px; text-align: center; text-decoration: none; display: inline-block; font-size: 16px; margin: 4px 2px; cursor: pointer;\">Reset Password</a><br><br>" + "Thank you,<br>" + "Nginep Team";

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(new InternetAddress(fromAddress, senderName));
            helper.setTo(email);
            helper.setSubject(subject);

            content = content.replace("[[email]]", email);
            content = content.replace("[[resetPasswordUrl]]", resetPasswordUrl);
            helper.setText(content, true);  // Set to true for HTML content

            javaMailSender.send(message);

            return "Send verification reset password success";
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.info("Error sending verification code:" + e);
            throw new ApplicationException("Send verification code failed");
        }
    }

    @Override
    public String verifyUser(VerifyRequestDto verifyRequestDto) {
        if (authService.verifyAccount(verifyRequestDto)) {
            return "Account verification success";
        } else {
            throw new ApplicationException("Account verification failed");
        }
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
        response.setRole(user.getRole().name());
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