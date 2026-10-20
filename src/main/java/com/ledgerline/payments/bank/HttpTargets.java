package com.ledgerline.payments.bank;

import java.net.URI;

public final class HttpTargets {

    private HttpTargets() {
    }

    public static URI toUri(String raw) {
        return URI.create(raw.trim());
    }
}
