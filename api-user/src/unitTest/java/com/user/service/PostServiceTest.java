package com.user.service;

import com.storage.entity.Account;
import com.storage.entity.Product;
import com.storage.entity.ProductLevel;
import com.storage.entity.User;
import com.user.domain.post.PostCreator;
import com.user.domain.user.ProductFinder;
import com.user.domain.user.ProductValidator;
import com.user.dto.request.PostRegisterRequest;
import com.user.support.fixture.dto.request.PostRegisterRequestFixtureFactory;
import com.user.support.fixture.entity.AccountFixtureFactory;
import com.user.support.fixture.entity.ProductFixtureFactory;
import com.user.support.fixture.entity.UserFixtureFactory;
import com.user.utils.error.CommonException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;
    @Mock
    private ProductFinder productFinder;
    @Mock
    private ProductValidator productValidator;
    @Mock
    private PostCreator postCreator;

    @Test
    @DisplayName("프리미엄 게시글이 아니면 게시글은 바로 등록되어야 한다")
    void registerPostWhenPremiumIsNotNeeded() {
        // given
        Account account = AccountFixtureFactory.create();
        User user = UserFixtureFactory.create(account);
        PostRegisterRequest request = PostRegisterRequestFixtureFactory.createWithNeedPremium(false);

        // when
        postService.register(user, request);

        // then
        then(postCreator).should().create(user, request.location(), request.title(), request.content(), request.needPremium());
    }

    @Test
    @DisplayName("프리미엄 게시글은 등록되기 전에 상품을 찾고 검증되어야 한다")
    void registerPostWhenPremiumIsNeededAndProductIsValid() {
        // given
        Account account = AccountFixtureFactory.create();
        User user = UserFixtureFactory.create(account);
        Product product = ProductFixtureFactory.createWith(user, ProductLevel.PREMIUM);
        given(productFinder.find(user)).willReturn(product);
        PostRegisterRequest request = PostRegisterRequestFixtureFactory.createWithNeedPremium(true);

        // when
        postService.register(user, request);

        // then
        then(productValidator).should().validate(eq(product), any(LocalDateTime.class));
        then(postCreator).should().create(user, request.location(), request.title(), request.content(), request.needPremium());
    }

    @Test
    @DisplayName("프리미엄 게시글 등록시 상품이 없으면 등록되지 않는다")
    void notRegisterPremiumPostWhenProductNotExists() {
        // given
        Account account = AccountFixtureFactory.create();
        User user = UserFixtureFactory.create(account);
        given(productFinder.find(user)).willThrow(CommonException.class);
        PostRegisterRequest request = PostRegisterRequestFixtureFactory.createWithNeedPremium(true);

        // when && then
        assertThatThrownBy(() -> postService.register(user, request))
                .isInstanceOf(CommonException.class);
    }

    @Test
    @DisplayName("프리미엄 게시글 등록시 상품의 유효성 검증에 실패하면 등록되지 않는다")
    void notRegisterPremiumPostWhenValidationFailure() {
        // given
        Account account = AccountFixtureFactory.create();
        User user = UserFixtureFactory.create(account);
        Product product = ProductFixtureFactory.createWith(user, ProductLevel.PREMIUM);
        given(productFinder.find(user)).willReturn(product);
        willThrow(CommonException.class).given(productValidator).validate(eq(product), any(LocalDateTime.class));
        PostRegisterRequest request = PostRegisterRequestFixtureFactory.createWithNeedPremium(true);

        // when && then
        assertThatThrownBy(() -> postService.register(user, request))
                .isInstanceOf(CommonException.class);
    }
}