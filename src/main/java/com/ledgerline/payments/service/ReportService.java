package com.ledgerline.payments.service;

import com.ledgerline.payments.dto.CreateFilterRequest;
import com.ledgerline.payments.dto.CreatePresetRequest;
import com.ledgerline.payments.dto.ReportRow;
import com.ledgerline.payments.repo.ReportRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    private final ReportRepository reports;

    public ReportService(ReportRepository reports) {
        this.reports = reports;
    }

    public long savePreset(CreatePresetRequest request) {
        return reports.insertPreset(request.name(), request.whereClause());
    }

    public ReportRow runPreset(long presetId) {
        String whereClause;
        try {
            whereClause = reports.findPresetWhere(presetId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("preset " + presetId + " not found");
        }
        return ReportRow.of(reports.runPresetReport(whereClause));
    }

    public long saveFilter(CreateFilterRequest request) {
        StringBuilder where = new StringBuilder("status = '").append(request.status().name()).append("'");
        if (request.minTotalCents() != null) {
            where.append(" AND total_cents >= ").append(request.minTotalCents());
        }
        if (request.currency() != null) {
            where.append(" AND currency = '").append(request.currency()).append("'");
        }
        return reports.insertFilter(request.name(), where.toString());
    }

    public ReportRow runFilter(long filterId) {
        String whereClause;
        try {
            whereClause = reports.findFilterWhere(filterId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("filter " + filterId + " not found");
        }
        return ReportRow.of(reports.runFilterReport(whereClause));
    }
}
