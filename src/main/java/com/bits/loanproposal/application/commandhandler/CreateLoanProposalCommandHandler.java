package com.bits.loanproposal.application.commandhandler;

import com.bits.ddd.annotation.PersistDomain;
import com.bits.ddd.annotation.RegisterCommandHandler;
import com.bits.ddd.handler.CommandHandler;
import com.bits.ddd.service.DomainPersistenceService;
import com.bits.ddd.service.MessageProcessor;
import com.bits.ddd.service.SourceDataContext;
import com.bits.ddd.shared.exception.domain.DomainValidationException;
import com.bits.loanproposal.application.command.CreateLoanProposalCommand;
import com.bits.loanproposal.application.dto.LoanProposalSourceData;
import com.bits.loanproposal.application.mapper.LoanProposalDataMapper;
import com.bits.loanproposal.application.mapper.LoanProposalSourceDataMapper;
import com.bits.loanproposal.application.service.LoanProposalSourceDataProvider;
import com.bits.loanproposal.application.service.ProposalNumberSequenceService;
import com.bits.loanproposal.domain.aggregate.LoanProposal;
import com.bits.loanproposal.domain.aggregate.LoanProposalRepository;
import com.bits.loanproposal.domain.exception.LoanProposalValidationException;
import com.bits.loanproposal.domain.param.LoanProposalCreationData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.bits.loanproposal.domain.constant.DomainErrorConstant.ALREADY_EXISTS;
import static com.bits.loanproposal.domain.constant.DomainErrorConstant.LOAN_PROPOSAL_ALREADY_EXISTS;

@Slf4j
@Service
@RegisterCommandHandler
public class CreateLoanProposalCommandHandler implements CommandHandler<CreateLoanProposalCommand> {

    @PersistDomain
    private final DomainPersistenceService<LoanProposal, String> persistenceService;
    private final LoanProposalSourceDataProvider sourceDataProvider;
    private final MessageProcessor messageProcessor;
    private final LoanProposalRepository loanProposalRepository;
    private final LoanProposalDataMapper dataMapper;
    private final ProposalNumberSequenceService sequenceService;

    public CreateLoanProposalCommandHandler(
            DomainPersistenceService<LoanProposal, String> persistenceService,
            LoanProposalSourceDataProvider sourceDataProvider,
            MessageProcessor messageProcessor,
            LoanProposalRepository loanProposalRepository,
            LoanProposalDataMapper dataMapper,
            ProposalNumberSequenceService sequenceService) {
        this.persistenceService = persistenceService;
        this.sourceDataProvider = sourceDataProvider;
        this.messageProcessor = messageProcessor;
        this.loanProposalRepository = loanProposalRepository;
        this.dataMapper = dataMapper;
        this.sequenceService = sequenceService;
    }

    @Override
    public void handle(CreateLoanProposalCommand command) {
        if (loanProposalRepository.findById(command.getId()).isPresent()) {
            throw new DomainValidationException(ALREADY_EXISTS, LOAN_PROPOSAL_ALREADY_EXISTS);
        }

        SourceDataContext context = sourceDataProvider.provide(command);
        LoanProposalSourceData sourceData = LoanProposalSourceDataMapper.toSourceData(context);

        // a validation failure below burns this sequence number — gaps in proposal numbers are acceptable
        long sequence = sequenceService.next(dataMapper.deriveApplicationDate(sourceData));
        LoanProposalCreationData creationData = dataMapper.toCreationData(command, sourceData, sequence);
        LoanProposal loanProposal;
        try {
            loanProposal = LoanProposal.create(creationData, sourceData);
        } catch (LoanProposalValidationException loanProposalValidationException) {
            publishFailedEvent(loanProposalValidationException, messageProcessor);
            throw loanProposalValidationException;
        }

        persistenceService.persist(loanProposal);
        messageProcessor.publish(loanProposal.getEvents());
    }

    // Fire-and-forget: the aggregate is never persisted on validation failure, so this event
    // has no outbox row; a broker outage here must not turn the client's 400 into a 500.
    static void publishFailedEvent(LoanProposalValidationException loanProposalValidationException, MessageProcessor messageProcessor) {
        try {
            messageProcessor.publish(List.of(loanProposalValidationException.getFailedEvent()));
        } catch (Exception publishFailure) {
            log.error("Could not publish LoanProposalFailedEvent for trace {}: {}",
                    loanProposalValidationException.getFailedEvent().getTracerId(), publishFailure.getMessage());
        }
    }
}
