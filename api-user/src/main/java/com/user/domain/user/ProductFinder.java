package com.user.domain.user;

import com.storage.entity.Product;
import com.storage.entity.User;
import com.storage.repository.ProductRepository;
import com.user.utils.error.CommonException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.user.enums.ErrorType.PRODUCT_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class ProductFinder {

    private final ProductRepository productRepository;

    public Product find(User user) {
        return productRepository.findByUser(user)
                .orElseThrow(() -> new CommonException(PRODUCT_NOT_FOUND));
    }
}
