package com.ledgerline.payments.service;

import java.nio.file.Path;

public final class FileNames {

    private FileNames() {
    }

    public static String stripTraversal(String name) {
        if (name == null || name.isBlank()) {
            return "";
        }
        Path path = Path.of(name).getFileName();
        return path == null ? "" : path.toString();
    }
}
