package com.bits.loanproposal.domain.exception;

import com.bits.ddd.shared.exception.domain.DomainValidationException;
import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.loanproposal.domain.event.LoanProposalFailedEvent;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;

// DDD-REQ-032: domain-specific validation exception carrying the LoanProposalFailedEvent.
// Extends the library's DomainValidationException so its GlobalExceptionHandler maps this
// to HTTP 400 without any extra advice.
@Getter
public class LoanProposalValidationException extends DomainValidationException {

    private final transient LoanProposalFailedEvent failedEvent;
    private final transient Map<String, LocalizedMessage> errors;

    public LoanProposalValidationException(LoanProposalFailedEvent failedEvent,
                                           Map<String, LocalizedMessage> errors) {
        super("LOAN_PROPOSAL_VALIDATION_FAILED", summarize(errors));
        this.failedEvent = failedEvent;
        this.errors = errors;
    }

    private static String summarize(Map<String, LocalizedMessage> errors) {
        return errors.entrySet().stream()
                .map(e -> e.getKey() + ": " + e.getValue().getKey())
                .collect(Collectors.joining("; "));
    }
}
