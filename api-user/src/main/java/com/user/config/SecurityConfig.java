package com.user.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.config.filter.EmailPasswordAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration(proxyBeanMethods = false)
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers("/error");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, EmailPasswordAuthFilter emailPasswordAuthFilter) throws Exception {
        return http
                .authorizeHttpRequests(registry -> registry.anyRequest().permitAll())
                .addFilterBefore(emailPasswordAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    public EmailPasswordAuthFilter emailPasswordAuthFilter(AuthenticationManager authenticationManager) {
        EmailPasswordAuthFilter filter = new EmailPasswordAuthFilter("/auth/login", objectMapper);
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
