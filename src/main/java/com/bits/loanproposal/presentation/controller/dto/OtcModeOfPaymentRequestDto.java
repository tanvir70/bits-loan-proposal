package com.bits.loanproposal.presentation.controller.dto;

import com.bits.loanproposal.domain.enums.ModeOfPaymentSubType;
import java.time.LocalDate;

public record OtcModeOfPaymentRequestDto(
    Long modeOfPaymentId,
    ModeOfPaymentSubType subType,
    String bankAccountNumber,
    String bankRoutingNumber,
    Long bankId,
    Long bankBranchId,
    String paymentSubTypeNumber,
    LocalDate paymentSubTypeDate,
    String bkashWalletNumber,
    String rocketWalletNumber,
    Long premiumModeOfPaymentId,
    Long digitalDisbursementModeId
) {}
