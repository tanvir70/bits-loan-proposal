package com.bits.loanproposal.domain.enums;

import com.bits.ddd.shared.persistence.converter.BitsEnum;

public enum ModeOfPaymentSubType implements BitsEnum<Integer> {
    CHEQUE(1),
    TT(2),
    BEFTN(3),
    ROCKET(4),
    BKASH(5),
    ONLINE(6),
    CASH_DEPOSIT(7),
    RTGS(8),
    FUND_TRANSFER(9),
    SCB(10),
    HSBC(11);

    private final Integer value;

    ModeOfPaymentSubType(Integer value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
