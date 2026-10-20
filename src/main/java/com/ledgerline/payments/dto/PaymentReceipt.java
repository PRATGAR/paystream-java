package com.ledgerline.payments.dto;

public record PaymentReceipt(long paymentId, String status, long amountCents, String returnUrl) {
}
