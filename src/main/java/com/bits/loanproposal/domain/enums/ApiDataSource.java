package com.bits.loanproposal.domain.enums;

import com.bits.ddd.shared.persistence.converter.BitsEnum;

public enum ApiDataSource implements BitsEnum<Integer> {
    OTC(1),
    DCS(2);

    private final Integer value;

    ApiDataSource(Integer value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
