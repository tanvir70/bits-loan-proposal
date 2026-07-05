package com.bits.loanproposal.domain.specification.rules;

import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.ddd.specification.rules.Specification;
import com.bits.loanproposal.application.dto.sourcedata.LoanProductPolicy;
import com.bits.loanproposal.domain.specification.context.LoanProposalValidationContext;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class LoanAmountSpecification implements Specification<LoanProposalValidationContext> {

    @Override
    public Map<String, LocalizedMessage> validate(LoanProposalValidationContext context) {
        Map<String, LocalizedMessage> errors = new HashMap<>();
        BigDecimal amount = context.aggregate().getProposedLoanAmount();
        if (amount == null || amount.signum() < 0) {
            errors.put("proposedLoanAmount",
                    LocalizedMessage.builder().key("LOAN_AMOUNT_INVALID").build());
            return errors;
        }
        LoanProductPolicy policy = context.loanProductPolicy();
        if (policy == null) {
            errors.put("loanProductPolicy",
                    LocalizedMessage.builder().key("LOAN_PRODUCT_POLICY_NOT_FOUND").build());
            return errors;
        }
        if ((policy.getMinAmount() != null && amount.compareTo(policy.getMinAmount()) < 0)
                || (policy.getMaxAmount() != null && amount.compareTo(policy.getMaxAmount()) > 0)) {
            errors.put("proposedLoanAmount", LocalizedMessage.builder()
                    .key("LOAN_AMOUNT_OUT_OF_POLICY_RANGE")
                    .args(new Object[]{policy.getMinAmount(), policy.getMaxAmount()})
                    .build());
        }
        BigDecimal grant = context.aggregate().getProposedGrantAmount();
        if (grant != null && grant.signum() < 0) {
            errors.put("proposedGrantAmount",
                    LocalizedMessage.builder().key("GRANT_AMOUNT_INVALID").build());
        }
        return errors;
    }
}
