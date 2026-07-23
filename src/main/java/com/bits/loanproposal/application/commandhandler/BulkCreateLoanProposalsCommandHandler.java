package com.bits.loanproposal.application.commandhandler;

import com.bits.ddd.annotation.RegisterCommandHandler;
import com.bits.ddd.handler.CommandHandler;
import com.bits.ddd.service.AggregateService;
import com.bits.ddd.service.SourceDataContext;
import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.loanproposal.application.command.BulkCreateLoanProposalsCommand;
import com.bits.loanproposal.application.command.CreateLoanProposalCommand;
import com.bits.loanproposal.application.dto.LoanProposalSourceData;
import com.bits.loanproposal.application.mapper.LoanProposalDataMapper;
import com.bits.loanproposal.application.mapper.LoanProposalSourceDataMapper;
import com.bits.loanproposal.application.service.LoanProposalSourceDataProvider;
import com.bits.loanproposal.application.service.ProposalNumberSequenceService;
import com.bits.loanproposal.domain.aggregate.LoanProposal;
import com.bits.loanproposal.domain.exception.LoanProposalValidationException;
import com.bits.loanproposal.domain.param.LoanProposalCreationData;
import com.bits.loanproposal.domain.repository.LoanProposalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

import static com.bits.loanproposal.application.constant.CommandHandlerErrorConstant.BUSINESS_DATE_NOT_AVAILABLE;
import static com.bits.loanproposal.application.constant.CommandHandlerErrorConstant.DUPLICATE_LOAN_PROPOSAL_ID;
import static com.bits.loanproposal.application.constant.CommandHandlerErrorConstant.LOAN_PROPOSAL_ID_REQUIRED;
import static com.bits.loanproposal.application.constant.CommandHandlerErrorConstant.LOAN_PROPOSALS_REQUIRED;
import static com.bits.loanproposal.domain.constant.DomainErrorConstant.LOAN_PROPOSAL_ALREADY_EXISTS;

@Slf4j
@Service
@RegisterCommandHandler
@RequiredArgsConstructor
public class BulkCreateLoanProposalsCommandHandler implements CommandHandler<BulkCreateLoanProposalsCommand> {

    private final AggregateService<LoanProposal, String> aggregateService;
    private final LoanProposalSourceDataProvider sourceDataProvider;
    private final LoanProposalRepository loanProposalRepository;
    private final LoanProposalDataMapper loanProposalDataMapper;
    private final ProposalNumberSequenceService numberSequenceService;

    @Override
    @Transactional
    public void handle(BulkCreateLoanProposalsCommand bulkCreateLoanProposalsCommand) {
        Map<String, LocalizedMessage> errors = new LinkedHashMap<>();
        List<CreateLoanProposalCommand> createLoanProposalCommands = bulkCreateLoanProposalsCommand.getLoanProposals();

        validateRequestedProposals(createLoanProposalCommands, errors);
        validateDuplicateRequestIds(createLoanProposalCommands, errors);
        validateExistingIds(createLoanProposalCommands, errors);

        if (!errors.isEmpty()) {
            throw new LoanProposalValidationException(errors);
        }

        List<LoanProposal> loanProposals = new ArrayList<>();
        for (CreateLoanProposalCommand createLoanProposalCommand : createLoanProposalCommands) {
            SourceDataContext sourceDataContext = sourceDataProvider.provide(createLoanProposalCommand);
            LoanProposalSourceData loanProposalSourceData = LoanProposalSourceDataMapper.toSourceData(sourceDataContext);

            LocalDate applicationDate = loanProposalDataMapper.deriveApplicationDate(loanProposalSourceData);
            if (applicationDate == null) {
                errors.put(createLoanProposalCommand.getId() + ".applicationDate",
                        LocalizedMessage.builder().key(BUSINESS_DATE_NOT_AVAILABLE).build());
                continue;
            }

            long sequence = numberSequenceService.next(applicationDate);
            LoanProposalCreationData loanProposalCreationData = loanProposalDataMapper.toCreationData(createLoanProposalCommand, loanProposalSourceData, sequence);
            loanProposals.add(LoanProposal.create(loanProposalCreationData, loanProposalSourceData));
        }

        if (!errors.isEmpty()) {
            throw new LoanProposalValidationException(errors);
        }

        log.info("Bulk creating {} loan proposals, traceId={}", loanProposals.size(), bulkCreateLoanProposalsCommand.getTracerId());
        aggregateService.saveAll(loanProposals);
    }

    private void validateRequestedProposals(List<CreateLoanProposalCommand> createLoanProposalCommands,
                                            Map<String, LocalizedMessage> errors) {
        if (createLoanProposalCommands == null || createLoanProposalCommands.isEmpty()) {
            errors.put("loanProposals", LocalizedMessage.builder().key(LOAN_PROPOSALS_REQUIRED).build());
            return;
        }

        for (int index = 0; index < createLoanProposalCommands.size(); index++) {
            CreateLoanProposalCommand createLoanProposalCommand = createLoanProposalCommands.get(index);
            if (createLoanProposalCommand == null || createLoanProposalCommand.getId() == null || createLoanProposalCommand.getId().isBlank()) {
                errors.put("loanProposals[%d].id".formatted(index),
                        LocalizedMessage.builder().key(LOAN_PROPOSAL_ID_REQUIRED).build());
            }
        }
    }

    private void validateDuplicateRequestIds(List<CreateLoanProposalCommand> createLoanProposalCommands,
                                             Map<String, LocalizedMessage> errors) {
        if (createLoanProposalCommands == null) {
            return;
        }

        Set<String> seenIds = new HashSet<>();
        Set<String> duplicateIds = new LinkedHashSet<>();
        for (CreateLoanProposalCommand createLoanProposalCommand : createLoanProposalCommands) {
            if (createLoanProposalCommand == null || createLoanProposalCommand.getId() == null || createLoanProposalCommand.getId().isBlank()) {
                continue;
            }
            if (!seenIds.add(createLoanProposalCommand.getId())) {
                duplicateIds.add(createLoanProposalCommand.getId());
            }
        }

        duplicateIds.forEach(id -> errors.put(id,
                LocalizedMessage.builder().key(DUPLICATE_LOAN_PROPOSAL_ID).build()));
    }

    private void validateExistingIds(List<CreateLoanProposalCommand> createLoanProposalCommands,
                                     Map<String, LocalizedMessage> errors) {
        if (createLoanProposalCommands == null || !errors.isEmpty()) {
            return;
        }

        List<String> ids = createLoanProposalCommands.stream()
                .map(CreateLoanProposalCommand::getId)
                .filter(id -> id != null && !id.isBlank())
                .distinct()
                .toList();

        loanProposalRepository.findByIdList(ids)
                .forEach(loanProposal -> errors.put(loanProposal.id(),
                        LocalizedMessage.builder().key(LOAN_PROPOSAL_ALREADY_EXISTS).build()));
    }
}
