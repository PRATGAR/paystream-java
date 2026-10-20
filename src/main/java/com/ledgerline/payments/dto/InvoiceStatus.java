package com.ledgerline.payments.dto;

import java.util.Locale;

public enum InvoiceStatus {
    DRAFT,
    OPEN,
    PARTIAL,
    PAID,
    CANCELLED;

    public static InvoiceStatus parse(String raw) {
        if (raw == null) {
            return OPEN;
        }
        try {
            return valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return OPEN;
        }
    }
}
