package com.user.domain.user;

import com.storage.entity.Product;
import com.storage.entity.ProductLevel;
import com.storage.entity.User;
import com.user.support.fixture.entity.AccountFixtureFactory;
import com.user.support.fixture.entity.ProductFixtureFactory;
import com.user.support.fixture.entity.UserFixtureFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ProductValidatorTest {

    @InjectMocks
    private ProductValidator productValidator;

    @Test
    @DisplayName("프리미엄 상품이면 true를 반환한다.")
    void isPremiumProductProductReturnTrue() {
        // given
        User user = UserFixtureFactory.create(AccountFixtureFactory.create());
        Product product = ProductFixtureFactory.createWith(user, ProductLevel.PREMIUM);
        LocalDateTime now = product.getEndAt().minusDays(1L);

        // when
        boolean isPremium = productValidator.isPremiumProduct(product, now);

        // then
        assertThat(isPremium).isTrue();
    }

    @Test
    @DisplayName("프리미엄 상품이 아니면 false를 반환한다.")
    void isPremiumThrowsExceptionForNotPremiumProductProductLevelProduct() {
        // given
        User user = UserFixtureFactory.create(AccountFixtureFactory.create());
        Product product = ProductFixtureFactory.createWith(user, ProductLevel.BASIC);
        LocalDateTime now = product.getEndAt().minusDays(1L);

        // when
        boolean isPremium = productValidator.isPremiumProduct(product, now);

        // then
        assertThat(isPremium).isFalse();
    }

    @Test
    @DisplayName("만료된 상품이면 false를 반환한다.")
    void isPremiumProductProductLevelThrowsExceptionForExpiredProduct() {
        // given
        User user = UserFixtureFactory.create(AccountFixtureFactory.create());
        Product product = ProductFixtureFactory.createWith(user, ProductLevel.PREMIUM);
        LocalDateTime now = product.getEndAt().plusDays(1L);

        // when
        boolean isPremium = productValidator.isPremiumProduct(product, now);

        // then
        assertThat(isPremium).isFalse();
    }
}