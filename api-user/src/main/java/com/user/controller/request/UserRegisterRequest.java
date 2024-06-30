package com.user.controller.request;

import com.support.utils.StringUtils;
import com.user.service.request.UserRegisterServiceRequest;
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

    public UserRegisterServiceRequest toServiceRequest() {
        return UserRegisterServiceRequest.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .profileImageUrl(StringUtils.checkIfNull(profileImageUrl))
                .bio(StringUtils.checkIfNull(bio))
                .build();
    }

}
