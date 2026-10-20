package com.ledgerline.payments.repo;

final class NativeQueries {

    private static final String CREDIT_COLUMNS = "id, number, invoice_id, amount_cents, category";

    private NativeQueries() {
    }

    static String creditNotesByReason(long excludeId, String reason) {
        return "SELECT " + CREDIT_COLUMNS + " FROM credit_notes WHERE reason = '" + reason
                + "' AND id <> " + excludeId + " ORDER BY created_at DESC";
    }
}
