package com.bits.loanproposal.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanProposalCollateral {
    private String id;
    private String collateralType;
    private double estimatedValue;
}
