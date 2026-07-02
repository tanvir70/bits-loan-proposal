package com.bits.loanproposal.presentation.controller.dto;

import java.time.LocalDate;

public record CoBorrowerRequestDto(
    String id,
    String name,
    Long relationshipId,
    String nationalId,
    LocalDate dateOfBirth
) {}
