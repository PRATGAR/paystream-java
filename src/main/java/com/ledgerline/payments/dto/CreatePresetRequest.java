package com.ledgerline.payments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePresetRequest(
        @NotBlank @Size(max = 80) String name,
        @NotBlank @Size(max = 400) String whereClause) {
}
