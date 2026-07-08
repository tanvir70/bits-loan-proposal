package com.bits.loanproposal.application.commandhandler;

import com.bits.ddd.annotation.PersistDomain;
import com.bits.ddd.annotation.RegisterCommandHandler;
import com.bits.ddd.handler.CommandHandler;
import com.bits.ddd.service.DomainPersistenceService;
import com.bits.ddd.service.MessageProcessor;
import com.bits.loanproposal.application.command.DeleteLoanProposalCommand;
import com.bits.loanproposal.application.mapper.LoanProposalDataMapper;
import com.bits.loanproposal.application.service.LoanProposalQueryService;
import com.bits.loanproposal.domain.aggregate.LoanProposal;
import com.bits.loanproposal.domain.exception.LoanProposalValidationException;
import com.bits.loanproposal.domain.param.LoanProposalDeletionData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RegisterCommandHandler
@RequiredArgsConstructor
public class DeleteLoanProposalCommandHandler implements CommandHandler<DeleteLoanProposalCommand> {

    @PersistDomain
    private final DomainPersistenceService<LoanProposal, String> persistenceService;
    private final MessageProcessor messageProcessor;
    private final LoanProposalQueryService loanProposalQueryService;
    private final LoanProposalDataMapper loanProposalDataMapper;

    @Override
    @Transactional
    public void handle(DeleteLoanProposalCommand deleteLoanProposalCommand) {
        LoanProposal loanProposal = loanProposalQueryService.fetchByIdOrHandleFailure(deleteLoanProposalCommand.getId(), deleteLoanProposalCommand.getTracerId());
        LoanProposalDeletionData loanProposalDeletionData = loanProposalDataMapper.toDeletionData(deleteLoanProposalCommand);

        try {
            loanProposal.delete(loanProposalDeletionData);
        } catch (LoanProposalValidationException ex) {
            CreateLoanProposalCommandHandler.publishFailedEvent(ex, messageProcessor);
            throw ex;
        }

        persistenceService.persist(loanProposal);
        messageProcessor.publish(loanProposal.getEvents());
    }
}
