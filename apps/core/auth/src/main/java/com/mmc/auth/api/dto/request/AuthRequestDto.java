package com.mmc.auth.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record AuthRequestDto(
        @NotBlank(message = "Login cannot be a null or empty")
        @Length(min = 4, max = 16, message = "Minimum login length 4 and maximum 16 symbols")
        String login,
        @NotBlank(message = "Password cannot be a null or empty")
        @Length(min = 6, max = 16, message = "Minimum length password 6 and maximum 16 symbols")
        String password) {
}
