package com.user.domain.user;

import com.storage.entity.Product;
import com.storage.entity.ProductLevel;
import com.storage.entity.User;
import com.user.support.fixture.entity.AccountFixtureFactory;
import com.user.support.fixture.entity.ProductFixtureFactory;
import com.user.support.fixture.entity.UserFixtureFactory;
import com.user.utils.error.CommonException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static com.user.enums.ErrorType.PRODUCT_PREMIUM_REQUIRED;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class ProductValidatorTest {

    @InjectMocks
    private ProductValidator productValidator;

    @Test
    @DisplayName("프리미엄 상품이면 예외가 발생하지 않아야 한다")
    void validatePremiumDoesNotThrowExceptionForPremiumProduct() {
        // given
        User user = UserFixtureFactory.create(AccountFixtureFactory.create());
        Product product = ProductFixtureFactory.createWith(user, ProductLevel.PREMIUM);
        LocalDateTime now = product.getEndAt().minusDays(1L);

        // when && then
        assertDoesNotThrow(() -> productValidator.validatePremium(product, now));
    }

    @Test
    @DisplayName("프리미엄 상품이 아니면 예외가 발생한다.")
    void validatePremiumThrowsExceptionForNotPremiumProduct() {
        // given
        User user = UserFixtureFactory.create(AccountFixtureFactory.create());
        Product product = ProductFixtureFactory.createWith(user, ProductLevel.BASIC);
        LocalDateTime now = product.getEndAt().minusDays(1L);

        // when && then
        assertThatThrownBy(() -> productValidator.validatePremium(product, now))
                .isInstanceOf(CommonException.class)
                .hasMessage(PRODUCT_PREMIUM_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("만료된 상품이면 예외가 발생한다.")
    void validatePremiumThrowsExceptionForExpiredProduct() {
        // given
        User user = UserFixtureFactory.create(AccountFixtureFactory.create());
        Product product = ProductFixtureFactory.createWith(user, ProductLevel.PREMIUM);
        LocalDateTime now = product.getEndAt().plusDays(1L);

        // when && then
        assertThatThrownBy(() -> productValidator.validatePremium(product, now))
                .isInstanceOf(CommonException.class)
                .hasMessage(PRODUCT_PREMIUM_REQUIRED.getMessage());
    }
}