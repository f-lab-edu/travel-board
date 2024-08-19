package com.user.support.fixture.dto.request;

import com.user.dto.request.PostRegisterRequest;

import java.util.List;

public class PostRegisterRequestFixtureFactory {

    private static final String DEFAULT_LOCATION = "location";
    private static final String DEFAULT_TITLE = "title";
    private static final String DEFAULT_CONTENT = "content";
    private static final boolean DEFAULT_NEED_PREMIUM = false;

    public static PostRegisterRequest createWithNeedPremium(boolean needPremium) {
        return new PostRegisterRequest(DEFAULT_LOCATION, DEFAULT_TITLE, DEFAULT_CONTENT, needPremium);
    }

    public static List<PostRegisterRequest> getInvalidLocationRequests() {
        return List.of(
                new PostRegisterRequest(null, DEFAULT_TITLE, DEFAULT_CONTENT, DEFAULT_NEED_PREMIUM),
                new PostRegisterRequest("", DEFAULT_TITLE, DEFAULT_CONTENT, DEFAULT_NEED_PREMIUM),
                new PostRegisterRequest("   ", DEFAULT_TITLE, DEFAULT_CONTENT, DEFAULT_NEED_PREMIUM),
                new PostRegisterRequest("L".repeat(16), DEFAULT_TITLE, DEFAULT_CONTENT, DEFAULT_NEED_PREMIUM)
        );
    }

    public static List<PostRegisterRequest> getInvalidTitleRequests() {
        return List.of(
                new PostRegisterRequest(DEFAULT_LOCATION, null, DEFAULT_CONTENT, DEFAULT_NEED_PREMIUM),
                new PostRegisterRequest(DEFAULT_LOCATION, "", DEFAULT_CONTENT, DEFAULT_NEED_PREMIUM),
                new PostRegisterRequest(DEFAULT_LOCATION, "   ", DEFAULT_CONTENT, DEFAULT_NEED_PREMIUM),
                new PostRegisterRequest(DEFAULT_LOCATION, "T".repeat(86), DEFAULT_CONTENT, DEFAULT_NEED_PREMIUM)
        );
    }

    public static List<PostRegisterRequest> getInvalidContentRequests() {
        return List.of(
                new PostRegisterRequest(DEFAULT_LOCATION, DEFAULT_TITLE, null, DEFAULT_NEED_PREMIUM),
                new PostRegisterRequest(DEFAULT_LOCATION, DEFAULT_TITLE, "", DEFAULT_NEED_PREMIUM),
                new PostRegisterRequest(DEFAULT_LOCATION, DEFAULT_TITLE, "   ", DEFAULT_NEED_PREMIUM)
        );
    }

    public static List<PostRegisterRequest> getInvalidNeedPremiumRequests() {
        return List.of(
                new PostRegisterRequest(DEFAULT_LOCATION, DEFAULT_TITLE, DEFAULT_CONTENT, null)
        );
    }
}
