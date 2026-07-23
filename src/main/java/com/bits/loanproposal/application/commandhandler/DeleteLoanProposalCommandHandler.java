package com.bits.loanproposal.application.commandhandler;

import com.bits.ddd.annotation.RegisterCommandHandler;
import com.bits.ddd.handler.CommandHandler;
import com.bits.ddd.service.AggregateService;
import com.bits.loanproposal.application.command.DeleteLoanProposalCommand;
import com.bits.loanproposal.application.mapper.LoanProposalDataMapper;
import com.bits.loanproposal.application.service.LoanProposalQueryService;
import com.bits.loanproposal.domain.aggregate.LoanProposal;
import com.bits.loanproposal.domain.param.LoanProposalDeletionData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RegisterCommandHandler
@RequiredArgsConstructor
public class DeleteLoanProposalCommandHandler implements CommandHandler<DeleteLoanProposalCommand> {

    private final AggregateService<LoanProposal, String> aggregateService;
    private final LoanProposalQueryService loanProposalQueryService;
    private final LoanProposalDataMapper loanProposalDataMapper;

    @Override
    @Transactional
    public void handle(DeleteLoanProposalCommand deleteLoanProposalCommand) {
        LoanProposal loanProposal = loanProposalQueryService.fetchByIdOrHandleFailure(deleteLoanProposalCommand.getId(), deleteLoanProposalCommand.getTracerId());
        LoanProposalDeletionData loanProposalDeletionData = loanProposalDataMapper.toDeletionData(deleteLoanProposalCommand);

        loanProposal.delete(loanProposalDeletionData);

        aggregateService.update(loanProposal);
    }
}
