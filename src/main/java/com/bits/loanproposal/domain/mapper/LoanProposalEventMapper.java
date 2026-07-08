package com.bits.loanproposal.domain.mapper;

import com.bits.loanproposal.domain.aggregate.LoanProposal;
import com.bits.loanproposal.domain.event.LoanProposalCreatedEvent;
import com.bits.loanproposal.domain.event.LoanProposalDeletedEvent;
import com.bits.loanproposal.domain.event.LoanProposalUpdatedEvent;
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
            loanProposal.getStatus(),
            loanProposal.getIsDigitalDisbursement(),
            loanProposal.getNominees(),
            loanProposal.getFireInsuranceDetails(),
            loanProposal.getModeOfPayment(),
            loanProposal.getApplicationDate(),
            version,
            loanProposal.getTracerId()
        );
    }

    default LoanProposalUpdatedEvent toUpdatedEvent(LoanProposal loanProposal) {
        if (loanProposal == null) {
            return null;
        }
        long version = loanProposal.getVersion() == null ? 0L : loanProposal.getVersion();
        return new LoanProposalUpdatedEvent(
                loanProposal.id(),
                loanProposal.getLoanProposalId(),
                loanProposal.getProposalNumber(),
                loanProposal.getBranchId(),
                loanProposal.getBranchCode(),
                loanProposal.getProjectId(),
                loanProposal.getMemberId(),
                loanProposal.getLoanProductId(),
                loanProposal.getLoanProductDetailsId(),
                loanProposal.getLoanProductPolicyId(),
                loanProposal.getSchemeId(),
                loanProposal.getFrequencyId(),
                loanProposal.getProposedLoanAmount(),
                loanProposal.getApprovedLoanAmount(),
                loanProposal.getProposedGrantAmount(),
                loanProposal.getApprovedGrantAmount(),
                loanProposal.getInstallmentAmount(),
                loanProposal.getProposalDurationInMonths(),
                loanProposal.getLoanProposalType(),
                loanProposal.getLoanProposalStatus(),
                loanProposal.getDataSource(),
                loanProposal.getStatus(),
                loanProposal.getIsDigitalDisbursement(),
                loanProposal.getNominees(),
                loanProposal.getFireInsuranceDetails(),
                loanProposal.getModeOfPayment(),
                loanProposal.getApplicationDate(),
                loanProposal.getProposalReferenceNumber(),
                version,
                loanProposal.getTracerId()
        );
    }

    default LoanProposalDeletedEvent toDeletedEvent(LoanProposal loanProposal) {
        if (loanProposal == null) {
            return null;
        }
        long version = loanProposal.getVersion() == null ? 0L : loanProposal.getVersion();
        return new LoanProposalDeletedEvent(
                loanProposal.id(),
                loanProposal.getBranchId(),
                loanProposal.getMemberId(),
                loanProposal.getProposalNumber(),
                loanProposal.getLoanProposalStatus(),
                loanProposal.getStatus(),
                loanProposal.getDeletedBy(),
                loanProposal.getDeletedAt(),
                version,
                loanProposal.getTracerId()
        );
    }
}
