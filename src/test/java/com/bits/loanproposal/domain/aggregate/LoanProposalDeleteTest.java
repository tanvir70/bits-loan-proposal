package com.bits.loanproposal.domain.aggregate;

import com.bits.ddd.shared.domain.value.DomainStatus;
import com.bits.loanproposal.domain.enums.LoanProposalStatus;
import com.bits.loanproposal.domain.event.LoanProposalDeletedEvent;
import com.bits.loanproposal.domain.exception.LoanProposalValidationException;
import com.bits.loanproposal.domain.param.LoanProposalDeletionData;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoanProposalDeleteTest {

    private LoanProposal proposal(LoanProposalStatus status) {
        LoanProposal proposal = new LoanProposal();
        proposal.setId("lp-1");
        proposal.setBranchId(10L);
        proposal.setMemberId(20L);
        proposal.setProposalNumber("202607-00001");
        proposal.setLoanProposalStatus(status);
        return proposal;
    }

    private LoanProposalDeletionData deletionData() {
        return new LoanProposalDeletionData(
                "trace-1",
                "lp-1",
                10L,
                "user-1",
                LocalDateTime.of(2026, 7, 8, 12, 30)
        );
    }

    @Test
    void pendingProposalIsSoftDeletedAndDeletedEventIsRaised() {
        LoanProposal proposal = proposal(LoanProposalStatus.PENDING);

        proposal.delete(deletionData());

        assertEquals(Boolean.TRUE, proposal.getDeleted());
        assertEquals("user-1", proposal.getDeletedBy());
        assertEquals(LocalDateTime.of(2026, 7, 8, 12, 30), proposal.getDeletedAt());
        assertEquals(DomainStatus.INACTIVE, proposal.getStatus());
        assertEquals(LoanProposalStatus.PENDING, proposal.getLoanProposalStatus());
        assertEquals("trace-1", proposal.getTracerId());
        assertEquals(1, proposal.getEvents().size());
        assertInstanceOf(LoanProposalDeletedEvent.class, proposal.getEvents().get(0));

        LoanProposalDeletedEvent event = (LoanProposalDeletedEvent) proposal.getEvents().get(0);
        assertEquals("loan-proposal.deleted", event.getRoutingKey());
        assertEquals(DomainStatus.INACTIVE, event.getDomainStatus());
        assertEquals("user-1", event.getDeletedBy());
    }

    @Test
    void nonPendingProposalDeleteRaisesFailedEvent() {
        LoanProposal proposal = proposal(LoanProposalStatus.APPROVED);

        LoanProposalValidationException ex = assertThrows(LoanProposalValidationException.class,
                () -> proposal.delete(deletionData()));

        assertEquals("DELETE_FAILED", ex.getErrors().get("loanProposal").getKey());
        assertEquals("trace-1", ex.getFailedEvent().getTracerId());
        assertEquals("loan-proposal.failed", ex.getFailedEvent().getRoutingKey());
    }
}
