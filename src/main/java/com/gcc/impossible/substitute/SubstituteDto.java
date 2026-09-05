package com.gcc.impossible.substitute;

import com.gcc.impossible.store.StoreDto;
import io.swagger.v3.oas.annotations.media.Schema;

public record SubstituteDto(StoreDto store, @Schema(example = "42") long visits) {
}
