package com.bits.loanproposal.presentation.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreateLoanProposalRequestDto(
    @NotBlank(message = "Applicant name is required")
    String applicantName,

    @Positive(message = "Amount must be positive")
    double amount
) {}
