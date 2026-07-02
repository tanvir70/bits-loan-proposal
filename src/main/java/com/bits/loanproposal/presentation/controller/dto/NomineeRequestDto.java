package com.bits.loanproposal.presentation.controller.dto;

import java.util.List;

public record NomineeRequestDto(
    String id,
    String name,
    Long relationshipId,
    Double sharePercentage,
    List<String> insuranceTypes
) {}
