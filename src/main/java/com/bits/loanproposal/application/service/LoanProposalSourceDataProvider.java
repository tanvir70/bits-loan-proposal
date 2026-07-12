package com.bits.loanproposal.application.service;

import com.bits.ddd.service.SourceDataContext;
import com.bits.ddd.service.SourceDataCoordinator;
import com.bits.ddd.service.SourceDataProvider;
import com.bits.ddd.shared.exception.domain.SourceDataValidationException;
import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.loanproposal.application.command.CreateLoanProposalCommand;
import com.bits.loanproposal.infrastructure.persistence.repository.repository.*;
import org.springframework.stereotype.Component;

@Component
public class LoanProposalSourceDataProvider implements SourceDataProvider<CreateLoanProposalCommand> {

    private final SourceDataCoordinator coordinator;

    // Inject the 12 auto-generated repositories
    private final MemberDocumentRepository memberRepository;
    private final LoanProductDocumentRepository loanProductRepository;
    private final LoanProductDetailsDocumentRepository loanProductDetailsRepository;
    private final LoanProductPolicyDocumentRepository loanProductPolicyRepository;
    private final SchemeDocumentRepository schemeRepository;
    private final ProjectDocumentRepository projectRepository;
    private final ProjectPolicyDocumentRepository projectPolicyRepository;
    private final BranchDocumentRepository branchRepository;
    private final VillageOrganisationDocumentRepository villageOrganisationRepository;
    private final InsuranceProductDocumentRepository insuranceProductRepository;
    private final CountryDocumentRepository countryRepository;
    private final BankDocumentRepository bankRepository;

    public LoanProposalSourceDataProvider(
            SourceDataCoordinator coordinator,
            MemberDocumentRepository memberRepository,
            LoanProductDocumentRepository loanProductRepository,
            LoanProductDetailsDocumentRepository loanProductDetailsRepository,
            LoanProductPolicyDocumentRepository loanProductPolicyRepository,
            SchemeDocumentRepository schemeRepository,
            ProjectDocumentRepository projectRepository,
            ProjectPolicyDocumentRepository projectPolicyRepository,
            BranchDocumentRepository branchRepository,
            VillageOrganisationDocumentRepository villageOrganisationRepository,
            InsuranceProductDocumentRepository insuranceProductRepository,
            CountryDocumentRepository countryRepository,
            BankDocumentRepository bankRepository) {
        this.coordinator = coordinator;
        this.memberRepository = memberRepository;
        this.loanProductRepository = loanProductRepository;
        this.loanProductDetailsRepository = loanProductDetailsRepository;
        this.loanProductPolicyRepository = loanProductPolicyRepository;
        this.schemeRepository = schemeRepository;
        this.projectRepository = projectRepository;
        this.projectPolicyRepository = projectPolicyRepository;
        this.branchRepository = branchRepository;
        this.villageOrganisationRepository = villageOrganisationRepository;
        this.insuranceProductRepository = insuranceProductRepository;
        this.countryRepository = countryRepository;
        this.bankRepository = bankRepository;
    }

    @Override
    public SourceDataContext provide(CreateLoanProposalCommand command) {
        SourceDataCoordinator.SourceDataFetchBuilder builder = coordinator.builder(command.getTracerId());

        // Add always-required lookups
        builder.add("member", memberRepository, command.getMemberId(), "memberId",
                LocalizedMessage.builder().key("MEMBER_NOT_FOUND").build());

        builder.add("loanProduct", loanProductRepository, command.getLoanProductId(), "loanProductId",
                LocalizedMessage.builder().key("LOAN_PRODUCT_NOT_FOUND").build());

        builder.add("loanProductDetails", loanProductDetailsRepository, command.getLoanProductDetailsId(), "loanProductDetailsId",
                LocalizedMessage.builder().key("LOAN_PRODUCT_DETAILS_NOT_FOUND").build());

        builder.add("loanProductPolicy", loanProductPolicyRepository, command.getLoanProductPolicyId(), "loanProductPolicyId",
                LocalizedMessage.builder().key("LOAN_PRODUCT_POLICY_NOT_FOUND").build());

        builder.add("scheme", schemeRepository, command.getSchemeId(), "schemeId",
                LocalizedMessage.builder().key("SCHEME_NOT_FOUND").build());

        builder.add("project", projectRepository, command.getProjectId(), "projectId",
                LocalizedMessage.builder().key("PROJECT_NOT_FOUND").build());

        builder.add("projectPolicy", projectPolicyRepository, command.getProjectId(), "projectId",
                LocalizedMessage.builder().key("PROJECT_POLICY_NOT_FOUND").build());

        builder.add("branch", branchRepository, command.getBranchId(), "branchId",
                LocalizedMessage.builder().key("BRANCH_NOT_FOUND").build());

        // Add conditional lookups
        if (command.getVillageOrganisationId() != null) {
            builder.add("villageOrganisation", villageOrganisationRepository, command.getVillageOrganisationId(), "voId",
                    LocalizedMessage.builder().key("VO_NOT_FOUND").build());
        }

        if (command.getInsuranceProductId() != null) {
            builder.add("insuranceProduct", insuranceProductRepository, command.getInsuranceProductId(), "insuranceProductId",
                    LocalizedMessage.builder().key("INSURANCE_PRODUCT_NOT_FOUND").build());
        }

        if (command.getCountryId() != null) {
            builder.add("country", countryRepository, command.getCountryId(), "countryId",
                    LocalizedMessage.builder().key("COUNTRY_NOT_FOUND").build());
        }

        if (command.getBankId() != null) {
            builder.add("bank", bankRepository, command.getBankId(), "bankId",
                    LocalizedMessage.builder().key("BANK_NOT_FOUND").build());
        }

        return builder.fetch((tracerId, errors) ->
                new SourceDataValidationException(tracerId, "CreateLoanProposalCommand", errors));
    }
}
