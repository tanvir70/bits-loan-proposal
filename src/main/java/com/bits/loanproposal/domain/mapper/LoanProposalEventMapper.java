package com.bits.loanproposal.domain.mapper;

import com.bits.loanproposal.domain.aggregate.LoanProposal;
import com.bits.loanproposal.domain.event.LoanProposalCreatedEvent;

public final class LoanProposalEventMapper {
    private LoanProposalEventMapper() {}

    public static LoanProposalCreatedEvent toCreatedEvent(LoanProposal loanProposal) {
        long version = loanProposal.getVersion() == null ? 0L : loanProposal.getVersion();
        return new LoanProposalCreatedEvent(
            loanProposal.id(),
            loanProposal.getApplicantName(),
            loanProposal.getAmount(),
            version,
            loanProposal.getTracerId()
        );
    }
}
