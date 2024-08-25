package com.user.domain.product;

import com.storage.entity.Product;
import com.storage.entity.ProductLevel;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ProductValidator {
    
    public boolean isPremiumProduct(Product product, LocalDateTime now) {
        return isPremiumProductLevel(product) && !isExpired(product, now);
    }

    private boolean isPremiumProductLevel(Product product) {
        return product.getLevel() == ProductLevel.PREMIUM;
    }

    private boolean isExpired(Product product, LocalDateTime now) {
        return product.getEndAt().isBefore(now);
    }
}
