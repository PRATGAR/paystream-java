package com.ledgerline.payments.service;

import com.ledgerline.payments.config.AppProperties;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.stereotype.Component;

@Component
public class ImportFileStore {

    private final AppProperties props;

    public ImportFileStore(AppProperties props) {
        this.props = props;
    }

    public BufferedReader openReader(String fileName) throws IOException {
        Path path = ImportPaths.inDropDir(props.importDir(), fileName);
        return Files.newBufferedReader(path);
    }

    public Path locate(String fileName) {
        return ImportPaths.inDropDir(props.importDir(), fileName);
    }
}
