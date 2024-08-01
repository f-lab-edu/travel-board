package com.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AccessTokenReissueRequest(@NotBlank String refreshToken) {
}
