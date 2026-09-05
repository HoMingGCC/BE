package com.gcc.impossible.merchant;

import jakarta.validation.constraints.NotBlank;

public record NewsSendRequest(@NotBlank String message) {
}
