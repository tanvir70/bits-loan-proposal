package com.bits.loanproposal.domain.exception;

import com.bits.ddd.shared.exception.domain.DomainValidationException;
import com.bits.ddd.shared.localization.LocalizedMessage;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class LoanProposalValidationException extends DomainValidationException {

    private final transient Map<String, LocalizedMessage> errors;

    public LoanProposalValidationException(Map<String, LocalizedMessage> errors) {
        super("LOAN_PROPOSAL_VALIDATION_FAILED", summarize(errors));
        this.errors = errors;
    }

    private static String summarize(Map<String, LocalizedMessage> errors) {
        return errors.entrySet().stream()
                .map(e -> e.getKey() + ": " + e.getValue().getKey())
                .collect(Collectors.joining("; "));
    }
}
