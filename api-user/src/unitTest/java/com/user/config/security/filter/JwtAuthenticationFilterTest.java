package com.user.config.security.filter;

import com.storage.entity.User;
import com.user.config.security.UserPrincipal;
import com.user.service.AuthService;
import com.user.support.fixture.entity.AccountFixtureFactory;
import com.user.support.fixture.entity.UserFixtureFactory;
import com.user.utils.error.CommonException;
import com.user.utils.token.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Optional;

import static com.user.enums.ErrorType.UNAUTHORIZED_TOKEN;
import static com.user.enums.TokenType.ACCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private AuthService authService;
    @Mock
    private HandlerExceptionResolver resolver;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("유효한 Authorization 헤더의 토큰이 주어지면 SecurityContext에 인증 정보를 설정한다")
    void validTokenSetsAuthenticationInSecurityContext() throws ServletException, IOException {
        // given
        String token = "validToken";
        Long userId = 1L;
        User user = UserFixtureFactory.create(AccountFixtureFactory.create());
        UserPrincipal principal = UserPrincipal.of(user);
        String authorization = "Bearer " + token;

        given(request.getHeader(AUTHORIZATION)).willReturn(authorization);
        given(jwtTokenProvider.extractTokenFromHeader(authorization)).willReturn(Optional.of(token));
        given(jwtTokenProvider.getUserId(ACCESS, token)).willReturn(userId);
        given(authService.getUserPrincipal(userId)).willReturn(principal);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal authenticatedPrincipal = (UserPrincipal) authentication.getPrincipal();

        assertThat(authenticatedPrincipal).isEqualTo(principal);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("유효하지 않은 Authorization 헤더의 토큰이 주어지면 예외를 처리한다")
    void invalidTokenThrowsCommonExceptionAndResolvesException() throws ServletException, IOException {
        // given
        String token = "invalidToken";
        String authorizationHeader = "Bearer " + token;
        CommonException commonException = new CommonException(UNAUTHORIZED_TOKEN);

        given(request.getHeader(AUTHORIZATION)).willReturn(authorizationHeader);
        given(jwtTokenProvider.extractTokenFromHeader(authorizationHeader)).willReturn(Optional.of(token));
        given(jwtTokenProvider.getUserId(ACCESS, token)).willThrow(commonException);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(resolver).resolveException(request, response, null, commonException);
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("토큰이 주어지지 않으면 토큰 인증 절차를 bypass한다")
    void noTokenBypassesTokenAuthentication() throws ServletException, IOException {
        // given
        given(request.getHeader(AUTHORIZATION)).willReturn(null);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
    }
}