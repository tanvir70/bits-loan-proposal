package com.bits.loanproposal.application.commandhandler;

import com.bits.ddd.annotation.PersistDomain;
import com.bits.ddd.annotation.RegisterCommandHandler;
import com.bits.ddd.handler.CommandHandler;
import com.bits.ddd.service.DomainPersistenceService;
import com.bits.ddd.shared.service.MessagePublisher;
import com.bits.ddd.service.SourceDataContext;
import com.bits.ddd.shared.localization.LocalizedMessage;
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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

import static com.bits.loanproposal.domain.constant.DomainErrorConstant.ALREADY_EXISTS;
import static com.bits.loanproposal.domain.constant.DomainErrorConstant.LOAN_PROPOSAL_ALREADY_EXISTS;

@Slf4j
@Service
@RegisterCommandHandler
@AllArgsConstructor
public class CreateLoanProposalCommandHandler implements CommandHandler<CreateLoanProposalCommand> {

    @PersistDomain
    private final DomainPersistenceService<LoanProposal, String> persistenceService;
    private final LoanProposalSourceDataProvider sourceDataProvider;
    private final MessagePublisher messagePublisher;
    private final LoanProposalRepository loanProposalRepository;
    private final LoanProposalDataMapper loanProposalDataMapper;
    private final ProposalNumberSequenceService numberSequenceService;

    @Override
    public void handle(CreateLoanProposalCommand command) {
        if (loanProposalRepository.findById(command.getId()).isPresent()) {
            throw new LoanProposalValidationException(ALREADY_EXISTS, LOAN_PROPOSAL_ALREADY_EXISTS);
        }

        SourceDataContext context = sourceDataProvider.provide(command);
        LoanProposalSourceData sourceData = LoanProposalSourceDataMapper.toSourceData(context);

        LocalDate applicationDate = loanProposalDataMapper.deriveApplicationDate(sourceData);
        if (applicationDate == null) {
            Map<String, LocalizedMessage> errors = Map.of("applicationDate",
                    LocalizedMessage.builder().key("BUSINESS_DATE_NOT_AVAILABLE").build());
            throw new LoanProposalValidationException(errors);
        }

        long sequence = numberSequenceService.next(applicationDate);
        LoanProposalCreationData creationData = loanProposalDataMapper.toCreationData(command, sourceData, sequence);

        LoanProposal loanProposal = LoanProposal.create(creationData, sourceData);

        persistenceService.persist(loanProposal);
        messagePublisher.publishAll(loanProposal.getEvents());
    }
}
