package com.bits.loanproposal.domain.specification.rules;

import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.ddd.specification.rules.Specification;
import com.bits.loanproposal.application.dto.sourcedata.LoanProductDetails;
import com.bits.loanproposal.application.dto.sourcedata.LoanProductPolicy;
import com.bits.loanproposal.domain.aggregate.LoanProposal;
import com.bits.loanproposal.domain.specification.context.LoanProposalValidationContext;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// DDD-REQ-013 (doc name: LoanAmountGrantInstallmentSpecification)
public class LoanAmountSpecification implements Specification<LoanProposalValidationContext> {

    @Override
    public Map<String, LocalizedMessage> validate(LoanProposalValidationContext context) {
        Map<String, LocalizedMessage> errors = new HashMap<>();
        LoanProposal aggregate = context.aggregate();
        BigDecimal amount = aggregate.getProposedLoanAmount();
        if (amount == null || amount.signum() < 0) {
            errors.put("proposedLoanAmount",
                    LocalizedMessage.builder().key("LOAN_AMOUNT_INVALID").build());
        } else {
            LoanProductPolicy policy = context.loanProductPolicy();
            if (policy == null) {
                errors.put("loanProductPolicy",
                        LocalizedMessage.builder().key("LOAN_PRODUCT_POLICY_NOT_FOUND").build());
            } else if ((policy.getMinAmount() != null && amount.compareTo(policy.getMinAmount()) < 0)
                    || (policy.getMaxAmount() != null && amount.compareTo(policy.getMaxAmount()) > 0)) {
                errors.put("proposedLoanAmount", LocalizedMessage.builder()
                        .key("LOAN_AMOUNT_OUT_OF_POLICY_RANGE")
                        .args(new Object[]{policy.getMinAmount(), policy.getMaxAmount()})
                        .build());
            }
        }
        BigDecimal grant = aggregate.getProposedGrantAmount();
        if (grant != null && grant.signum() < 0) {
            errors.put("proposedGrantAmount",
                    LocalizedMessage.builder().key("GRANT_AMOUNT_INVALID").build());
        }
        if (aggregate.getInstallmentAmount() != null && aggregate.getApprovedInstallmentAmount() != null
                && aggregate.getInstallmentAmount().compareTo(aggregate.getApprovedInstallmentAmount()) != 0) {
            errors.put("installmentAmount",
                    LocalizedMessage.builder().key("INSTALLMENT_AMOUNT_WRONG").build());
        }
        LoanProductDetails details = context.loanProductDetails();
        if (details != null && !matchesProductDetails(aggregate, details)) {
            errors.put("installment",
                    LocalizedMessage.builder().key("INSTALLMENT_CONFIG_MISMATCH").build());
        }
        // ponytail: three REQ-013 sub-rules skipped — computeGrantAmount formula,
        // variable-installment config lookup, and isValidInterestRate are all undefined in the
        // doc and have no source data to check against; see docs/create-flow-gaps.md #1
        return errors;
    }

    private boolean matchesProductDetails(LoanProposal aggregate, LoanProductDetails details) {
        if (details.getInstallmentCount() != null
                && !Objects.equals(aggregate.getNumberOfInstallments(), details.getInstallmentCount())) {
            return false;
        }
        if (details.getDurationMonths() != null
                && !Objects.equals(aggregate.getProposalDurationInMonths(), details.getDurationMonths())) {
            return false;
        }
        if (details.getInterestRate() != null) {
            return aggregate.getInterestRate() != null
                    && aggregate.getInterestRate().compareTo(details.getInterestRate()) == 0;
        }
        return true;
    }
}
