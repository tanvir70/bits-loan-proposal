package com.bits.loanproposal.domain.exception;

import com.bits.ddd.shared.exception.domain.FailureException;
import com.bits.ddd.shared.exception.enums.ErrorCode;
import com.bits.ddd.shared.localization.LocalizedMessage;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class LoanProposalValidationException extends FailureException {

    public LoanProposalValidationException(Map<String, LocalizedMessage> errors) {
        super(null, "LoanProposal", ErrorCode.VALIDATION_ERROR, HttpStatus.BAD_REQUEST, false, errors);
    }

    public LoanProposalValidationException(String field, String messageKey) {
        this(Map.of(field, LocalizedMessage.builder().key(messageKey).build()));
    }
}
