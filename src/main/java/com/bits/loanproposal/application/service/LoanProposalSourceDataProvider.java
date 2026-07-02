package com.bits.loanproposal.application.service;

import com.bits.ddd.application.service.SourceDataContext;
import com.bits.ddd.application.service.SourceDataCoordinator;
import com.bits.ddd.application.service.SourceDataProvider;
import com.bits.ddd.shared.exception.domain.DomainValidationException;
import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.loanproposal.application.command.CreateLoanProposalCommand;
import com.bits.loanproposal.infrastructure.persistence.repository.ClientInfoDocumentRepository;
import org.springframework.stereotype.Component;

@Component
public class LoanProposalSourceDataProvider implements SourceDataProvider<CreateLoanProposalCommand> {

    private final SourceDataCoordinator coordinator;
    private final ClientInfoDocumentRepository clientInfoRepository;

    public LoanProposalSourceDataProvider(
            SourceDataCoordinator coordinator,
            ClientInfoDocumentRepository clientInfoRepository) {
        this.coordinator = coordinator;
        this.clientInfoRepository = clientInfoRepository;
    }

    @Override
    public SourceDataContext provide(CreateLoanProposalCommand command) {
        SourceDataCoordinator.SourceDataFetchBuilder builder = coordinator.builder(command.getTracerId());
        
        // Use the library's add method for lookup
        builder.add("clientInfo", clientInfoRepository, command.getApplicantName(), "applicantName",
                LocalizedMessage.builder().key("CLIENT_INFO_NOT_FOUND").build());

        return builder.fetch(errors -> new DomainValidationException(
                "SOURCE_DATA_ERROR",
                errors.toString()
        ));
    }
}
