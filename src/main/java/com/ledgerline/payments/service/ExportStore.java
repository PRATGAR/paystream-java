package com.ledgerline.payments.service;

import com.ledgerline.payments.config.AppProperties;
import com.ledgerline.payments.dto.StatementLine;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class ExportStore {

    private final AppProperties props;

    public ExportStore(AppProperties props) {
        this.props = props;
    }

    public String writeSummary(long importId, List<StatementLine> lines) throws IOException {
        Files.createDirectories(props.exportDir());
        String name = "import-" + importId + "-summary.csv";
        StringBuilder csv = new StringBuilder("reference,amount_cents,description\n");
        for (StatementLine line : lines) {
            csv.append(line.reference()).append(',')
                    .append(line.amountCents()).append(',')
                    .append(line.description()).append('\n');
        }
        Files.writeString(props.exportDir().resolve(name), csv.toString());
        return name;
    }

    public Resource resource(String name) {
        Path baseDir = props.exportDir().toAbsolutePath().normalize();
        Path resolved = baseDir.resolve(FileNames.stripTraversal(name)).normalize();
        if (!resolved.startsWith(baseDir)) {
            throw new IllegalArgumentException("Invalid export name: " + name);
        }
        return new FileSystemResource(resolved);
    }
}
