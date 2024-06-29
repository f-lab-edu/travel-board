package com.user.controller.request;

import com.support.utils.StringUtils;
import com.user.exception.InvalidRequestException;
import com.user.service.request.UserRegisterServiceRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record UserRegisterRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6, max = 20) String password,
        @NotBlank @Size(min = 3, max = 15) String nickname,
        @URL @Size(max = 512) String profileImageUrl,
        @Size(max = 100) String bio
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

    public void validate() {
        validatePassword(password);
        validateNickname(nickname);
    }

    private void validatePassword(String password) {
        if (StringUtils.isNotAlphabetAndNumberAndSpecialCharacter(password)) {
            throw new InvalidRequestException("password", "Password must be alphabet, number, and special character");
        }
    }

    private void validateNickname(String nickname) {
        if (StringUtils.isNotAlphabetAndNumber(nickname)) {
            throw new InvalidRequestException("nickname", "Nickname must be alphabet and number");
        }
    }
}
