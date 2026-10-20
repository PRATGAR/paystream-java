package com.ledgerline.payments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CancelInvoiceRequest(@NotBlank @Size(max = 200) String reason) {
}
