package com.bits.loanproposal.domain.mapper;

import com.bits.loanproposal.domain.aggregate.LoanProposal;
import com.bits.loanproposal.domain.event.LoanProposalCreatedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface LoanProposalEventMapper {
    LoanProposalEventMapper INSTANCE = Mappers.getMapper(LoanProposalEventMapper.class);

    default LoanProposalCreatedEvent toCreatedEvent(LoanProposal loanProposal) {
        if (loanProposal == null) {
            return null;
        }
        long version = loanProposal.getVersion() == null ? 0L : loanProposal.getVersion();
        return new LoanProposalCreatedEvent(
            loanProposal.id(),
            loanProposal.getLoanProposalId(),
            loanProposal.getProposalNumber(),
            loanProposal.getBranchId(),
            loanProposal.getBranchCode(),
            loanProposal.getProjectId(),
            loanProposal.getMemberId(),
            loanProposal.getLoanProductId(),
            loanProposal.getProposedLoanAmount(),
            loanProposal.getApprovedLoanAmount(),
            loanProposal.getLoanProposalStatus(),
            loanProposal.getDataSource(),
            loanProposal.getDomainStatus(),
            loanProposal.getIsDigitalDisbursement(),
            loanProposal.getNominees(),
            loanProposal.getFireInsuranceDetails(),
            loanProposal.getModeOfPayment(),
            loanProposal.getApplicationDate(),
            version,
            loanProposal.getTracerId()
        );
    }
}
