package com.ledgerline.payments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreatePayoutAccountRequest(
        @NotBlank @Size(max = 100) String holderName,
        @NotBlank @Pattern(regexp = "^[A-Z]{2}[0-9]{2}[A-Z0-9]{11,30}$") String iban,
        @NotBlank @Pattern(regexp = "^[A-Z]{6}[A-Z0-9]{2,5}$") String bankCode,
        @NotBlank @Size(max = 300) String verificationUrl,
        @NotBlank @ApprovedHost @Size(max = 300) String notifyUrl) {
}
