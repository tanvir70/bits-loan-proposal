package com.bits.loanproposal.application.dto;

import com.bits.loanproposal.application.dto.sourcedata.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanProposalSourceData {
    private Member member;
    private LoanProduct loanProduct;
    private LoanProductDetails loanProductDetails;
    private LoanProductPolicy loanProductPolicy;
    private Scheme scheme;
    private Project project;
    private ProjectPolicy projectPolicy;
    private Branch branch;
    private VillageOrganisation villageOrganisation;
    private InsuranceProduct insuranceProduct;
    private Country country;
    private Bank bank;
}
