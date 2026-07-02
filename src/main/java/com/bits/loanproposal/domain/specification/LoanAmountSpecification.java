package com.bits.loanproposal.domain.specification;

public class LoanAmountSpecification {
    public boolean isSatisfiedBy(double amount) {
        return amount >= 1000.0 && amount <= 500000.0;
    }
}
