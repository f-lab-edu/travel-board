package com.user.controller.request;

import com.storage.entity.Account;
import com.storage.entity.User;
import com.support.utils.StringUtils;
import com.user.service.factory.UserFactory;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record UserRegisterRequest(
        @NotBlank
        @Email
        String email,

        @NotBlank
        @Size(min = 6, max = 20)
        @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*]*$",
                message = "Password must contain alphabets, numbers, and special characters")
        String password,

        @NotBlank
        @Size(min = 3, max = 15)
        @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Nickname must contain alphabets and numbers")
        String nickname,

        @URL
        @Size(max = 512)
        String profileImageUrl,

        @Size(max = 100)
        String bio
) {

    public User toUser(Account account) {
        return UserFactory.create(
                account,
                nickname,
                StringUtils.checkIfNull(profileImageUrl),
                StringUtils.checkIfNull(bio)
        );
    }
}
