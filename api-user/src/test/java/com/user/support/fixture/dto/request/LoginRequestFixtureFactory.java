package com.user.support.fixture.dto.request;

import com.user.dto.request.LoginRequest;

public class LoginRequestFixtureFactory {

    private static final String DEFAULT_EMAIL = "valid@email.com";
    private static final String DEFAULT_PASSWORD = "password";

    public static LoginRequest create() {
        return new LoginRequest(DEFAULT_EMAIL, DEFAULT_PASSWORD);
    }
}
