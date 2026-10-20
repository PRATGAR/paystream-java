package com.ledgerline.payments.service;

import com.ledgerline.payments.dto.CreateCreditNoteRequest;
import com.ledgerline.payments.dto.CreditNoteView;
import com.ledgerline.payments.repo.CreditNoteRepository;
import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreditNoteService {

    private final CreditNoteRepository creditNotes;

    public CreditNoteService(CreditNoteRepository creditNotes) {
        this.creditNotes = creditNotes;
    }

    @Transactional
    public CreditNoteView create(CreateCreditNoteRequest request) {
        int existing = creditNotes.countForCustomerRef(request.customerRef());
        String number = "CN-" + request.invoiceId() + "-" + (existing + 1);
        long id = creditNotes.insert(number, request.invoiceId(), request.amountCents(),
                request.reason(), request.category().name(), request.customerRef());
        return new CreditNoteView(id, number, request.invoiceId(), request.amountCents(),
                request.category().name());
    }

    public List<CreditNoteView> related(long creditNoteId) {
        try {
            String reason = creditNotes.findReason(creditNoteId);
            return creditNotes.findRelated(creditNoteId, reason);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("credit note " + creditNoteId + " not found");
        }
    }

    public List<CreditNoteView> peers(long creditNoteId) {
        try {
            String category = creditNotes.findCategory(creditNoteId);
            return creditNotes.findPeers(creditNoteId, category);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("credit note " + creditNoteId + " not found");
        }
    }
}
