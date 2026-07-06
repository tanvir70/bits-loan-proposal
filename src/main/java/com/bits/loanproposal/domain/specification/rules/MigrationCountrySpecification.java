package com.bits.loanproposal.domain.specification.rules;

import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.ddd.specification.rules.Specification;
import com.bits.loanproposal.domain.specification.context.LoanProposalValidationContext;

import java.util.HashMap;
import java.util.Map;

// DDD-REQ-024
public class MigrationCountrySpecification implements Specification<LoanProposalValidationContext> {

    @Override
    public Map<String, LocalizedMessage> validate(LoanProposalValidationContext context) {
        Map<String, LocalizedMessage> errors = new HashMap<>();
        if (!ProductTypes.is(context.loanProduct(), "MIGRATION")) {
            return errors;
        }
        if (context.aggregate().getCountryId() == null) {
            errors.put("country",
                    LocalizedMessage.builder().key("MIGRATION_COUNTRY_MANDATORY").build());
        } else if (context.country() == null || !context.country().isConfiguredForMigration()) {
            errors.put("country",
                    LocalizedMessage.builder().key("MIGRATION_COUNTRY_NOT_CONFIGURED").build());
        }
        return errors;
    }
}
