package com.bits.loanproposal.application.dto;

import com.bits.loanproposal.application.dto.sourcedata.ClientInfo;
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
    private ClientInfo clientInfo;
}
