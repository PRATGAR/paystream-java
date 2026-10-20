package com.ledgerline.payments.service;

import com.ledgerline.payments.dto.ImportSummary;
import com.ledgerline.payments.dto.RegisterImportRequest;
import com.ledgerline.payments.dto.StatementLine;
import com.ledgerline.payments.repo.ReconciliationRepository;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import org.springframework.core.io.Resource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ReconciliationService {

    private final ReconciliationRepository recon;
    private final ImportFileStore importFiles;
    private final UploadStore uploads;
    private final ExportStore exports;
    private final ImportFingerprints fingerprints;
    private final CamtParser camt;

    public ReconciliationService(ReconciliationRepository recon, ImportFileStore importFiles,
                                 UploadStore uploads, ExportStore exports,
                                 ImportFingerprints fingerprints, CamtParser camt) {
        this.recon = recon;
        this.importFiles = importFiles;
        this.uploads = uploads;
        this.exports = exports;
        this.fingerprints = fingerprints;
        this.camt = camt;
    }

    @Transactional
    public long registerImport(RegisterImportRequest request) {
        String fingerprint = fingerprints.of(request.fileName(), request.batchDate());
        if (recon.existsFingerprint(fingerprint)) {
            throw new IllegalStateException("this statement file was already registered for the batch");
        }
        return recon.insertImport(request.fileName(), fingerprint, request.batchDate());
    }

    @Transactional
    public ImportSummary process(long importId) throws IOException {
        String fileName = lookupImportFile(importId);
        List<StatementLine> lines;
        if (fileName.toLowerCase(Locale.ROOT).endsWith(".xml")) {
            lines = camt.parse(importFiles.locate(fileName), XmlFactories.bankFeed());
        } else {
            lines = readCsv(fileName);
        }
        recon.insertLines(importId, lines);
        recon.markProcessed(importId);
        String summaryFile = exports.writeSummary(importId, lines);
        long total = lines.stream().mapToLong(StatementLine::amountCents).sum();
        return new ImportSummary(importId, lines.size(), total, summaryFile);
    }

    @Transactional
    public long storeUpload(MultipartFile file) throws IOException {
        String storedName = uploads.save(file);
        return recon.insertUpload(file.getOriginalFilename(), storedName);
    }

    public List<String> preview(long uploadId) throws IOException {
        String storedName;
        try {
            storedName = recon.findStoredName(uploadId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("upload " + uploadId + " not found");
        }
        if (storedName.toLowerCase(Locale.ROOT).endsWith(".xml")) {
            return camt.parse(uploads.locate(storedName), XmlFactories.preview()).stream()
                    .map(line -> line.reference() + " " + line.amountCents())
                    .toList();
        }
        return uploads.head(storedName, 20);
    }

    public Resource openExport(String name) {
        return exports.resource(name);
    }

    private String lookupImportFile(long importId) {
        try {
            return recon.findImportFileName(importId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("import " + importId + " not found");
        }
    }

    private List<StatementLine> readCsv(String fileName) throws IOException {
        try (BufferedReader reader = importFiles.openReader(fileName)) {
            return reader.lines()
                    .skip(1)
                    .map(ReconciliationService::parseCsvLine)
                    .toList();
        }
    }

    private static StatementLine parseCsvLine(String raw) {
        String[] parts = raw.split(",", 3);
        String reference = parts.length > 0 ? parts[0].trim() : "";
        long cents = parts.length > 1 ? Long.parseLong(parts[1].trim()) : 0L;
        String description = parts.length > 2 ? parts[2].trim() : "";
        return new StatementLine(reference, cents, description);
    }
}
