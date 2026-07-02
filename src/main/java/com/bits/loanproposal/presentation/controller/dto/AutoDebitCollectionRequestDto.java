package com.bits.loanproposal.presentation.controller.dto;

import com.bits.loanproposal.domain.enums.AutoDebitCollectionSubType;
import java.util.List;

public record AutoDebitCollectionRequestDto(
    AutoDebitCollectionSubType subType,
    Long memberBankManagementLinkId,
    List<String> chequeNumbers,
    List<String> micrNumbers,
    String rocketWalletNumber
) {}
