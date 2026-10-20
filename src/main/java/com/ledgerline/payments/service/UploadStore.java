package com.ledgerline.payments.service;

import com.ledgerline.payments.config.AppProperties;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class UploadStore {

    private final AppProperties props;

    public UploadStore(AppProperties props) {
        this.props = props;
    }

    public String save(MultipartFile file) throws IOException {
        String storedName = StoredNames.from(file.getOriginalFilename());
        Files.createDirectories(props.uploadDir());
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, props.uploadDir().resolve(storedName), StandardCopyOption.REPLACE_EXISTING);
        }
        return storedName;
    }

    public Path locate(String storedName) {
        return props.uploadDir().resolve(storedName);
    }

    public List<String> head(String storedName, int maxLines) throws IOException {
        return Files.readAllLines(locate(storedName)).stream().limit(maxLines).toList();
    }
}
