package com.user.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.storage.entity.User;
import com.user.config.UserPrincipal;
import com.user.controller.request.LoginRequest;
import com.user.controller.response.TokenResponse;
import com.user.utils.error.ErrorMessage;
import com.user.utils.token.JwtTokenProvider;
import com.user.utils.token.TokenType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import java.io.IOException;
import java.util.Date;

import static com.user.utils.error.ErrorType.LOGIN_FAIL;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class EmailPasswordAuthFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper;

    public EmailPasswordAuthFilter(String loginUrl, ObjectMapper objectMapper) {
        super(loginUrl);
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        LoginRequest emailPassword = objectMapper.readValue(request.getInputStream(), LoginRequest.class);

        UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.unauthenticated(
                emailPassword.email(),
                emailPassword.password()
        );

        token.setDetails(this.authenticationDetailsSource.buildDetails(request));
        return this.getAuthenticationManager().authenticate(token);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        UserPrincipal principal = (UserPrincipal) authResult.getPrincipal();
        User user = principal.getUser();

        new JwtTokenProvider().generateToken(TokenType.ACCESS, user.getId(), new Date());
        new JwtTokenProvider().generateToken(TokenType.REFRESH, user.getId(), new Date());
        // token 생성하여 내려줄 것

        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(UTF_8.name());
        response.setStatus(SC_OK);

        objectMapper.writeValue(response.getWriter(), new TokenResponse("access", "refresh"));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(UTF_8.name());
        response.setStatus(SC_UNAUTHORIZED);
        objectMapper.writeValue(response.getWriter(), new ErrorMessage(LOGIN_FAIL));
    }
}
