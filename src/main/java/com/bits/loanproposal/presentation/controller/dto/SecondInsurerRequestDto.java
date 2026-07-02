package com.bits.loanproposal.presentation.controller.dto;

import java.time.LocalDate;

public record SecondInsurerRequestDto(
    String id,
    String name,
    Long genderId,
    Long relationshipId,
    LocalDate dateOfBirth,
    String nationalId
) {}
