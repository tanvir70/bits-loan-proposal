package com.bits.loanproposal.domain.exception;

import com.bits.ddd.shared.exception.domain.BusinessRuleViolationException;
import com.bits.ddd.shared.localization.LocalizedMessage;
import java.util.Map;
import lombok.Getter;

@Getter
public class LoanProposalValidationException extends BusinessRuleViolationException {

    private static final String REQUEST_TYPE = "LoanProposal";

    private final transient Map<String, LocalizedMessage> errors;

    public LoanProposalValidationException(Map<String, LocalizedMessage> errors) {
        super(null, REQUEST_TYPE, errors);
        this.errors = errors;
    }

    public LoanProposalValidationException(String field, String messageKey) {
        this(Map.of(field, LocalizedMessage.builder().key(messageKey).build()));
    }
}
