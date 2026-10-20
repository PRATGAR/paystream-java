package com.ledgerline.payments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateCreditNoteRequest(
        @NotNull Long invoiceId,
        @Positive long amountCents,
        @NotBlank @Size(max = 200) String reason,
        @NotNull CreditCategory category,
        @NotBlank @Size(max = 60) String customerRef) {
}
