package com.ledgerline.payments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreatePaymentLinkRequest(
        @NotNull Long invoiceId,
        @Positive long amountCents,
        @NotBlank @Pattern(regexp = "^[A-Z0-9-]{6,32}$") String reference,
        @TrustedReturnUrl @Size(max = 300) String returnUrl,
        @Size(max = 300) String callbackUrl) {
}
