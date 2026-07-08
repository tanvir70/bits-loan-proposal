package com.bits.loanproposal.application.dto;

public record DeleteLoanProposalMessageDto(
        String tracerId,
        String id,
        Long branchId,
        String deletedBy
) {
}
