package com.user.support.fixture.dto.request;

import com.user.dto.request.UserRegisterRequest;

import java.util.List;

public class UserRegisterRequestFixtureFactory {

    private static final String DEFAULT_EMAIL = "valid@gmail.com";
    private static final String DEFAULT_PASSWORD = "password";
    private static final String DEFAULT_NICKNAME = "nickname";
    private static final String DEFAULT_PROFILE_IMAGE_URL = "https://travel/profileImageUrl.png";
    private static final String DEFAULT_BIO = "introduce myself";

    public static UserRegisterRequest create() {
        return new UserRegisterRequest(DEFAULT_EMAIL, DEFAULT_PASSWORD, DEFAULT_NICKNAME, DEFAULT_PROFILE_IMAGE_URL, DEFAULT_BIO);
    }

    public static List<UserRegisterRequest> getInvalidEmailRequests() {
        return List.of(
                new UserRegisterRequest(null, DEFAULT_PASSWORD, DEFAULT_NICKNAME, DEFAULT_PROFILE_IMAGE_URL, DEFAULT_BIO),
                new UserRegisterRequest("", DEFAULT_PASSWORD, DEFAULT_NICKNAME, DEFAULT_PROFILE_IMAGE_URL, DEFAULT_BIO),
                new UserRegisterRequest("invalid_email", DEFAULT_PASSWORD, DEFAULT_NICKNAME, DEFAULT_PROFILE_IMAGE_URL, DEFAULT_BIO)
        );
    }

    public static List<UserRegisterRequest> getInvalidPasswordRequests() {
        return List.of(
                new UserRegisterRequest(DEFAULT_EMAIL, null, DEFAULT_NICKNAME, DEFAULT_PROFILE_IMAGE_URL, DEFAULT_BIO),
                new UserRegisterRequest(DEFAULT_EMAIL, "", DEFAULT_NICKNAME, DEFAULT_PROFILE_IMAGE_URL, DEFAULT_BIO),
                new UserRegisterRequest(DEFAULT_EMAIL, "short", DEFAULT_NICKNAME, DEFAULT_PROFILE_IMAGE_URL, DEFAULT_BIO),
                new UserRegisterRequest(DEFAULT_EMAIL, "longlonglonglonglonglong", DEFAULT_NICKNAME, DEFAULT_PROFILE_IMAGE_URL, DEFAULT_BIO),
                new UserRegisterRequest(DEFAULT_EMAIL, "password)(**&&", DEFAULT_NICKNAME, DEFAULT_PROFILE_IMAGE_URL, DEFAULT_BIO)
        );
    }

    public static List<UserRegisterRequest> getInvalidNicknameRequests() {
        return List.of(
                new UserRegisterRequest(DEFAULT_EMAIL, DEFAULT_PASSWORD, null, DEFAULT_PROFILE_IMAGE_URL, DEFAULT_BIO),
                new UserRegisterRequest(DEFAULT_EMAIL, DEFAULT_PASSWORD, "", DEFAULT_PROFILE_IMAGE_URL, DEFAULT_BIO),
                new UserRegisterRequest(DEFAULT_EMAIL, DEFAULT_PASSWORD, "n".repeat(2), DEFAULT_PROFILE_IMAGE_URL, DEFAULT_BIO),
                new UserRegisterRequest(DEFAULT_EMAIL, DEFAULT_PASSWORD, "nick name", DEFAULT_PROFILE_IMAGE_URL, DEFAULT_BIO),
                new UserRegisterRequest(DEFAULT_EMAIL, DEFAULT_PASSWORD, "닉네임", DEFAULT_PROFILE_IMAGE_URL, DEFAULT_BIO),
                new UserRegisterRequest(DEFAULT_EMAIL, DEFAULT_PASSWORD, "n".repeat(21), DEFAULT_PROFILE_IMAGE_URL, DEFAULT_BIO)
        );
    }

    public static List<UserRegisterRequest> getInvalidProfileImageUrlRequests() {
        return List.of(
                new UserRegisterRequest(DEFAULT_EMAIL, DEFAULT_PASSWORD, DEFAULT_NICKNAME, "invalid_url", DEFAULT_BIO),
                new UserRegisterRequest(DEFAULT_EMAIL, DEFAULT_PASSWORD, DEFAULT_NICKNAME, "L".repeat(513), DEFAULT_BIO),
                new UserRegisterRequest(DEFAULT_EMAIL, DEFAULT_PASSWORD, DEFAULT_NICKNAME, "http", DEFAULT_BIO)
        );
    }

    public static List<UserRegisterRequest> getInvalidBioRequests() {
        return List.of(
                new UserRegisterRequest(DEFAULT_EMAIL, DEFAULT_PASSWORD, DEFAULT_NICKNAME, DEFAULT_PROFILE_IMAGE_URL, "가".repeat(101))
        );
    }
}
