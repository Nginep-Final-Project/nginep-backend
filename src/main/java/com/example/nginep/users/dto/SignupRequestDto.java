package com.example.nginep.users.dto;

import com.example.nginep.users.entity.Users;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SignupRequestDto {
    private String fullName;
    private LocalDate dateOfBirth;
    private String email;
    private String password;
    private Users.Role role;

    public Users toEntity(){
        Users user = new Users();
        user.setFullName(fullName);
        user.setDateOfBirth(dateOfBirth);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        user.setProfilePicture("https://res.cloudinary.com/dhbg53ncx/image/upload/v1724048239/y2v5dowacq3zuvraaeem.png");
        user.setAccountType(Users.AccountType.valueOf("email"));
        return user;
    }
}
