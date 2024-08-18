package com.user.support.fixture.dto.request;

import com.user.dto.request.PostRegisterRequest;

public class PostRegisterRequestFixtureFactory {

    private static final String DEFAULT_LOCATION = "location";
    private static final String DEFAULT_TITLE = "title";
    private static final String DEFAULT_CONTENT = "content";

    public static PostRegisterRequest createWith(boolean needPremium) {
        return new PostRegisterRequest(DEFAULT_LOCATION, DEFAULT_TITLE, DEFAULT_CONTENT, needPremium);
    }
}
