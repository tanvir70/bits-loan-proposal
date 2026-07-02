package com.bits.loanproposal.presentation.controller.dto;

import java.math.BigDecimal;

public record FireInsuranceDetailsRequestDto(
    String businessName,
    String businessAddress,
    String businessPhone,
    String businessEmail,
    Long divisionId,
    Long districtId,
    Long thanaId,
    Long businessTypeId,
    Long constructionOfPremisesId,
    BigDecimal fireInsurancePremiumAmount,
    BigDecimal fireInsuranceInsuredAmount,
    Integer durationOfFireInsurance,
    String fireInsuranceProductName,
    BigDecimal bracCommissionAmount,
    BigDecimal memberCommissionAmount
) {}
