package com.ledgerline.payments.dto;

public record PaymentLinkView(String token, String payUrl, long amountCents, String reference) {
}
