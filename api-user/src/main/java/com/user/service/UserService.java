package com.user.service;

import com.storage.entity.Account;
import com.storage.entity.User;
import com.storage.repository.AccountRepository;
import com.storage.repository.UserRepository;
import com.user.controller.request.UserRegisterRequest;
import com.user.exception.ConflictException;
import com.user.service.factory.AccountFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long register(UserRegisterRequest request) {
        checkDuplicateEmail(request.email());
        Account account = registerAccount(request.password(), request.email());
        User user = registerUser(request, account);
        updateAuditing(account, user);
        return user.getId();
    }

    private void updateAuditing(Account account, User user) {
        account.initializedBy(account.getId());
        user.initializedBy(account.getId());
    }

    private User registerUser(UserRegisterRequest request, Account newAccount) {
        User user = request.toUser(newAccount);
        return userRepository.save(user);
    }

    private Account registerAccount(String password, String email) {
        String encodedPassword = passwordEncoder.encode(password);
        Account account = AccountFactory.create(email, encodedPassword);
        return accountRepository.save(account);
    }

    private void checkDuplicateEmail(String email) {
        accountRepository.findByEmail(email).ifPresent(account -> {
            throw new ConflictException("Email is already in use");
        });
    }
}
