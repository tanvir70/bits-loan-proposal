package com.bits.loanproposal.domain.param;

public record LoanProposalCreationData(
    String tracerId,
    String applicantName,
    double amount
) {}
