package com.bits.loanproposal.domain.specification.rules;

import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.ddd.specification.rules.Specification;
import com.bits.loanproposal.domain.aggregate.LoanProposal;
import com.bits.loanproposal.domain.specification.context.LoanProposalValidationContext;
import com.bits.loanproposal.domain.value.OtcModeOfPayment;

import java.util.HashMap;
import java.util.Map;

// DDD-REQ-023
public class DigitalDisbursementSpecification implements Specification<LoanProposalValidationContext> {

    @Override
    public Map<String, LocalizedMessage> validate(LoanProposalValidationContext context) {
        Map<String, LocalizedMessage> errors = new HashMap<>();
        LoanProposal aggregate = context.aggregate();
        OtcModeOfPayment modeOfPayment = aggregate.getModeOfPayment();
        // ponytail: doc's isCentralDisbursementMode() is undefined; a non-null
        // digitalDisbursementModeId is the only central-disbursement marker in the data
        if (ProductTypes.is(context.loanProduct(), "MONEY_PLANT")
                && modeOfPayment != null && modeOfPayment.digitalDisbursementModeId() != null) {
            errors.put("modeOfPayment",
                    LocalizedMessage.builder().key("MONEY_PLANT_CENTRAL_DISBURSEMENT_INVALID").build());
        }
        if (Boolean.TRUE.equals(aggregate.getIsDigitalDisbursement()) && modeOfPayment == null) {
            errors.put("modeOfPayment",
                    LocalizedMessage.builder().key("MODE_OF_PAYMENT_REQUIRED_FOR_DIGITAL").build());
        }
        // ponytail: auto-debit and payment-channel branch/project mapping checks skipped — no
        // mapping source data exists; Amar Hishab premium-collection block skipped — the
        // premiumModeOfPaymentId value that means "Amar Hishab" is unknown (member snapshot
        // already carries hasAmarHishabAccount/amarHishabBalance for when it lands)
        if (aggregate.getPremiumAmount() != null && aggregate.getPremiumAmount().signum() < 0) {
            errors.put("premiumAmount",
                    LocalizedMessage.builder().key("PREMIUM_AMOUNT_NEGATIVE").build());
        }
        if (modeOfPayment != null && modeOfPayment.bankAccountNumber() != null) {
            if (context.bank() == null) {
                errors.put("bankAccount",
                        LocalizedMessage.builder().key("MEMBER_BANK_ACCOUNT_NOT_FOUND").build());
            } else if (!context.bank().accountBelongsToMember(aggregate.getMemberId())) {
                errors.put("bankAccount",
                        LocalizedMessage.builder().key("BANK_DOES_NOT_MATCH_MEMBER").build());
            }
        }
        return errors;
    }
}
