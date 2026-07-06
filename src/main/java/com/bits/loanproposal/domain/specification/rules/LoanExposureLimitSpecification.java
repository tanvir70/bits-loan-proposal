package com.bits.loanproposal.domain.specification.rules;

import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.ddd.specification.rules.Specification;
import com.bits.loanproposal.domain.specification.context.LoanProposalValidationContext;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

// DDD-REQ-014
public class LoanExposureLimitSpecification implements Specification<LoanProposalValidationContext> {

    @Override
    public Map<String, LocalizedMessage> validate(LoanProposalValidationContext context) {
        Map<String, LocalizedMessage> errors = new HashMap<>();
        if (context.projectPolicy() == null || !context.projectPolicy().isEnforcesLoanExposureLimit()) {
            return errors;
        }
        // ponytail: doc reads the limit off projectPolicy, but in our source data the
        // office-and-project exposure limit lives on LoanProductPolicy
        BigDecimal limit = context.loanProductPolicy() != null
                ? context.loanProductPolicy().getOfficeAndProjectExposureLimit() : null;
        BigDecimal amount = context.aggregate().getProposedLoanAmount();
        if (limit != null && amount != null && amount.compareTo(limit) > 0) {
            errors.put("proposedLoanAmount",
                    LocalizedMessage.builder().key("LOAN_EXPOSURE_LIMIT_EXCEEDED").build());
        }
        return errors;
    }
}
