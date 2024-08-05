package com.user.support.fixture.dto.request;

import com.user.dto.request.AccessTokenReissueRequest;

import java.util.List;

public class AccessTokenReissueRequestFixtureFactory {

    private static final String DEFAULT_REFRESH_TOKEN = "refreshToken.refreshToken.refreshToken";

    public static AccessTokenReissueRequest createMockRefreshToken() {
        return new AccessTokenReissueRequest(DEFAULT_REFRESH_TOKEN);
    }

    public static List<AccessTokenReissueRequest> getInvalidRefreshTokenRequests() {
        return List.of(
                new AccessTokenReissueRequest(null),
                new AccessTokenReissueRequest(""),
                new AccessTokenReissueRequest("   ")
        );
    }
}
