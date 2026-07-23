package com.bits.loanproposal.application.commandhandler;

import com.bits.ddd.service.AggregateService;
import com.bits.loanproposal.application.command.BulkApproveLoanProposalsCommand;
import com.bits.loanproposal.domain.aggregate.LoanProposal;
import com.bits.loanproposal.domain.repository.LoanProposalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BulkApproveLoanProposalsCommandHandlerTest {

    @Mock
    private LoanProposalRepository loanProposalRepository;

    @Mock
    private AggregateService<LoanProposal, String> aggregateService;

    @InjectMocks
    private BulkApproveLoanProposalsCommandHandler handler;

    @Test
    @SuppressWarnings("unchecked")
    void approvesUsingBulkFetchInsteadOfOneQueryPerId() {
        LoanProposal first = eligibleLoanProposal("lp-1");
        LoanProposal second = eligibleLoanProposal("lp-2");
        when(loanProposalRepository.findByIdList(List.of("lp-1", "lp-2")))
                .thenReturn(List.of(first, second));

        handler.handle(new BulkApproveLoanProposalsCommand(
                "trace-1", List.of("lp-1", "lp-2"), "approver-1"));

        verify(loanProposalRepository).findByIdList(List.of("lp-1", "lp-2"));
        verify(loanProposalRepository, never()).findById(anyString());
        verify(first).approve("trace-1", "approver-1");
        verify(second).approve("trace-1", "approver-1");

        ArgumentCaptor<List<LoanProposal>> captor = ArgumentCaptor.forClass(List.class);
        verify(aggregateService).saveAll(captor.capture());
        assertEquals(List.of(first, second), captor.getValue());
    }

    private static LoanProposal eligibleLoanProposal(String id) {
        LoanProposal loanProposal = mock(LoanProposal.class);
        when(loanProposal.id()).thenReturn(id);
        when(loanProposal.isEligibleForApproval()).thenReturn(true);
        return loanProposal;
    }
}
