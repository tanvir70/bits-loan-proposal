package com.bits.loanproposal.application.commandhandler;

import com.bits.ddd.annotation.PersistDomain;
import com.bits.ddd.annotation.RegisterCommandHandler;
import com.bits.ddd.handler.CommandHandler;
import com.bits.ddd.service.DomainPersistenceService;
import com.bits.ddd.service.MessageProcessor;
import com.bits.ddd.service.SourceDataContext;
import com.bits.loanproposal.application.command.UpdateLoanProposalCommand;
import com.bits.loanproposal.application.dto.LoanProposalSourceData;
import com.bits.loanproposal.application.mapper.LoanProposalDataMapper;
import com.bits.loanproposal.application.mapper.LoanProposalSourceDataMapper;
import com.bits.loanproposal.application.service.LoanProposalQueryService;
import com.bits.loanproposal.application.service.UpdateLoanProposalSourceDataProvider;
import com.bits.loanproposal.domain.aggregate.LoanProposal;
import com.bits.loanproposal.domain.exception.LoanProposalValidationException;
import com.bits.loanproposal.domain.param.LoanProposalUpdateData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RegisterCommandHandler
public class UpdateLoanProposalCommandHandler implements CommandHandler<UpdateLoanProposalCommand> {

    @PersistDomain
    private final DomainPersistenceService<LoanProposal, String> persistenceService;
    private final UpdateLoanProposalSourceDataProvider sourceDataProvider;
    private final MessageProcessor messageProcessor;
    private final LoanProposalQueryService queryService;
    private final LoanProposalDataMapper dataMapper;

    public UpdateLoanProposalCommandHandler(DomainPersistenceService<LoanProposal, String> persistenceService,
                                            UpdateLoanProposalSourceDataProvider sourceDataProvider,
                                            MessageProcessor messageProcessor,
                                            LoanProposalQueryService queryService,
                                            LoanProposalDataMapper dataMapper) {
        this.persistenceService = persistenceService;
        this.sourceDataProvider = sourceDataProvider;
        this.messageProcessor = messageProcessor;
        this.queryService = queryService;
        this.dataMapper = dataMapper;
    }

    @Override
    @Transactional
    public void handle(UpdateLoanProposalCommand command) {
        LoanProposal loanProposal = queryService.fetchByIdOrHandleFailure(command.getId(), command.getTracerId());
        SourceDataContext sourceDataContext = sourceDataProvider.provide(command, loanProposal);
        LoanProposalSourceData sourceData = LoanProposalSourceDataMapper.toSourceData(sourceDataContext);
        LoanProposalUpdateData updateData = dataMapper.toUpdateData(command, sourceData);

        try {
            loanProposal.update(updateData);
        } catch (LoanProposalValidationException ex) {
            CreateLoanProposalCommandHandler.publishFailedEvent(ex, messageProcessor);
            throw ex;
        }

        persistenceService.persist(loanProposal);
        messageProcessor.publish(loanProposal.getEvents());
        loanProposal.clearEvents();
    }
}
