package com.user.domain;

import com.storage.entity.Account;
import com.storage.repository.AccountRepository;
import com.storage.repository.UserRepository;
import com.user.MockitoTestSupport;
import com.user.controller.request.UserRegisterRequest;
import com.user.service.UserService;
import com.user.utils.error.CommonException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.BDDMockito.given;

class UserServiceTest extends MockitoTestSupport {

    @InjectMocks
    UserService userService;
    @Mock
    AccountRepository accountRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    UserRepository userRepository;

    @DisplayName("")
    @Test
    void test() {
        // given
        UserRegisterRequest request = new UserRegisterRequest("test", "test", "test", "test", "test");
        given(accountRepository.findByEmail("test")).willReturn(Optional.of(Account.builder().build()));

        // when,then(expected)
        Assertions.assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(CommonException.class)
                .hasMessageContaining("Email is already in use");
    }
}
