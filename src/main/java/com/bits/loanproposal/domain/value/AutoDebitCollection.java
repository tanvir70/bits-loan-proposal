package com.bits.loanproposal.domain.value;

import com.bits.loanproposal.domain.enums.AutoDebitCollectionSubType;
import java.util.List;

public record AutoDebitCollection(
    AutoDebitCollectionSubType subType,
    Long memberBankManagementLinkId,
    List<String> chequeNumbers,
    List<String> micrNumbers,
    String rocketWalletNumber
) {}
