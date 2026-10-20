package com.ledgerline.payments.service;

public final class Redirects {

    private static final String FALLBACK = "/pay/cancelled";

    private Redirects() {
    }

    public static String local(String target) {
        if (target == null || target.isBlank()) {
            return FALLBACK;
        }
        if (!target.startsWith("/")) {
            return FALLBACK;
        }
        return target;
    }
}
