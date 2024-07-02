package com.user.controller;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

import static org.springframework.boot.web.server.Cookie.SameSite.NONE;

@Component
public class CookieProvider {

    public ResponseCookie createCookie(String name, String value, Duration maxAge) {
        return ResponseCookie.from(name, value)
                .maxAge(maxAge)
                .httpOnly(true)
                .path("/")
                .sameSite(NONE.attributeValue())
                .build();
    }
}
