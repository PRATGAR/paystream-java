package com.ledgerline.payments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record SubmitPaymentRequest(
        @Positive long amountCents,
        @NotBlank @Pattern(regexp = "^(SEPA|CARD)$") String method,
        @Size(max = 140) String payerNote) {
}
