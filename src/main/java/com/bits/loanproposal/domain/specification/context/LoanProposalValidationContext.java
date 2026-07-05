package com.bits.loanproposal.domain.specification.context;

import com.bits.ddd.specification.context.ValidationContext;
import com.bits.loanproposal.application.dto.sourcedata.*;
import com.bits.loanproposal.domain.aggregate.LoanProposal;

public record LoanProposalValidationContext(
        Member member,
        LoanProduct loanProduct,
        LoanProductDetails loanProductDetails,
        LoanProductPolicy loanProductPolicy,
        Scheme scheme,
        Project project,
        ProjectPolicy projectPolicy,
        Branch branch,
        VillageOrganisation villageOrganisation,
        InsuranceProduct insuranceProduct,
        Country country,
        Bank bank,
        LoanProposal aggregate
) implements ValidationContext {
}
