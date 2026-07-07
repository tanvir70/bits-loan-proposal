package com.bits.loanproposal.application.service;

import com.bits.ddd.service.SourceDataContext;
import com.bits.ddd.service.SourceDataCoordinator;
import com.bits.ddd.shared.exception.domain.SourceDataValidationException;
import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.loanproposal.application.command.UpdateLoanProposalCommand;
import com.bits.loanproposal.domain.aggregate.LoanProposal;
import com.bits.loanproposal.infrastructure.persistence.repository.repository.BankDocumentRepository;
import com.bits.loanproposal.infrastructure.persistence.repository.repository.BranchDocumentRepository;
import com.bits.loanproposal.infrastructure.persistence.repository.repository.CountryDocumentRepository;
import com.bits.loanproposal.infrastructure.persistence.repository.repository.InsuranceProductDocumentRepository;
import com.bits.loanproposal.infrastructure.persistence.repository.repository.LoanProductDetailsDocumentRepository;
import com.bits.loanproposal.infrastructure.persistence.repository.repository.LoanProductDocumentRepository;
import com.bits.loanproposal.infrastructure.persistence.repository.repository.LoanProductPolicyDocumentRepository;
import com.bits.loanproposal.infrastructure.persistence.repository.repository.MemberDocumentRepository;
import com.bits.loanproposal.infrastructure.persistence.repository.repository.ProjectDocumentRepository;
import com.bits.loanproposal.infrastructure.persistence.repository.repository.ProjectPolicyDocumentRepository;
import com.bits.loanproposal.infrastructure.persistence.repository.repository.SchemeDocumentRepository;
import com.bits.loanproposal.infrastructure.persistence.repository.repository.VillageOrganisationDocumentRepository;
import org.springframework.stereotype.Component;

@Component
public class UpdateLoanProposalSourceDataProvider {

    private final SourceDataCoordinator coordinator;
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

    public UpdateLoanProposalSourceDataProvider(
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

    public SourceDataContext provide(UpdateLoanProposalCommand command, LoanProposal existingAggregate) {
        SourceDataCoordinator.SourceDataFetchBuilder builder = coordinator.builder(command.getTracerId());

        Long memberId = existingAggregate.getMemberId();
        Long branchId = existingAggregate.getBranchId();
        Long loanProductId = effective(command.getLoanProductId(), existingAggregate.getLoanProductId());
        Long loanProductDetailsId = effective(command.getLoanProductDetailsId(), existingAggregate.getLoanProductDetailsId());
        Long loanProductPolicyId = effective(command.getLoanProductPolicyId(), existingAggregate.getLoanProductPolicyId());
        Long schemeId = effective(command.getSchemeId(), existingAggregate.getSchemeId());
        Long projectId = existingAggregate.getProjectId();
        Long villageOrganisationId = existingAggregate.getVillageOrganisationId();
        Long insuranceProductId = effective(command.getInsuranceProductId(), existingAggregate.getInsuranceProductId());
        Long countryId = effective(command.getCountryId(), existingAggregate.getCountryId());
        Long bankId = effectiveBankId(command, existingAggregate);

        builder.add("member", memberRepository, memberId, "memberId",
                LocalizedMessage.builder().key("MEMBER_NOT_FOUND").build());
        builder.add("branch", branchRepository, branchId, "branchId",
                LocalizedMessage.builder().key("BRANCH_NOT_FOUND").build());

        if (loanProductId != null) {
            builder.add("loanProduct", loanProductRepository, loanProductId, "loanProductId",
                    LocalizedMessage.builder().key("LOAN_PRODUCT_NOT_FOUND").build());
        }
        if (loanProductDetailsId != null) {
            builder.add("loanProductDetails", loanProductDetailsRepository, loanProductDetailsId, "loanProductDetailsId",
                    LocalizedMessage.builder().key("LOAN_PRODUCT_DETAILS_NOT_FOUND").build());
        }
        if (loanProductPolicyId != null) {
            builder.add("loanProductPolicy", loanProductPolicyRepository, loanProductPolicyId, "loanProductPolicyId",
                    LocalizedMessage.builder().key("LOAN_PRODUCT_POLICY_NOT_FOUND").build());
        }
        if (schemeId != null) {
            builder.add("scheme", schemeRepository, schemeId, "schemeId",
                    LocalizedMessage.builder().key("SCHEME_NOT_FOUND").build());
        }
        if (projectId != null) {
            builder.add("project", projectRepository, projectId, "projectId",
                    LocalizedMessage.builder().key("PROJECT_NOT_FOUND").build());
            builder.add("projectPolicy", projectPolicyRepository, projectId, "projectId",
                    LocalizedMessage.builder().key("PROJECT_POLICY_NOT_FOUND").build());
        }
        if (villageOrganisationId != null) {
            builder.add("villageOrganisation", villageOrganisationRepository, villageOrganisationId, "voId",
                    LocalizedMessage.builder().key("VO_NOT_FOUND").build());
        }
        if (insuranceProductId != null) {
            builder.add("insuranceProduct", insuranceProductRepository, insuranceProductId, "insuranceProductId",
                    LocalizedMessage.builder().key("INSURANCE_PRODUCT_NOT_FOUND").build());
        }
        if (countryId != null) {
            builder.add("country", countryRepository, countryId, "countryId",
                    LocalizedMessage.builder().key("COUNTRY_NOT_FOUND").build());
        }
        if (bankId != null) {
            builder.add("bank", bankRepository, bankId, "bankId",
                    LocalizedMessage.builder().key("BANK_NOT_FOUND").build());
        }

        return builder.fetch(SourceDataValidationException::new);
    }

    private static Long effective(Long requested, Long current) {
        return requested != null ? requested : current;
    }

    private static Long effectiveBankId(UpdateLoanProposalCommand command, LoanProposal existingAggregate) {
        if (command.getModeOfPayment() != null && command.getModeOfPayment().bankId() != null) {
            return command.getModeOfPayment().bankId();
        }
        if (existingAggregate.getModeOfPayment() != null) {
            return existingAggregate.getModeOfPayment().bankId();
        }
        return null;
    }
}
