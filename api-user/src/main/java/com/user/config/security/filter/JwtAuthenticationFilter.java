package com.user.config.security.filter;

import com.user.config.security.UserPrincipal;
import com.user.enums.TokenType;
import com.user.service.AuthService;
import com.user.utils.error.CommonException;
import com.user.utils.token.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final HandlerExceptionResolver resolver;
    private final AuthService authService;
    private static final Set<String> EXCLUDED_PATHS = Set.of("/auth/login", "/auth/signup");

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,
                                   @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver,
                                   AuthService authService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.resolver = resolver;
        this.authService = authService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return EXCLUDED_PATHS.contains(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        Optional<String> token = jwtTokenProvider.extractTokenFromHeader(authorization);

        if (token.isPresent()) {
            try {
                Long userId = jwtTokenProvider.getUserId(TokenType.ACCESS, token.get());
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
