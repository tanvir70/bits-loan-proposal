package com.bits.loanproposal.domain.enums;

import com.bits.ddd.shared.persistence.converter.BitsEnum;

public enum LoanProposalType implements BitsEnum<Integer> {
    NORMAL_LOAN(1),
    RF(2),
    RS(3),
    GOOD_LOAN(4),
    UPG(5);

    private final Integer value;

    LoanProposalType(Integer value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
