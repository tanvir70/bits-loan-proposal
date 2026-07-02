package com.bits.loanproposal.application.commandhandler;

import com.bits.ddd.annotation.PersistDomain;
import com.bits.ddd.annotation.RegisterCommandHandler;
import com.bits.ddd.handler.CommandHandler;
import com.bits.ddd.service.MessageProcessor;
import com.bits.ddd.service.SourceDataContext;
import com.bits.ddd.service.DomainPersistenceService;
import com.bits.ddd.shared.exception.domain.DomainValidationException;
import com.bits.loanproposal.application.command.CreateLoanProposalCommand;
import com.bits.loanproposal.application.dto.LoanProposalSourceData;
import com.bits.loanproposal.application.mapper.LoanProposalDataMapper;
import com.bits.loanproposal.application.mapper.LoanProposalSourceDataMapper;
import com.bits.loanproposal.application.service.LoanProposalSourceDataProvider;
import com.bits.loanproposal.domain.aggregate.LoanProposal;
import com.bits.loanproposal.domain.aggregate.LoanProposalRepository;
import com.bits.loanproposal.domain.param.LoanProposalCreationData;
import org.springframework.stereotype.Service;

@Service
@RegisterCommandHandler
public class CreateLoanProposalCommandHandler implements CommandHandler<CreateLoanProposalCommand> {

    @PersistDomain
    private final DomainPersistenceService<LoanProposal, String> persistenceService;
    private final LoanProposalSourceDataProvider sourceDataProvider;
    private final MessageProcessor messageProcessor;
    private final LoanProposalRepository repository;

    public CreateLoanProposalCommandHandler(
            DomainPersistenceService<LoanProposal, String> persistenceService,
            LoanProposalSourceDataProvider sourceDataProvider,
            MessageProcessor messageProcessor,
            LoanProposalRepository repository) {
        this.persistenceService = persistenceService;
        this.sourceDataProvider = sourceDataProvider;
        this.messageProcessor = messageProcessor;
        this.repository = repository;
    }

    @Override
    public void handle(CreateLoanProposalCommand command) {
        // 1. Verify proposal ID uniqueness
        if (repository.findById(command.getId()).isPresent()) {
            throw new DomainValidationException(
                "ALREADY_EXISTS", 
                "Buffer Loan Proposal already exists with given id."
            );
        }

        // 2. Fetch all required lookups
        SourceDataContext context = sourceDataProvider.provide(command);
        LoanProposalSourceData sourceData = LoanProposalSourceDataMapper.toSourceData(context);
        
        // 3. Map to parameters and create aggregate
        LoanProposalCreationData creationData = LoanProposalDataMapper.toCreationData(command, sourceData);
        LoanProposal loanProposal = LoanProposal.create(creationData);
        
        // 4. Persist and publish events
        persistenceService.persist(loanProposal);
        messageProcessor.publish(loanProposal.getEvents());
        loanProposal.clearEvents();
    }
}
