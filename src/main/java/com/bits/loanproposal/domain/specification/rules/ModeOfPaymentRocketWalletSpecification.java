package com.bits.loanproposal.domain.specification.rules;

import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.ddd.specification.rules.Specification;
import com.bits.loanproposal.domain.enums.AutoDebitCollectionSubType;
import com.bits.loanproposal.domain.enums.ModeOfPaymentSubType;
import com.bits.loanproposal.domain.specification.context.LoanProposalValidationContext;
import com.bits.loanproposal.domain.value.AutoDebitCollection;
import com.bits.loanproposal.domain.value.OtcModeOfPayment;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// DDD-REQ-022
public class ModeOfPaymentRocketWalletSpecification implements Specification<LoanProposalValidationContext> {

    @Override
    public Map<String, LocalizedMessage> validate(LoanProposalValidationContext context) {
        Map<String, LocalizedMessage> errors = new HashMap<>();
        if (context.member() == null) {
            return errors;
        }
        String memberWallet = context.member().getRocketWalletNumber();
        OtcModeOfPayment modeOfPayment = context.aggregate().getModeOfPayment();
        if (modeOfPayment != null && modeOfPayment.subType() == ModeOfPaymentSubType.ROCKET
                && !Objects.equals(modeOfPayment.rocketWalletNumber(), memberWallet)) {
            errors.put("modeOfPaymentDisbursement",
                    LocalizedMessage.builder().key("ROCKET_WALLET_MISMATCH").build());
        }
        AutoDebitCollection collection = context.aggregate().getAutoDebitCollection();
        if (collection != null && collection.subType() == AutoDebitCollectionSubType.ROCKET
                && !Objects.equals(collection.rocketWalletNumber(), memberWallet)) {
            errors.put("modeOfPaymentCollection",
                    LocalizedMessage.builder().key("ROCKET_WALLET_MISMATCH").build());
        }
        return errors;
    }
}
