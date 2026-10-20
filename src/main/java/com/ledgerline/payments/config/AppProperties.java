package com.ledgerline.payments.config;

import java.nio.file.Path;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ledgerline")
public record AppProperties(
        Path importDir,
        Path uploadDir,
        Path exportDir,
        String bankBaseUrl,
        String vaultKey,
        List<String> approvedHosts,
        List<String> trustedReturnHosts) {
}
