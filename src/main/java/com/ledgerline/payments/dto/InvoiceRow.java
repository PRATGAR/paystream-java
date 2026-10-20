package com.ledgerline.payments.dto;

import java.time.LocalDate;

public record InvoiceRow(
        long id,
        String number,
        long customerId,
        String status,
        long totalCents,
        long paidCents,
        String currency,
        LocalDate dueDate) {
}
