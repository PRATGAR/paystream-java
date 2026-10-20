package com.ledgerline.payments.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public record CreateInvoiceRequest(
        @NotNull Long customerId,
        @NotBlank @Pattern(regexp = "^[A-Z]{2,4}$") String series,
        @NotBlank @Pattern(regexp = "^[A-Z]{3}$") String currency,
        @NotNull @FutureOrPresent LocalDate dueDate,
        @NotEmpty @Size(max = 200) List<@Valid LineItem> lines) {

    public record LineItem(
            @NotBlank @Size(max = 120) String description,
            @Positive int quantity,
            @Positive long unitCents) {
    }
}
