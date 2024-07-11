package com.user.service;

import com.storage.entity.Account;
import com.storage.entity.User;
import com.storage.repository.AccountRepository;
import com.storage.repository.UserRepository;
import com.user.controller.request.UserRegisterRequest;
import com.user.domain.account.AccountManager;
import com.user.domain.user.UserManager;
import com.user.utils.error.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.user.utils.error.ErrorType.DUPLICATED_EMAIL;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void register(UserRegisterRequest request) {
        accountRepository.findByEmail(request.email()).ifPresent(account -> {
            throw new ApplicationException(DUPLICATED_EMAIL);
        });

        Account account = AccountManager.create(request.email(), request.password(), passwordEncoder);
        User user = UserManager.create(account, request.nickname(), request.profileImageUrl(), request.bio());

        accountRepository.save(account);
        userRepository.save(user);
    }
}
