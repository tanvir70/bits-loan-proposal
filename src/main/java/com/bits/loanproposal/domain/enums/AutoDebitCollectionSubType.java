package com.bits.loanproposal.domain.enums;

import com.bits.ddd.shared.persistence.converter.BitsEnum;

public enum AutoDebitCollectionSubType implements BitsEnum<Integer> {
    ROCKET(1),
    DDI(2),
    BKASH(3),
    OTHER(4);

    private final Integer value;

    AutoDebitCollectionSubType(Integer value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
