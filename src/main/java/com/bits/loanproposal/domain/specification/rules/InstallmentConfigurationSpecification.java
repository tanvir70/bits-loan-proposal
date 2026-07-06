package com.bits.loanproposal.domain.specification.rules;

import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.ddd.specification.rules.Specification;
import com.bits.loanproposal.domain.entity.SecondInsurer;
import com.bits.loanproposal.domain.specification.context.LoanProposalValidationContext;

import java.util.HashMap;
import java.util.Map;

// DDD-REQ-021
public class InstallmentConfigurationSpecification implements Specification<LoanProposalValidationContext> {

    @Override
    public Map<String, LocalizedMessage> validate(LoanProposalValidationContext context) {
        Map<String, LocalizedMessage> errors = new HashMap<>();
        // ponytail: doc's projectInstallmentConfigExists() check skipped — no project
        // installment-configuration entity exists among the source data lookups
        if (context.loanProductDetails() == null) {
            // key differs from LoanProductPolicySpecification's "loanProductDetails" so the
            // merged error map keeps both messages
            errors.put("installmentDetails",
                    LocalizedMessage.builder().key("INSTALLMENT_CALC_DETAILS_MISSING").build());
        }
        SecondInsurer insurer = context.aggregate().getSecondInsurer();
        // ponytail: doc scopes this to the spouse (insurer.isSpouseOf(member)) but there is no
        // relationshipId->SPOUSE mapping in source data; checked for any second insurer instead
        // (inert today: the mapper hardcodes hasOtherLoanAccounts to false)
        if (insurer != null && insurer.isHasOtherLoanAccounts()) {
            errors.put("secondInsurerAccounts",
                    LocalizedMessage.builder().key("INSURER_SPOUSE_ENGAGED_LOAN_ACCOUNTS").build());
        }
        return errors;
    }
}
