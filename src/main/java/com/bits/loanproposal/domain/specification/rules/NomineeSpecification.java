package com.bits.loanproposal.domain.specification.rules;

import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.ddd.specification.rules.Specification;
import com.bits.loanproposal.domain.specification.context.LoanProposalValidationContext;

import java.util.HashMap;
import java.util.Map;

// DDD-REQ-017
public class NomineeSpecification implements Specification<LoanProposalValidationContext> {

    // ponytail: DCS_MAX_NOMINEES never defined in the doc; 3 per its CSI/fire-insurance nominee limit
    private static final int DCS_MAX_NOMINEES = 3;

    @Override
    public Map<String, LocalizedMessage> validate(LoanProposalValidationContext context) {
        Map<String, LocalizedMessage> errors = new HashMap<>();
        if (context.aggregate().getNominees() != null
                && context.aggregate().getNominees().size() > DCS_MAX_NOMINEES) {
            errors.put("nominees", LocalizedMessage.builder().key("NOMINEE_LIMIT_EXCEEDED").build());
        }
        return errors;
    }
}
