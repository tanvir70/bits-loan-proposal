package com.bits.loanproposal.domain.enums;

import com.bits.ddd.shared.persistence.converter.BitsEnum;

/** A value-backed enum persisted by its numeric value. */
public enum LoanProposalTestPriority implements BitsEnum<Integer> {
    STANDARD(10),
    PRIORITY(20),
    URGENT(30);

    private final Integer value;

    LoanProposalTestPriority(Integer value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
