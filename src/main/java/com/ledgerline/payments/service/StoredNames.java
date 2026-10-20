package com.ledgerline.payments.service;

import java.util.UUID;
import org.apache.commons.io.FilenameUtils;

public final class StoredNames {

    private StoredNames() {
    }

    public static String from(String original) {
        String base = original == null ? "" : FilenameUtils.getName(original);
        if (base.isBlank() || base.equals(".") || base.equals("..")) {
            return UUID.randomUUID() + ".csv";
        }
        return base;
    }
}
