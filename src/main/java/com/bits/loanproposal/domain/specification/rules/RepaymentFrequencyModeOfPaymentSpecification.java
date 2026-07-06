package com.bits.loanproposal.domain.specification.rules;

import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.ddd.specification.rules.Specification;
import com.bits.loanproposal.domain.enums.ModeOfPaymentSubType;
import com.bits.loanproposal.domain.specification.context.LoanProposalValidationContext;
import com.bits.loanproposal.domain.value.OtcModeOfPayment;

import java.util.HashMap;
import java.util.Map;

// DDD-REQ-012
public class RepaymentFrequencyModeOfPaymentSpecification implements Specification<LoanProposalValidationContext> {

    @Override
    public Map<String, LocalizedMessage> validate(LoanProposalValidationContext context) {
        Map<String, LocalizedMessage> errors = new HashMap<>();
        Long frequencyId = context.aggregate().getFrequencyId();
        // ponytail: doc's isRecognisedFrequency() is undefined; 1..10 range per the ">10 invalid" rule
        if (frequencyId == null || frequencyId < 1 || frequencyId > 10) {
            errors.put("frequency", LocalizedMessage.builder().key("LOAN_FREQUENCY_NOT_FOUND").build());
        }
        OtcModeOfPayment modeOfPayment = context.aggregate().getModeOfPayment();
        if (modeOfPayment != null) {
            // ponytail: subType is an enum, so any recognised value is valid; null means unmapped/invalid input
            if (modeOfPayment.subType() == null) {
                errors.put("modeOfPayment",
                        LocalizedMessage.builder().key("INVALID_MODE_OF_PAYMENT_SUBTYPE").build());
            }
            if (modeOfPayment.subType() == ModeOfPaymentSubType.ROCKET
                    && context.member() != null
                    && context.member().getRocketWalletNumber() == null) {
                errors.put("rocketWallet",
                        LocalizedMessage.builder().key("MEMBER_NO_ROCKET_WALLET").build());
            }
        }
        return errors;
    }
}
