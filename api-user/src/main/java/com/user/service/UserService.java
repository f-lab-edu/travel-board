package com.user.service;

import com.storage.entity.Account;
import com.storage.entity.User;
import com.storage.repository.AccountRepository;
import com.storage.repository.UserRepository;
import com.user.controller.request.UserRegisterRequest;
import com.user.service.factory.AccountFactory;
import com.user.service.factory.UserFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long register(UserRegisterRequest request) {
        accountRepository.findByEmail(request.email()).ifPresent(account -> {
            throw new IllegalStateException("Email is already in use");
        });

        String encodedPassword = passwordEncoder.encode(request.password());
        Account account = AccountFactory.create(request.email(), encodedPassword);
        User user = UserFactory.create(
                account,
                request.nickname(),
                Optional.ofNullable(request.profileImageUrl()).orElse(""),
                Optional.ofNullable(request.bio()).orElse("")
        );

        accountRepository.save(account);
        userRepository.save(user);
        return user.getId();
    }
}
