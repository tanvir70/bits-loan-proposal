package com.bits.loanproposal.domain.param;

import java.time.LocalDateTime;

public record LoanProposalDeletionData(
        String traceId,
        String id,
        Long branchId,
        String deletedBy,
        LocalDateTime deletedAt
) {
}
