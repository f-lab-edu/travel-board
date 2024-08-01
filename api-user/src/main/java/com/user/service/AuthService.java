package com.user.service;

import com.storage.entity.Account;
import com.storage.entity.User;
import com.storage.repository.AccountRepository;
import com.storage.repository.UserRepository;
import com.user.config.security.UserPrincipal;
import com.user.domain.account.AccountCreator;
import com.user.domain.user.UserCreator;
import com.user.domain.user.UserUpdater;
import com.user.dto.request.UserRegisterRequest;
import com.user.utils.error.CommonException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.user.enums.ErrorType.DUPLICATED_EMAIL;
import static com.user.enums.ErrorType.LOGIN_FAIL;
import static com.user.enums.ErrorType.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserUpdater userUpdater;

    @Transactional
    public void register(UserRegisterRequest request) {
        accountRepository.findByEmail(request.email()).ifPresent(account -> {
            throw new CommonException(DUPLICATED_EMAIL);
        });

        Account account = AccountCreator.create(request.email(), request.password(), passwordEncoder);
        User user = UserCreator.create(account, request.nickname(), request.profileImageUrl(), request.bio());

        accountRepository.save(account);
        userRepository.save(user);
    }

    public User getUser(String email) {
        return userRepository.findByAccountEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(LOGIN_FAIL.getMessage()));
    }

    @Transactional
    public void registerRefreshToken(User user, String refreshToken) {
        userUpdater.updateRefreshToken(user, refreshToken);
    }

    public UserPrincipal getUserPrincipal(Long userId) {
        User user = userRepository.findByIdWithAccount(userId)
                .orElseThrow(() -> new CommonException(USER_NOT_FOUND));
        return UserPrincipal.of(user);
    }
}
