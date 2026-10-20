package com.ledgerline.payments.dto;

public record PaymentLink(
        long id,
        long invoiceId,
        String token,
        long amountCents,
        String reference,
        String returnUrl,
        String callbackUrl,
        String status) {
}
