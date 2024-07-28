package com.user.config.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.config.security.UserPrincipal;
import com.user.dto.response.LoginSuccessResponse;
import com.user.enums.TokenType;
import com.user.service.AuthService;
import com.user.utils.token.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Date;

import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(UTF_8.name());
        response.setStatus(SC_OK);

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Date now = new Date();
        String accessToken = jwtTokenProvider.generateToken(TokenType.ACCESS, principal.getUser().getId(), now);
        String refreshToken = jwtTokenProvider.generateToken(TokenType.REFRESH, principal.getUser().getId(), now);

        authService.registerRefreshToken(principal.getUser(), refreshToken);

        LoginSuccessResponse loginResponse = LoginSuccessResponse.of(accessToken, refreshToken);
        objectMapper.writeValue(response.getWriter(), loginResponse);
    }
}