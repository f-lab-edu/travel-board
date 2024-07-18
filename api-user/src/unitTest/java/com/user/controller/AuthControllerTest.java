package com.user.controller;

import com.user.ControllerTestSupport;
import com.user.controller.request.UserRegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("회원가입이 성공하면 201 Created 응답이 반환되어야 한다")
    void signupSuccess() throws Exception {
        // given
        UserRegisterRequest request = getUserRegisterRequest();

        // when & then
        mockMvc.perform(post("/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated());
    }

    private UserRegisterRequest getUserRegisterRequest() {
        return new UserRegisterRequest(
                "email@gmail.com",
                "password",
                "nickname",
                "https://profileImageUrl.png",
                "bio"
        );
    }
}
