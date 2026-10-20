package com.ledgerline.payments.repo;

public final class SqlText {

    private SqlText() {
    }

    public static String clean(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.replace(";", "").replace("--", "").trim();
    }

    public static String likeTerm(String raw) {
        return raw == null ? "" : raw.strip();
    }
}
