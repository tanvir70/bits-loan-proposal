package com.bits.loanproposal.domain.constant;

public final class DomainErrorConstant {

    public static final String ALREADY_EXISTS = "ALREADY_EXISTS";
    public static final String LOAN_PROPOSAL_ALREADY_EXISTS = "Buffer Loan Proposal already exists with given id.";
    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String LOAN_PROPOSAL_NOT_FOUND = "Buffer Loan Proposal not found";
    public static final String UPDATE_FAILED = "UPDATE_FAILED";
    public static final String LOAN_PROPOSAL_UPDATE_FAILED = "Approved loan proposal cannot be modified";
    public static final String DELETE_FAILED = "DELETE_FAILED";
    public static final String LOAN_PROPOSAL_DELETE_FAILED = "Only pending loan proposal can be deleted. Loan proposal Status: {status}";
    public static final String ID_NULL = "ID_NULL";
    public static final String PROPOSAL_ID_MUST_NOT_BE_NULL = "The given id must not be null!";

    private DomainErrorConstant() {
    }
}
