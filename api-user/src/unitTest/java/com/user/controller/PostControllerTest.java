package com.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.storage.entity.User;
import com.user.config.security.SecurityConfig;
import com.user.config.security.WithMockUserPrincipal;
import com.user.dto.request.PostRegisterRequest;
import com.user.service.AuthService;
import com.user.service.PostService;
import com.user.support.fixture.dto.request.PostRegisterRequestFixtureFactory;
import com.user.utils.error.CommonException;
import com.user.utils.token.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static com.user.enums.ErrorType.INVALID_REQUEST;
import static com.user.enums.ErrorType.LOGIN_REQUIRED;
import static com.user.enums.ErrorType.PRODUCT_NOT_FOUND;
import static com.user.enums.ErrorType.PRODUCT_PREMIUM_REQUIRED;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PostController.class)
@Import(SecurityConfig.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private PostService postService;

    @Test
    @WithMockUserPrincipal
    @DisplayName("게시물 등록이 성공하면 201 Created 응답을 반환한다.")
    void postRegisterSuccess() throws Exception {
        // given
        PostRegisterRequest request = PostRegisterRequestFixtureFactory.createWithNeedPremium(true);
        String json = objectMapper.writeValueAsString(request);
        // postService.register method is called, but it just returns without actually doing anything.

        // when && then
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("로그인 하지 않으면 게시물 등록시 401 Unauthorized 응답을 반환한다")
    void postRegisterWhenNotLogin() throws Exception {
        // given
        PostRegisterRequest request = PostRegisterRequestFixtureFactory.createWithNeedPremium(true);
        String json = objectMapper.writeValueAsString(request);

        // when && then
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value(LOGIN_REQUIRED.getMessage()));
    }

    @Test
    @WithMockUserPrincipal
    @DisplayName("post.register 요청시 PREMIUM 회원 아닌데 PREMIUM 게시물을 등록하는 경우 402 Payment Required 응답을 반환한다")
    void postRegisterWhenNotPremium() throws Exception {
        // given
        PostRegisterRequest request = PostRegisterRequestFixtureFactory.createWithNeedPremium(true);
        String json = objectMapper.writeValueAsString(request);
        doThrow(new CommonException(PRODUCT_PREMIUM_REQUIRED)).when(postService).register(any(User.class), eq(request));

        // when && then
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isPaymentRequired())
                .andExpect(jsonPath("$.code").value(402))
                .andExpect(jsonPath("$.message").value(PRODUCT_PREMIUM_REQUIRED.getMessage()));
    }

    @Test
    @WithMockUserPrincipal
    @DisplayName("post.register 요청시 User가 가진 상품이 없으면 404 Not Found 응답을 반환한다")
    void postRegisterWhenProductNotFound() throws Exception {
        // given
        PostRegisterRequest request = PostRegisterRequestFixtureFactory.createWithNeedPremium(true);
        String json = objectMapper.writeValueAsString(request);
        doThrow(new CommonException(PRODUCT_NOT_FOUND)).when(postService).register(any(User.class), eq(request));

        // when && then
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value(PRODUCT_NOT_FOUND.getMessage()));
    }

    @TestFactory
    @WithMockUserPrincipal
    @DisplayName("게시물 등록시 장소 유효성 검증 실패하면 400 Bad Request 응답이 반환되어야 한다")
    Stream<DynamicTest> registerPostFailureWhenLocationInvalid() {
        return PostRegisterRequestFixtureFactory.getInvalidLocationRequests().stream()
                .map(request -> dynamicTest(
                                String.format("장소에 %s를 입력하면 유효성 검증이 실패한다", request.location()),
                                () -> mockMvc.perform(post("/posts")
                                                .contentType(APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isBadRequest())
                                        .andExpect(jsonPath("$.code").value(400))
                                        .andExpect(jsonPath("$.message").value(INVALID_REQUEST.getMessage()))
                                        .andExpect(jsonPath("$.validations.location").isNotEmpty())
                        )
                );
    }

    @TestFactory
    @WithMockUserPrincipal
    @DisplayName("게시물 등록시 제목 유효성 검증 실패하면 400 Bad Request 응답이 반환되어야 한다")
    Stream<DynamicTest> registerPostFailureWhenTitleInvalid() {
        return PostRegisterRequestFixtureFactory.getInvalidTitleRequests().stream()
                .map(request -> dynamicTest(
                                String.format("제목에 %s를 입력하면 유효성 검증이 실패한다", request.title()),
                                () -> mockMvc.perform(post("/posts")
                                                .contentType(APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isBadRequest())
                                        .andExpect(jsonPath("$.code").value(400))
                                        .andExpect(jsonPath("$.message").value(INVALID_REQUEST.getMessage()))
                                        .andExpect(jsonPath("$.validations.title").isNotEmpty())
                        )
                );
    }

    @TestFactory
    @WithMockUserPrincipal
    @DisplayName("게시물 등록시 내용 유효성 검증 실패하면 400 Bad Request 응답이 반환되어야 한다")
    Stream<DynamicTest> registerPostFailureWhenContentInvalid() {
        return PostRegisterRequestFixtureFactory.getInvalidContentRequests().stream()
                .map(request -> dynamicTest(
                                String.format("내용에 %s를 입력하면 유효성 검증이 실패한다", request.content()),
                                () -> mockMvc.perform(post("/posts")
                                                .contentType(APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isBadRequest())
                                        .andExpect(jsonPath("$.code").value(400))
                                        .andExpect(jsonPath("$.message").value(INVALID_REQUEST.getMessage()))
                                        .andExpect(jsonPath("$.validations.content").isNotEmpty())
                        )
                );
    }

    @TestFactory
    @WithMockUserPrincipal
    @DisplayName("게시물 등록시 needPremium 유효성 검증 실패하면 400 Bad Request 응답이 반환되어야 한다")
    Stream<DynamicTest> registerPostFailureWhenNeedPremiumInvalid() {
        return PostRegisterRequestFixtureFactory.getInvalidNeedPremiumRequests().stream()
                .map(request -> dynamicTest(
                                String.format("needPremium에 %s를 입력하면 유효성 검증이 실패한다", request.needPremium()),
                                () -> mockMvc.perform(post("/posts")
                                                .contentType(APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isBadRequest())
                                        .andExpect(jsonPath("$.code").value(400))
                                        .andExpect(jsonPath("$.message").value(INVALID_REQUEST.getMessage()))
                                        .andExpect(jsonPath("$.validations.needPremium").isNotEmpty())
                        )
                );
    }
}