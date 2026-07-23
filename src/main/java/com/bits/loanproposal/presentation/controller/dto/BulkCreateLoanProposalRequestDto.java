package com.bits.loanproposal.presentation.controller.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record BulkCreateLoanProposalRequestDto(
        @NotEmpty(message = "Loan proposals are required.")
        List<@Valid CreateLoanProposalRequestDto> loanProposals
) {
}
