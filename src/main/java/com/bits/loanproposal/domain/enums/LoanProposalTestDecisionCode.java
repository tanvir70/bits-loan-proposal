package com.bits.loanproposal.domain.enums;

import com.bits.ddd.shared.persistence.converter.BitsEnum;

/** A value-backed enum persisted by its String code rather than its constant name. */
public enum LoanProposalTestDecisionCode implements BitsEnum<String> {
    REFER("RFR"),
    ACCEPT("ACC"),
    DECLINE("DEC");

    private final String value;

    LoanProposalTestDecisionCode(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
