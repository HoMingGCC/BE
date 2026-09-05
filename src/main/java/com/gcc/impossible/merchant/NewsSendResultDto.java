package com.gcc.impossible.merchant;

import io.swagger.v3.oas.annotations.media.Schema;

public record NewsSendResultDto(
        @Schema(example = "2") int weeklySlotUsed,
        @Schema(example = "2") int weeklySlotTotal) {
}
