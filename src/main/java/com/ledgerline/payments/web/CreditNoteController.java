package com.ledgerline.payments.web;

import com.ledgerline.payments.dto.CreateCreditNoteRequest;
import com.ledgerline.payments.dto.CreditNoteView;
import com.ledgerline.payments.service.CreditNoteService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/credit-notes")
public class CreditNoteController {

    private final CreditNoteService creditNotes;

    public CreditNoteController(CreditNoteService creditNotes) {
        this.creditNotes = creditNotes;
    }

    @PostMapping
    public ResponseEntity<CreditNoteView> createCreditNote(@Valid @RequestBody CreateCreditNoteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(creditNotes.create(request));
    }

    @GetMapping("/{id}/related")
    public List<CreditNoteView> relatedCreditNotes(@PathVariable long id) {
        return creditNotes.related(id);
    }

    @GetMapping("/{id}/peers")
    public List<CreditNoteView> peerCreditNotes(@PathVariable long id) {
        return creditNotes.peers(id);
    }
}
