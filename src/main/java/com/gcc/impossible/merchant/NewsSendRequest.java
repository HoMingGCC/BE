package com.gcc.impossible.merchant;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record NewsSendRequest(
        @NotBlank @Schema(example = "이번 주말 단골 손님 대상 20% 할인 쿠폰 드립니다!") String message) {
}
