package com.ledgerline.payments.service;

public final class AuditEvents {

    public static final String NOTE_ADDED = "invoice.note";
    public static final String INVOICE_CANCELLED = "invoice.cancelled";
    public static final String PAYMENT_RECEIVED = "payment.received";

    public static final String CANCEL_DETAIL = "cancelled by operator";

    private AuditEvents() {
    }
}
