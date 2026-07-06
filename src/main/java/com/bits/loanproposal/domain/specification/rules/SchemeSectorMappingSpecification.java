package com.bits.loanproposal.domain.specification.rules;

import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.ddd.specification.rules.Specification;
import com.bits.loanproposal.domain.specification.context.LoanProposalValidationContext;

import java.util.HashMap;
import java.util.Map;

// DDD-REQ-028
public class SchemeSectorMappingSpecification implements Specification<LoanProposalValidationContext> {

    @Override
    public Map<String, LocalizedMessage> validate(LoanProposalValidationContext context) {
        Map<String, LocalizedMessage> errors = new HashMap<>();
        // ponytail: doc's schemeExistsForLoanProductAndSector() is undefined; an absent scheme
        // lookup is the closest observable signal for "no scheme mapped"
        if (context.scheme() == null) {
            errors.put("scheme", LocalizedMessage.builder().key("SCHEME_LIST_EMPTY").build());
            return errors;
        }
        if (!context.scheme().isMappedToLoanProduct(context.loanProduct())) {
            errors.put("scheme",
                    LocalizedMessage.builder().key("SCHEME_NOT_MAPPED_TO_LOAN_PRODUCT").build());
        }
        // ponytail: doc checks sector against loan product OR scheme; LoanProduct carries no
        // sector mappings, so only the scheme side is checkable
        if (context.aggregate().getSectorId() != null
                && !context.scheme().isMappedToSector(context.aggregate().getSectorId())) {
            errors.put("sector",
                    LocalizedMessage.builder().key("SECTOR_NOT_MAPPED_TO_LOAN_PRODUCT").build());
        }
        return errors;
    }
}
