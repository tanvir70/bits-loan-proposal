package com.bits.loanproposal.domain.aggregate;

import com.bits.ddd.shared.domain.value.DomainStatus;
import com.bits.ddd.shared.exception.domain.DomainValidationException;
import com.bits.loanproposal.application.dto.LoanProposalSourceData;
import com.bits.loanproposal.application.dto.sourcedata.LoanProductPolicy;
import com.bits.loanproposal.application.dto.sourcedata.Member;
import com.bits.loanproposal.domain.enums.LoanProposalStatus;
import com.bits.loanproposal.domain.enums.LoanProposalType;
import com.bits.loanproposal.domain.event.LoanProposalUpdatedEvent;
import com.bits.loanproposal.domain.param.LoanProposalUpdateData;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoanProposalUpdateTest {

    private LoanProposal proposal() {
        LoanProposal proposal = new LoanProposal();
        proposal.setId("lp-1");
        proposal.setLoanProposalStatus(LoanProposalStatus.PENDING);
        proposal.setDomainStatus(DomainStatus.CREATED);
        proposal.setProposedLoanAmount(new BigDecimal("1000"));
        proposal.setApprovedLoanAmount(new BigDecimal("1000"));
        proposal.setInstallmentAmount(new BigDecimal("100"));
        proposal.setApprovedInstallmentAmount(new BigDecimal("100"));
        proposal.setLoanProposalType(LoanProposalType.NORMAL_LOAN);
        proposal.setProposalNumber("202607-00001");
        return proposal;
    }

    private LoanProposalSourceData sourceData() {
        Member member = Member.builder()
                .memberId(1L)
                .memberClassificationId(2L)
                .status("ACTIVE")
                .isScreened(false)
                .nationalId("1234567890")
                .build();

        LoanProductPolicy policy = LoanProductPolicy.builder()
                .minAmount(new BigDecimal("500"))
                .maxAmount(new BigDecimal("5000"))
                .build();

        return LoanProposalSourceData.builder()
                .member(member)
                .loanProductPolicy(policy)
                .build();
    }

    private LoanProposalUpdateData updateData(LoanProposal proposal, BigDecimal proposedLoanAmount, BigDecimal installmentAmount) {
        return new LoanProposalUpdateData(
                "trace-1",
                proposal.getId(),
                sourceData(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                proposedLoanAmount,
                null,
                null,
                null,
                null,
                null,
                installmentAmount,
                installmentAmount,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    @Test
    void updateRejectedWhenProposalNotPending() {
        LoanProposal proposal = proposal();
        proposal.setLoanProposalStatus(LoanProposalStatus.APPROVED);

        assertThrows(DomainValidationException.class,
                () -> proposal.update(updateData(proposal, new BigDecimal("2000"), new BigDecimal("200"))));
    }

    @Test
    void updateMergesStateAndMarksDomainStatusUpdated() {
        LoanProposal proposal = proposal();

        proposal.update(updateData(proposal, new BigDecimal("2000"), new BigDecimal("200")));

        assertEquals(new BigDecimal("2000"), proposal.getProposedLoanAmount());
        assertEquals(new BigDecimal("2000"), proposal.getApprovedLoanAmount());
        assertEquals(new BigDecimal("200"), proposal.getInstallmentAmount());
        assertEquals(DomainStatus.UPDATED, proposal.getDomainStatus());
        assertEquals(LoanProposalStatus.PENDING, proposal.getLoanProposalStatus());
        assertEquals(1, proposal.getEvents().size());
        assertInstanceOf(LoanProposalUpdatedEvent.class, proposal.getEvents().get(0));
    }
}
