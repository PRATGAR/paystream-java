package com.ledgerline.payments.service;

import java.nio.file.Path;

final class ImportPaths {

    private ImportPaths() {
    }

    static Path inDropDir(Path dropDir, String fileName) {
        return dropDir.resolve(fileName.trim());
    }
}
