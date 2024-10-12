package com.example.nginep.auth.entity;

import com.example.nginep.users.entity.Users;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class UserAuth extends Users implements UserDetails {
    private final Users user;

    public UserAuth (Users user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getProfilePicture() {
        return user.getProfilePicture();
    }

    @Override
    public String getEmail() {
        return user.getEmail();
    }

//    @Override
//    public Long getId() {
//        return user.getId();
//    }
}
