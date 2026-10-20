package com.ledgerline.payments.web;

import com.ledgerline.payments.dto.CreateFilterRequest;
import com.ledgerline.payments.dto.CreatePresetRequest;
import com.ledgerline.payments.dto.ReportRow;
import com.ledgerline.payments.service.ReportService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reports;

    public ReportController(ReportService reports) {
        this.reports = reports;
    }

    @PostMapping("/presets")
    public ResponseEntity<Map<String, Long>> savePreset(@Valid @RequestBody CreatePresetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("presetId", reports.savePreset(request)));
    }

    @GetMapping("/presets/{id}/run")
    public ReportRow runPreset(@PathVariable long id) {
        return reports.runPreset(id);
    }

    @PostMapping("/filters")
    public ResponseEntity<Map<String, Long>> saveFilter(@Valid @RequestBody CreateFilterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("filterId", reports.saveFilter(request)));
    }

    @GetMapping("/filters/{id}/run")
    public ReportRow runFilter(@PathVariable long id) {
        return reports.runFilter(id);
    }
}
