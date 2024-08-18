package com.user.domain.user;

import com.storage.entity.Product;
import com.storage.entity.ProductLevel;
import com.user.utils.error.CommonException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.user.enums.ErrorType.PRODUCT_PREMIUM_REQUIRED;

@Component
public class ProductValidator {
    
    public void validate(Product product, LocalDateTime now) {
        if (!isPremium(product) || isExpired(product, now)) {
            throw new CommonException(PRODUCT_PREMIUM_REQUIRED);
        }
    }

    private boolean isPremium(Product product) {
        return product.getLevel() == ProductLevel.PREMIUM;
    }

    private boolean isExpired(Product product, LocalDateTime now) {
        return product.getEndAt().isBefore(now);
    }
}
