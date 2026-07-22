package com.bits.loanproposal.presentation.controller.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record BulkApproveLoanProposalRequestDto(
        @NotEmpty List<String> loanProposalIds
) {
}
