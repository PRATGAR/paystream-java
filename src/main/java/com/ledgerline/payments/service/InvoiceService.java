package com.ledgerline.payments.service;

import com.ledgerline.payments.dto.CreateInvoiceRequest;
import com.ledgerline.payments.dto.InvoiceRow;
import com.ledgerline.payments.dto.InvoiceSearchResult;
import com.ledgerline.payments.dto.InvoiceStatus;
import com.ledgerline.payments.repo.AuditRepository;
import com.ledgerline.payments.repo.InvoiceRepository;
import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvoiceService {

    private final InvoiceRepository invoices;
    private final AuditRepository audit;

    public InvoiceService(InvoiceRepository invoices, AuditRepository audit) {
        this.invoices = invoices;
        this.audit = audit;
    }

    @Transactional
    public InvoiceRow create(CreateInvoiceRequest request) {
        long total = request.lines().stream()
                .mapToLong(line -> line.quantity() * line.unitCents())
                .sum();
        String number = invoices.nextNumber(request.series());
        long id = invoices.insert(number, request.customerId(), total, request.currency(), request.dueDate());
        invoices.insertLines(id, request.lines());
        return invoices.findByNumber(number);
    }

    public InvoiceSearchResult search(String customer, String status) {
        InvoiceStatus parsed = InvoiceStatus.parse(status);
        List<InvoiceRow> matches = invoices.findByCustomerName(customer);
        int statusCount = invoices.countByStatus(parsed.name());
        return new InvoiceSearchResult(matches, parsed.name(), statusCount);
    }

    public InvoiceRow get(long id) {
        try {
            return invoices.findById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("invoice " + id + " not found");
        }
    }

    public List<InvoiceRow> recent(int limit) {
        return invoices.findRecent(limit);
    }

    @Transactional
    public void addNote(long invoiceId, String note) {
        invoices.insertNote(invoiceId, note);
        audit.append(AuditEvents.NOTE_ADDED, note);
    }

    @Transactional
    public void cancel(long invoiceId, String reason) {
        invoices.markCancelled(invoiceId, reason);
        audit.append(AuditEvents.INVOICE_CANCELLED, AuditEvents.CANCEL_DETAIL);
    }
}
