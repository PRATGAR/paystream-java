package com.ledgerline.payments.web;

import com.ledgerline.payments.dto.AddNoteRequest;
import com.ledgerline.payments.dto.CancelInvoiceRequest;
import com.ledgerline.payments.dto.CreateInvoiceRequest;
import com.ledgerline.payments.dto.InvoiceRow;
import com.ledgerline.payments.dto.InvoiceSearchResult;
import com.ledgerline.payments.dto.PageParams;
import com.ledgerline.payments.service.InvoiceService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoices;

    public InvoiceController(InvoiceService invoices) {
        this.invoices = invoices;
    }

    @PostMapping
    public ResponseEntity<InvoiceRow> createInvoice(@Valid @RequestBody CreateInvoiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(invoices.create(request));
    }

    @GetMapping
    public InvoiceSearchResult searchInvoices(
            @RequestParam(defaultValue = "") String customer,
            @RequestParam(defaultValue = "OPEN") String status) {
        return invoices.search(customer, status);
    }

    @GetMapping("/{id}")
    public InvoiceRow getInvoice(@PathVariable long id) {
        return invoices.get(id);
    }

    @GetMapping("/recent")
    public List<InvoiceRow> recentInvoices(@Valid PageParams params) {
        return invoices.recent(params.limitOrDefault());
    }

    @PostMapping("/{id}/notes")
    public ResponseEntity<Void> addNote(@PathVariable long id, @Valid @RequestBody AddNoteRequest request) {
        invoices.addNote(id, request.note());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelInvoice(@PathVariable long id,
                                              @Valid @RequestBody CancelInvoiceRequest request) {
        invoices.cancel(id, request.reason());
        return ResponseEntity.noContent().build();
    }
}
