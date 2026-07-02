package com.bits.loanproposal.application.commandhandler;

import com.bits.ddd.annotation.PersistDomain;
import com.bits.ddd.annotation.RegisterCommandHandler;
import com.bits.ddd.application.handler.CommandHandler;
import com.bits.ddd.application.service.MessageProcessor;
import com.bits.ddd.application.service.SourceDataContext;
import com.bits.ddd.infra.persistence.service.DomainPersistenceService;
import com.bits.loanproposal.application.command.CreateLoanProposalCommand;
import com.bits.loanproposal.application.dto.LoanProposalSourceData;
import com.bits.loanproposal.application.mapper.LoanProposalSourceDataMapper;
import com.bits.loanproposal.application.service.LoanProposalSourceDataProvider;
import com.bits.loanproposal.domain.aggregate.LoanProposal;
import com.bits.loanproposal.domain.param.LoanProposalCreationData;
import org.springframework.stereotype.Service;

@Service
@RegisterCommandHandler
public class CreateLoanProposalCommandHandler implements CommandHandler<CreateLoanProposalCommand> {

    @PersistDomain
    private final DomainPersistenceService<LoanProposal, String> persistenceService;
    private final LoanProposalSourceDataProvider sourceDataProvider;
    private final MessageProcessor messageProcessor;

    public CreateLoanProposalCommandHandler(
            DomainPersistenceService<LoanProposal, String> persistenceService,
            LoanProposalSourceDataProvider sourceDataProvider,
            MessageProcessor messageProcessor) {
        this.persistenceService = persistenceService;
        this.sourceDataProvider = sourceDataProvider;
        this.messageProcessor = messageProcessor;
    }

    @Override
    public void handle(CreateLoanProposalCommand command) {
        SourceDataContext context = sourceDataProvider.provide(command);
        LoanProposalSourceData sourceData = LoanProposalSourceDataMapper.toSourceData(context);
        
        LoanProposalCreationData creationData = new LoanProposalCreationData(
            command.getTracerId(),
            command.getApplicantName(),
            command.getAmount()
        );
        LoanProposal loanProposal = LoanProposal.create(creationData);
        persistenceService.persist(loanProposal);
        messageProcessor.publish(loanProposal.getEvents());
        loanProposal.clearEvents();
    }
}
