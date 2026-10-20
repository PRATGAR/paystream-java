package com.ledgerline.payments.web;

import com.ledgerline.payments.dto.ImportSummary;
import com.ledgerline.payments.dto.RegisterImportRequest;
import com.ledgerline.payments.service.ReconciliationService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/reconciliation")
public class ReconciliationController {

    private final ReconciliationService reconciliation;

    public ReconciliationController(ReconciliationService reconciliation) {
        this.reconciliation = reconciliation;
    }

    @PostMapping("/imports")
    public ResponseEntity<Map<String, Long>> registerImport(@Valid @RequestBody RegisterImportRequest request) {
        long id = reconciliation.registerImport(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("importId", id));
    }

    @PostMapping("/imports/{id}/process")
    public ImportSummary processImport(@PathVariable long id) throws IOException {
        return reconciliation.process(id);
    }

    @PostMapping("/uploads")
    public ResponseEntity<Map<String, Long>> uploadStatement(@RequestParam("file") MultipartFile file)
            throws IOException {
        long id = reconciliation.storeUpload(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("uploadId", id));
    }

    @GetMapping("/uploads/{id}/preview")
    public List<String> previewUpload(@PathVariable long id) throws IOException {
        return reconciliation.preview(id);
    }

    @GetMapping("/exports")
    public ResponseEntity<Resource> downloadExport(@RequestParam String name) {
        Resource export = reconciliation.openExport(name);
        if (!export.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment")
                .body(export);
    }
}
