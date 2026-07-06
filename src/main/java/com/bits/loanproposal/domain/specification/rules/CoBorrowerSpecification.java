package com.bits.loanproposal.domain.specification.rules;

import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.ddd.specification.rules.Specification;
import com.bits.loanproposal.domain.specification.context.LoanProposalValidationContext;

import java.util.HashMap;
import java.util.Map;

// DDD-REQ-015
public class CoBorrowerSpecification implements Specification<LoanProposalValidationContext> {

    @Override
    public Map<String, LocalizedMessage> validate(LoanProposalValidationContext context) {
        Map<String, LocalizedMessage> errors = new HashMap<>();
        if (context.aggregate().getCoBorrower() != null
                && context.loanProduct() != null
                && !context.loanProduct().isRequiresCoBorrower()) {
            errors.put("coBorrower",
                    LocalizedMessage.builder().key("CO_BORROWER_NOT_APPLICABLE").build());
        }
        return errors;
    }
}
