package com.user.service;

import com.storage.entity.Account;
import com.storage.entity.User;
import com.storage.repository.AccountRepository;
import com.storage.repository.UserRepository;
import com.user.dto.request.UserRegisterRequest;
import com.user.support.fixture.dto.request.UserRegisterRequestFixtureFactory;
import com.user.utils.error.CommonException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.user.enums.ErrorType.DUPLICATED_EMAIL;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("고유한 이메일로 사용자 등록시 사용자와 계정이 생성되어야 한다")
    void registerUserWithUniqueEmail() {
        // given
        UserRegisterRequest request = UserRegisterRequestFixtureFactory.create();
        given(accountRepository.findByEmail(request.email())).willReturn(Optional.empty());
        given(passwordEncoder.encode(request.password())).willReturn("encodedPassword");

        // when
        assertDoesNotThrow(() -> authService.register(request));

        // then
        then(accountRepository).should().save(any(Account.class));
        then(userRepository).should().save(any(User.class));
    }

    @Test
    @DisplayName("중복된 이메일로 사용자 등록시 예외가 발생해야 한다")
    void registerUserWithDuplicatedEmail() {
        // given
        UserRegisterRequest request = UserRegisterRequestFixtureFactory.create();
        given(accountRepository.findByEmail(request.email())).willReturn(Optional.of(mock(Account.class)));

        // when & then
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(CommonException.class)
                .hasMessage(DUPLICATED_EMAIL.getMessage());
    }
}
