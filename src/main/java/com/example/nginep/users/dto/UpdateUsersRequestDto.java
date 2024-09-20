package com.example.nginep.users.dto;

import com.example.nginep.users.entity.Users;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class UpdateUsersRequestDto {
    private String fullName;
    private String email;
    private String password;
    private String profilePicture;
    private String picturePublicId;
    private Boolean isVerified;
    private LocalDate dateOfBirth;
    private String gender;
    private String phoneNumber;
    private String aboutYourself;
    private LocalTime checkinTime;
    private LocalTime checkoutTime;
    private String cancelPolicy;
    private String bankName;
    private String bankAccountNumber;
    private String bankHolderName;

    public Users toEntity(Users user) {
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(password);
        user.setProfilePicture(profilePicture);
        user.setProfilePicture(picturePublicId);
        user.setIsVerified(isVerified);
        user.setDateOfBirth(dateOfBirth);
        user.setGender(gender);
        user.setPhoneNumber(phoneNumber);
        user.setAboutYourself(aboutYourself);
        user.setCheckinTime(checkinTime);
        user.setCheckoutTime(checkoutTime);
        user.setCancelPolicy(cancelPolicy);
        user.setBankName(bankName);
        user.setBankAccountNumber(bankAccountNumber);
        user.setBankHolderName(bankHolderName);
        return user;
    }
}
