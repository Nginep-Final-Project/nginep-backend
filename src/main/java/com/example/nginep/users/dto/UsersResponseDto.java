package com.example.nginep.users.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class UsersResponseDto {
    private Long id;
    private String fullName;
    private String email;
    private String profilePicture;
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
}
