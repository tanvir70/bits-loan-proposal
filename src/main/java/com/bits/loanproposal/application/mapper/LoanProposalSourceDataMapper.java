package com.bits.loanproposal.application.mapper;

import com.bits.ddd.application.service.SourceDataContext;
import com.bits.loanproposal.application.dto.LoanProposalSourceData;
import com.bits.loanproposal.infrastructure.persistence.document.sourcedata.*;

public final class LoanProposalSourceDataMapper {

    private LoanProposalSourceDataMapper() {}

    public static LoanProposalSourceData toSourceData(SourceDataContext context) {
        MemberDocument memberDoc = context.get("member", MemberDocument.class);
        LoanProductDocument loanProductDoc = context.get("loanProduct", LoanProductDocument.class);
        LoanProductDetailsDocument detailsDoc = context.get("loanProductDetails", LoanProductDetailsDocument.class);
        LoanProductPolicyDocument policyDoc = context.get("loanProductPolicy", LoanProductPolicyDocument.class);
        SchemeDocument schemeDoc = context.get("scheme", SchemeDocument.class);
        ProjectDocument projectDoc = context.get("project", ProjectDocument.class);
        ProjectPolicyDocument projectPolicyDoc = context.get("projectPolicy", ProjectPolicyDocument.class);
        BranchDocument branchDoc = context.get("branch", BranchDocument.class);
        VillageOrganisationDocument voDoc = context.get("villageOrganisation", VillageOrganisationDocument.class);
        InsuranceProductDocument insuranceDoc = context.get("insuranceProduct", InsuranceProductDocument.class);
        CountryDocument countryDoc = context.get("country", CountryDocument.class);
        BankDocument bankDoc = context.get("bank", BankDocument.class);

        return LoanProposalSourceData.builder()
                .member(memberDoc != null ? memberDoc.toModel() : null)
                .loanProduct(loanProductDoc != null ? loanProductDoc.toModel() : null)
                .loanProductDetails(detailsDoc != null ? detailsDoc.toModel() : null)
                .loanProductPolicy(policyDoc != null ? policyDoc.toModel() : null)
                .scheme(schemeDoc != null ? schemeDoc.toModel() : null)
                .project(projectDoc != null ? projectDoc.toModel() : null)
                .projectPolicy(projectPolicyDoc != null ? projectPolicyDoc.toModel() : null)
                .branch(branchDoc != null ? branchDoc.toModel() : null)
                .villageOrganisation(voDoc != null ? voDoc.toModel() : null)
                .insuranceProduct(insuranceDoc != null ? insuranceDoc.toModel() : null)
                .country(countryDoc != null ? countryDoc.toModel() : null)
                .bank(bankDoc != null ? bankDoc.toModel() : null)
                .build();
    }
}
