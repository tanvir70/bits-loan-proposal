package com.bits.loanproposal.domain.value;

public record LoanAmount(double value, String currency) {
    public LoanAmount {
        if (value < 0) {
            throw new IllegalArgumentException("Loan amount cannot be negative");
        }
    }
}
