package com.user.service;

import com.storage.entity.Account;
import com.storage.entity.User;
import com.storage.repository.AccountRepository;
import com.storage.repository.UserRepository;
import com.user.config.security.UserPrincipal;
import com.user.domain.user.UserUpdater;
import com.user.dto.request.LoginRequest;
import com.user.dto.request.UserRegisterRequest;
import com.user.dto.response.TokenResponse;
import com.user.support.fixture.dto.request.LoginRequestFixtureFactory;
import com.user.support.fixture.dto.request.UserRegisterRequestFixtureFactory;
import com.user.support.fixture.entity.UserFixtureFactory;
import com.user.utils.error.CommonException;
import com.user.utils.token.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.user.enums.ErrorType.DUPLICATED_EMAIL;
import static com.user.enums.ErrorType.LOGIN_FAIL;
import static com.user.enums.ErrorType.UNAUTHORIZED_TOKEN;
import static com.user.enums.ErrorType.USER_NOT_FOUND;
import static com.user.enums.TokenType.REFRESH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    @Mock
    private UserUpdater userUpdater;
    @Mock
    private JwtTokenProvider jwtTokenProvider;

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

    @Test
    @DisplayName("유효한 이메일과 패스워드로 로그인이 성공하면 토큰이 반환된다")
    void successfulLoginReturnsTokenResponse() {
        // given
        LoginRequest loginRequest = LoginRequestFixtureFactory.create();
        User user = UserFixtureFactory.create();
        given(userRepository.findByAccountEmail(loginRequest.email())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(loginRequest.password(), user.getAccount().getPassword())).willReturn(true);
        given(jwtTokenProvider.generateToken(any(), any(), any())).willReturn("accessToken", "refreshToken");

        // when
        TokenResponse tokens = assertDoesNotThrow(() -> authService.login(loginRequest));

        // then
        assertEquals("accessToken", tokens.accessToken());
        assertEquals("refreshToken", tokens.refreshToken());
        then(userUpdater).should().updateRefreshToken(user, "refreshToken");
    }

    @Test
    @DisplayName("가입하지 않은 이메일로 로그인 시 예외가 발생해야 한다")
    void loginWithNotRegisteredEmail() {
        // given
        LoginRequest loginRequest = LoginRequestFixtureFactory.create();
        given(userRepository.findByAccountEmail(loginRequest.email())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(CommonException.class)
                .hasMessage(LOGIN_FAIL.getMessage());
    }

    @Test
    @DisplayName("패스워드가 일치하지 않는 경우 로그인 시 예외가 발생해야 한다")
    void loginWithNotMatchedPassword() {
        // given
        LoginRequest loginRequest = LoginRequestFixtureFactory.create();
        User user = UserFixtureFactory.create();
        given(userRepository.findByAccountEmail(loginRequest.email())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(loginRequest.password(), user.getAccount().getPassword())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(CommonException.class)
                .hasMessage(LOGIN_FAIL.getMessage());
    }

    @Test
    @DisplayName("존재하는 유저 아이디가 주어지면 유저 프린시펄이 반환된다")
    void existUserIdReturnsUserPrincipal() {
        // given
        Long userId = 1L;
        User user = UserFixtureFactory.create();
        given(userRepository.findByIdWithAccount(userId)).willReturn(Optional.of(user));

        // when
        UserPrincipal userPrincipal = authService.getUserPrincipal(userId);

        // then
        assertThat(userPrincipal.getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("존재하지 않은 유저 아이디가 주어지면 예외가 발생한다")
    void nonExistUserIdThrowsCommonException() {
        // given
        Long userId = 1L;
        given(userRepository.findByIdWithAccount(userId)).willReturn(Optional.empty());

        // when && then
        assertThatThrownBy(() -> authService.getUserPrincipal(userId))
                .isInstanceOf(CommonException.class)
                .hasMessage(USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("유효한 리프레시 토큰이 주어지면 엑세스토큰을 생성한다")
    void validRefreshTokenReturnsNewAccessToken() {
        // given
        String refreshToken = "validRefreshToken";
        Long userId = 1L;
        User user = UserFixtureFactory.create();
        String expectedAccessToken = "newAccessToken";

        given(jwtTokenProvider.getUserId(REFRESH, refreshToken)).willReturn(userId);
        given(userRepository.findByIdAndRefreshToken(userId, refreshToken)).willReturn(Optional.of(user));
        given(jwtTokenProvider.generateToken(any(), any(), any())).willReturn(expectedAccessToken);

        // when
        String accessToken = authService.reissueAccessToken(refreshToken);

        // then
        assertThat(accessToken).isEqualTo(expectedAccessToken);
    }

    @Test
    @DisplayName("유효한 리프레시 토큰이더라도 사용자가 가지지 않으면 예외가 발생한다. 예상 상황 이중 로그인 방지")
    void validRefreshTokenButUserNotfoundThrowsCommonException() {
        // given
        String refreshToken = "validRefreshToken";
        Long userId = 1L;

        given(jwtTokenProvider.getUserId(REFRESH, refreshToken)).willReturn(userId);
        given(userRepository.findByIdAndRefreshToken(userId, refreshToken)).willReturn(Optional.empty());

        // when && then
        assertThatThrownBy(() -> authService.reissueAccessToken(refreshToken))
                .isInstanceOf(CommonException.class)
                .hasMessage(USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("유효하지 않은 리프레시 토큰이 주어지면 예외가 발생한다")
    void invalidRefreshTokenThrowsCommonException() {
        // given
        String refreshToken = "invalidRefreshToken";

        given(jwtTokenProvider.getUserId(REFRESH, refreshToken)).willThrow(new CommonException(UNAUTHORIZED_TOKEN));

        // when && then
        assertThatThrownBy(() -> authService.reissueAccessToken(refreshToken))
                .isInstanceOf(CommonException.class)
                .hasMessage(UNAUTHORIZED_TOKEN.getMessage());
    }
}
