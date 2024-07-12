package com.user.domain;

import com.storage.entity.Account;
import com.storage.repository.AccountRepository;
import com.user.controller.request.UserRegisterRequest;
import com.user.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class ApiE2eTest {

    @Autowired
    UserService userService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @DisplayName("")
    @Test
    void test() {
        System.out.println("e2eTest");
        userService.register(new UserRegisterRequest("test", "test", "test", "test", "test"));

        Account account = accountRepository.findByEmail("test").orElseThrow();

        Assertions.assertThat(account.getEmail()).isEqualTo("test");
        org.junit.jupiter.api.Assertions.assertTrue(passwordEncoder.matches("test", account.getPassword()));
    }
}
