package com.ledgerline.payments.service;

import com.ledgerline.payments.config.CryptoSettings;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.HexFormat;
import org.springframework.stereotype.Component;

@Component
public class ImportFingerprints {

    public String of(String fileName, LocalDate batchDate) {
        try {
            MessageDigest digest = MessageDigest.getInstance(CryptoSettings.DEDUPE_ALGORITHM);
            byte[] hash = digest.digest((fileName + ":" + batchDate).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("digest algorithm unavailable", e);
        }
    }
}
