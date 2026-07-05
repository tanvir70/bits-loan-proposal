package com.bits.loanproposal.domain.constant;

public final class DomainErrorConstant {

    public static final String ALREADY_EXISTS = "ALREADY_EXISTS";
    public static final String LOAN_PROPOSAL_ALREADY_EXISTS = "Buffer Loan Proposal already exists with given id.";
    public static final String ID_NULL = "ID_NULL";
    public static final String PROPOSAL_ID_MUST_NOT_BE_NULL = "The given id must not be null!";

    private DomainErrorConstant() {
    }
}
