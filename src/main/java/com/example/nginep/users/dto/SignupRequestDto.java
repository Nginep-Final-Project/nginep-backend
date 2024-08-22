package com.example.nginep.users.dto;

import com.example.nginep.users.entity.Users;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
        return user;
    }
}
