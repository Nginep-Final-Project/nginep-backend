package com.example.nginep.users.dto;

import com.example.nginep.users.entity.Users;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SigninGoogleRequestDto {
    private String fullName;
    private String email;
    private String password;
    private String profilePicture;

    public Users toEntity(){
        Users user = new Users();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(password);
        user.setPassword(profilePicture);
        user.setRole(Users.Role.valueOf("guest"));
        user.setIsVerified(true);
        return user;
    }
}
