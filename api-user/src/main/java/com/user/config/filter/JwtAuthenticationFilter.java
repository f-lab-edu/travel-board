package com.user.config.filter;

import com.user.config.UserPrincipal;
import com.user.utils.error.CommonException;
import com.user.utils.token.JwtTokenProvider;
import com.user.utils.token.TokenPayload;
import com.user.utils.token.TokenType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final HandlerExceptionResolver resolver;
    private static final Set<String> EXCLUDED_PATHS = Set.of("/auth/login", "/auth/signup");

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return EXCLUDED_PATHS.contains(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        Optional<String> token = extractTokenFromHeader(authorization);

        if (token.isPresent()) {
            try {
                TokenPayload tokenPayload = jwtTokenProvider.getUserId(TokenType.ACCESS, token.get());
                UserPrincipal principal = new UserPrincipal(tokenPayload);
                UsernamePasswordAuthenticationToken authenticated = UsernamePasswordAuthenticationToken.authenticated(
                        principal,
                        token.get(),
                        principal.getAuthorities()
                );

                SecurityContextHolder.getContext().setAuthentication(authenticated);
            } catch (CommonException e) {
                resolver.resolveException(request, response, null, e);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    public Optional<String> extractTokenFromHeader(String authorization) {
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            return Optional.of(authorization.substring(7));
        }
        return Optional.empty();
    }
}
