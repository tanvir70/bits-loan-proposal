package com.bits.loanproposal.application.mapper;

import com.bits.loanproposal.application.command.CreateLoanProposalCommand;
import com.bits.loanproposal.presentation.controller.dto.CreateLoanProposalRequestDto;

public final class LoanProposalCommandMapper {
    private LoanProposalCommandMapper() {}

    public static CreateLoanProposalCommand toCreateCommand(String tracerId, CreateLoanProposalRequestDto request) {
        return new CreateLoanProposalCommand(
            tracerId,
            request.applicantName(),
            request.amount()
        );
    }
}
