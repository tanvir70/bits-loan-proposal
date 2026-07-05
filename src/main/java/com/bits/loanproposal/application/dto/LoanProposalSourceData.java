package com.bits.loanproposal.application.dto;

import com.bits.loanproposal.application.dto.sourcedata.*;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoanProposalSourceData {
    private final Member member;
    private final LoanProduct loanProduct;
    private final LoanProductDetails loanProductDetails;
    private final LoanProductPolicy loanProductPolicy;
    private final Scheme scheme;
    private final Project project;
    private final ProjectPolicy projectPolicy;
    private final Branch branch;
    private final VillageOrganisation villageOrganisation;
    private final InsuranceProduct insuranceProduct;
    private final Country country;
    private final Bank bank;
}
