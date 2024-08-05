package com.user.support.fixture.dto.request;

import com.user.dto.request.LoginRequest;

import java.util.List;

public class LoginRequestFixtureFactory {

    private static final String DEFAULT_EMAIL = "valid@email.com";
    private static final String DEFAULT_PASSWORD = "password";

    public static LoginRequest create() {
        return new LoginRequest(DEFAULT_EMAIL, DEFAULT_PASSWORD);
    }

    public static List<LoginRequest> getInvalidEmailRequests() {
        return List.of(
                new LoginRequest(null, DEFAULT_PASSWORD),
                new LoginRequest("", DEFAULT_PASSWORD),
                new LoginRequest("   ", DEFAULT_PASSWORD)
        );
    }

    public static List<LoginRequest> getInvalidPasswordRequests() {
        return List.of(
                new LoginRequest(DEFAULT_EMAIL, null),
                new LoginRequest(DEFAULT_EMAIL, ""),
                new LoginRequest(DEFAULT_EMAIL, "    ")
        );
    }
}
