package com.bits.loanproposal.application.commandhandler;

import com.bits.ddd.annotation.RegisterCommandHandler;
import com.bits.ddd.handler.CommandHandler;
import com.bits.ddd.service.AggregateService;
import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.loanproposal.application.command.BulkApproveLoanProposalsCommand;
import com.bits.loanproposal.domain.aggregate.LoanProposal;
import com.bits.loanproposal.domain.exception.LoanProposalValidationException;
import com.bits.loanproposal.domain.repository.LoanProposalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RegisterCommandHandler
@RequiredArgsConstructor
public class BulkApproveLoanProposalsCommandHandler implements CommandHandler<BulkApproveLoanProposalsCommand> {

    private static final String APPROVER_REQUIRED = "APPROVER_REQUIRED";
    private static final String LOAN_PROPOSAL_NOT_FOUND = "LOAN_PROPOSAL_NOT_FOUND";
    private static final String LOAN_PROPOSAL_ID_REQUIRED = "LOAN_PROPOSAL_ID_REQUIRED";
    private static final String LOAN_PROPOSAL_IDS_REQUIRED = "LOAN_PROPOSAL_IDS_REQUIRED";
    private static final String LOAN_PROPOSAL_APPROVAL_NOT_ALLOWED = "LOAN_PROPOSAL_APPROVAL_NOT_ALLOWED";

    private final LoanProposalRepository loanProposalRepository;
    private final AggregateService<LoanProposal, String> aggregateService;

    @Override
    @Transactional
    public void handle(BulkApproveLoanProposalsCommand command) {
        Map<String, LocalizedMessage> errors = new LinkedHashMap<>();

        validateApprover(command, errors);
        validateRequestedIds(command, errors);

        Set<String> uniqueIds = command.getLoanProposalIds() == null ? Set.of() : new LinkedHashSet<>(command.getLoanProposalIds());

        List<LoanProposal> loanProposals = fetchAndValidate(uniqueIds, errors);

        if (!errors.isEmpty()) {
            throw new LoanProposalValidationException(errors);
        }

        loanProposals.forEach(loanProposal ->
                loanProposal.approve(command.getTracerId(), command.getApprovedBy()));

        log.info("Bulk approving {} loan proposals, traceId={}, approvedBy={}",
                loanProposals.size(), command.getTracerId(), command.getApprovedBy());
        aggregateService.saveAll(loanProposals);
    }

    private void validateApprover(BulkApproveLoanProposalsCommand command, Map<String, LocalizedMessage> errors) {
        if (command.getApprovedBy() == null || command.getApprovedBy().isBlank()) {
            errors.put("approvedBy", LocalizedMessage.builder().key(APPROVER_REQUIRED).build());
        }
    }

    private void validateRequestedIds(BulkApproveLoanProposalsCommand command, Map<String, LocalizedMessage> errors) {
        if (command.getLoanProposalIds() == null || command.getLoanProposalIds().isEmpty()) {
            errors.put("loanProposalIds", LocalizedMessage.builder().key(LOAN_PROPOSAL_IDS_REQUIRED).build());
        }
    }

    private List<LoanProposal> fetchAndValidate(Set<String> loanProposalIds, Map<String, LocalizedMessage> errors) {
        List<LoanProposal> loanProposals = new ArrayList<>();

        for (String id : loanProposalIds) {
            if (id == null || id.isBlank()) {
                errors.put("loanProposalIds", LocalizedMessage.builder().key(LOAN_PROPOSAL_ID_REQUIRED).build());
                continue;
            }

            loanProposalRepository.findById(id)
                    .ifPresentOrElse(loanProposal -> validateEligibility(loanProposal, errors, loanProposals),
                            () -> errors.put(id, LocalizedMessage.builder().key(LOAN_PROPOSAL_NOT_FOUND).build()));
        }

        return loanProposals;
    }

    private void validateEligibility(LoanProposal loanProposal, Map<String, LocalizedMessage> errors, List<LoanProposal> loanProposals) {
        if (!loanProposal.isEligibleForApproval()) {
            errors.put(loanProposal.id(), LocalizedMessage.builder()
                    .key(LOAN_PROPOSAL_APPROVAL_NOT_ALLOWED)
                    .args(new Object[]{loanProposal.approvalIneligibilityReason()})
                    .build());
            return;
        }

        loanProposals.add(loanProposal);
    }
}
