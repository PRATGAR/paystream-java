package com.ledgerline.payments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CreateFilterRequest(
        @NotBlank @Size(max = 80) String name,
        @NotNull InvoiceStatus status,
        @PositiveOrZero Long minTotalCents,
        @Pattern(regexp = "^[A-Z]{3}$") String currency) {
}
