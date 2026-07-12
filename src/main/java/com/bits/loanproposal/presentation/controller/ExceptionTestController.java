package com.bits.loanproposal.presentation.controller;

import com.bits.ddd.shared.exception.domain.BusinessRuleViolationException;
import com.bits.ddd.shared.exception.domain.FailureException;
import com.bits.ddd.shared.exception.domain.SourceDataValidationException;
import com.bits.ddd.shared.exception.enums.ErrorCode;
import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.loanproposal.domain.exception.LoanProposalValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// ponytail: throwaway controller for manually verifying 1.2.3 exception handling; delete before merge
@RestController
@RequestMapping("/test-exceptions")
public class ExceptionTestController {

    private static final Map<String, LocalizedMessage> ERROR_DETAILS = Map.of(
            "proposedLoanAmount", LocalizedMessage.builder().key("LOAN_AMOUNT_EXCEEDS_LIMIT").build(),
            "memberId", LocalizedMessage.builder().key("MEMBER_NOT_ELIGIBLE").build());

    @GetMapping("/business-rule")
    public String businessRule() {
        throw new BusinessRuleViolationException("REQ-001", "CreateLoanProposalCommand", ERROR_DETAILS);
    }

    @GetMapping("/source-data")
    public String sourceData() {
        throw new SourceDataValidationException("REQ-002", "CreateLoanProposalCommand",
                Map.of("branchId", LocalizedMessage.builder().key("BRANCH_NOT_FOUND").build()));
    }

    @GetMapping("/loan-validation")
    public String loanValidation() {
        throw new LoanProposalValidationException("proposalId", "PROPOSAL_ID_MUST_NOT_BE_NULL");
    }

    @GetMapping("/failure-raw")
    public String failureRaw() {
        throw new FailureException("REQ-003", "DeleteLoanProposalCommand",
                ErrorCode.ENTITY_NOT_FOUND, HttpStatus.NOT_FOUND, false, null);
    }

    @GetMapping("/generic")
    public String generic() {
        throw new IllegalStateException("unexpected boom");
    }
}
