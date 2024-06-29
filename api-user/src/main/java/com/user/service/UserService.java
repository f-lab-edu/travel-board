package com.user.service;

import com.storage.entity.Account;
import com.storage.entity.User;
import com.storage.repository.AccountRepository;
import com.storage.repository.UserRepository;
import com.user.exception.ConflictException;
import com.user.service.request.UserRegisterServiceRequest;
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
    public void register(UserRegisterServiceRequest request) {
        checkDuplicateEmail(request.email());
        Account newAccount = registerAccount(request);
        User newUser = registerUser(request, newAccount);
        updateAuditing(newAccount, newUser);
    }

    private void updateAuditing(Account account, User user) {
        account.initializedBy(account.getId());
        user.initializedBy(account.getId());
    }

    private User registerUser(UserRegisterServiceRequest request, Account newAccount) {
        User newUser = request.toUser(newAccount);
        return userRepository.save(newUser);
    }

    private Account registerAccount(UserRegisterServiceRequest request) {
        String encodedPassword = passwordEncoder.encode(request.password());
        Account newAccount = Account.createNew(request.email(), encodedPassword);
        return accountRepository.save(newAccount);
    }

    private void checkDuplicateEmail(String email) {
        accountRepository.findByEmail(email).ifPresent(account -> {
            throw new ConflictException("Email is already in use");
        });
    }
}
