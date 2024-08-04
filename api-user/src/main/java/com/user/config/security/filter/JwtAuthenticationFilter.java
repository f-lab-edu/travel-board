package com.user.config.security.filter;

import com.user.config.security.UserPrincipal;
import com.user.service.AuthService;
import com.user.utils.error.CommonException;
import com.user.utils.token.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Optional;

import static com.user.enums.TokenType.ACCESS;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;
    private final HandlerExceptionResolver resolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        Optional<String> token = jwtTokenProvider.extractTokenFromHeader(authorization);

        if (token.isPresent()) {
            try {
                Long userId = jwtTokenProvider.getUserId(ACCESS, token.get());
                UserPrincipal principal = authService.getUserPrincipal(userId);
                UsernamePasswordAuthenticationToken authenticated = UsernamePasswordAuthenticationToken.authenticated(
                        principal, token.get(), principal.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authenticated);
            } catch (CommonException e) {
                resolver.resolveException(request, response, null, e);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
