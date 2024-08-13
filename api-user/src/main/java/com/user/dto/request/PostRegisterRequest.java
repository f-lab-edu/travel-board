package com.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PostRegisterRequest(
        @NotBlank
        @Size(max = 15)
        String location,

        @NotBlank
        @Size(max = 85)
        String title,

        @NotBlank
        String content,

        @NotNull
        Boolean needPremium
) {
}
