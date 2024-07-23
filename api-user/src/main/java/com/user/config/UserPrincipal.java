package com.user.config;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class UserPrincipal extends User {

    private final com.storage.entity.User user;
    private static final String ROLE_USER = "ROLE_USER";

    public UserPrincipal(com.storage.entity.User user) {
        super(user.getAccount().getEmail(), user.getAccount().getPassword(), List.of(new SimpleGrantedAuthority(ROLE_USER)));
        this.user = user;
    }
}
