package com.bits.loanproposal.domain.aggregate;

import com.bits.ddd.shared.domain.value.DomainStatus;
import com.bits.ddd.shared.exception.domain.DomainValidationException;
import com.bits.loanproposal.application.dto.LoanProposalSourceData;
import com.bits.loanproposal.application.dto.sourcedata.LoanProduct;
import com.bits.loanproposal.application.dto.sourcedata.LoanProductDetails;
import com.bits.loanproposal.application.dto.sourcedata.LoanProductPolicy;
import com.bits.loanproposal.application.dto.sourcedata.Member;
import com.bits.loanproposal.application.dto.sourcedata.Project;
import com.bits.loanproposal.application.dto.sourcedata.ProjectPolicy;
import com.bits.loanproposal.application.dto.sourcedata.Scheme;
import com.bits.loanproposal.domain.enums.LoanProposalStatus;
import com.bits.loanproposal.domain.enums.LoanProposalType;
import com.bits.loanproposal.domain.event.LoanProposalUpdatedEvent;
import com.bits.loanproposal.domain.param.LoanProposalUpdateData;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoanProposalUpdateTest {

    private LoanProposal proposal() {
        LoanProposal proposal = new LoanProposal();
        proposal.setId("lp-1");
        proposal.setLoanProposalStatus(LoanProposalStatus.PENDING);
        proposal.setProposedLoanAmount(new BigDecimal("1000"));
        proposal.setApprovedLoanAmount(new BigDecimal("1000"));
        proposal.setInstallmentAmount(new BigDecimal("100"));
        proposal.setApprovedInstallmentAmount(new BigDecimal("100"));
        proposal.setLoanProposalType(LoanProposalType.NORMAL_LOAN);
        proposal.setProposalNumber("202607-00001");
        // full 21-spec chain runs on update: frequency must be 1..10 and the product/details
        // active-on checks need a non-null business date
        proposal.setFrequencyId(2L);
        proposal.setApplicationDate(LocalDate.now());
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

        // minimal stubs so the full 21-spec chain passes: empty mappings are lenient,
        // null active periods pass isActiveOn for a non-null business date
        return LoanProposalSourceData.builder()
                .member(member)
                .loanProductPolicy(policy)
                .loanProduct(LoanProduct.builder().build())
                .loanProductDetails(LoanProductDetails.builder().build())
                .project(Project.builder().build())
                .projectPolicy(ProjectPolicy.builder().build())
                .scheme(Scheme.builder().build())
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
    void validationFailureCarriesStructuredFailedEvent() {
        LoanProposal proposal = proposal();
        proposal.setFrequencyId(99L); // outside 1..10 -> LOAN_FREQUENCY_NOT_FOUND

        com.bits.loanproposal.domain.exception.LoanProposalValidationException ex =
                assertThrows(com.bits.loanproposal.domain.exception.LoanProposalValidationException.class,
                        () -> proposal.update(updateData(proposal, new BigDecimal("2000"), new BigDecimal("200"))));

        assertEquals("LOAN_FREQUENCY_NOT_FOUND", ex.getErrors().get("frequency").getKey());
        assertEquals("LOAN_FREQUENCY_NOT_FOUND", ex.getFailedEvent().getErrors().get("frequency"));
        assertEquals("trace-1", ex.getFailedEvent().getTracerId());
        assertEquals("loan-proposal.failed", ex.getFailedEvent().getRoutingKey());
        // still a DomainValidationException, so the library handler maps it to HTTP 400
        assertInstanceOf(DomainValidationException.class, ex);
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
        assertEquals(DomainStatus.UPDATED, proposal.getStatus());
        assertEquals(LoanProposalStatus.PENDING, proposal.getLoanProposalStatus());
        assertEquals(1, proposal.getEvents().size());
        assertInstanceOf(LoanProposalUpdatedEvent.class, proposal.getEvents().get(0));
    }
}
