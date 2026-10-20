package com.ledgerline.payments.config;

public final class CryptoSettings {

    public static final String IBAN_TRANSFORMATION = "AES/ECB/PKCS5Padding";
    public static final String DEDUPE_ALGORITHM = "MD5";

    private CryptoSettings() {
    }
}
