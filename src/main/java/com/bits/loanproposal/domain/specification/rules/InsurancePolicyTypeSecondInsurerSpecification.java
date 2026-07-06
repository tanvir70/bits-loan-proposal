package com.bits.loanproposal.domain.specification.rules;

import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.ddd.specification.rules.Specification;
import com.bits.loanproposal.domain.aggregate.LoanProposal;
import com.bits.loanproposal.domain.entity.SecondInsurer;
import com.bits.loanproposal.domain.specification.context.LoanProposalValidationContext;

import java.util.HashMap;
import java.util.Map;

// DDD-REQ-016
public class InsurancePolicyTypeSecondInsurerSpecification implements Specification<LoanProposalValidationContext> {

    private static final Long SINGLE = 1L;
    private static final Long DOUBLE = 2L;

    @Override
    public Map<String, LocalizedMessage> validate(LoanProposalValidationContext context) {
        Map<String, LocalizedMessage> errors = new HashMap<>();
        LoanProposal aggregate = context.aggregate();
        if (SINGLE.equals(aggregate.getPolicyTypeId()) && aggregate.getSecondInsurer() != null) {
            errors.put("secondInsurer",
                    LocalizedMessage.builder().key("SINGLE_POLICY_NO_SECOND_INSURER").build());
        }
        if (Boolean.TRUE.equals(aggregate.getMicroInsurance())) {
            validateMicroInsurance(context, errors, aggregate);
        } else if (aggregate.getSecondInsurer() != null) {
            errors.put("secondInsurer",
                    LocalizedMessage.builder().key("SECOND_INSURER_NOT_REQUIRED").build());
        }
        if (context.insuranceProduct() != null && aggregate.getProposedLoanAmount() != null
                && !context.insuranceProduct().coversAmount(aggregate.getProposedLoanAmount())) {
            errors.put("proposedLoanAmount",
                    LocalizedMessage.builder().key("LOAN_AMOUNT_NOT_ALLOWED_FOR_INSURANCE").build());
        }
        return errors;
    }

    private void validateMicroInsurance(LoanProposalValidationContext context,
                                        Map<String, LocalizedMessage> errors,
                                        LoanProposal aggregate) {
        Long policyTypeId = aggregate.getPolicyTypeId();
        if (policyTypeId == null) {
            errors.put("policyTypeId",
                    LocalizedMessage.builder().key("POLICY_TYPE_NULL_FOR_MICRO_INSURANCE").build());
        } else if (!SINGLE.equals(policyTypeId) && !DOUBLE.equals(policyTypeId)) {
            errors.put("policyTypeId", LocalizedMessage.builder().key("POLICY_TYPE_INVALID").build());
        }
        if (aggregate.getInsuranceProductId() == null) {
            errors.put("insuranceProductId",
                    LocalizedMessage.builder().key("INSURANCE_PRODUCT_NULL_FOR_MICRO_INSURANCE").build());
        } else if (context.insuranceProduct() == null) {
            errors.put("insuranceProductId",
                    LocalizedMessage.builder().key("INSURANCE_PRODUCT_NOT_FOUND").build());
        }
        if (!DOUBLE.equals(policyTypeId)) {
            return;
        }
        SecondInsurer insurer = aggregate.getSecondInsurer();
        if (insurer == null) {
            errors.put("secondInsurer",
                    LocalizedMessage.builder().key("SECOND_INSURER_MANDATORY_DOUBLE").build());
            return;
        }
        if (insurer.getGenderId() == null) {
            errors.put("insurerGender", LocalizedMessage.builder().key("INSURER_GENDER_REQUIRED").build());
        }
        if (insurer.getRelationshipId() == null) {
            errors.put("insurerRelationship",
                    LocalizedMessage.builder().key("INSURER_RELATIONSHIP_REQUIRED").build());
        }
        if (context.member() != null && insurer.sameIdentityAs(context.member().getNationalId())) {
            errors.put("secondInsurer",
                    LocalizedMessage.builder().key("INSURER_IDENTITY_DUPLICATE").build());
        }
        // ponytail: doc exempts the spouse from the engagement checks and validates
        // relationship-vs-marital-status and gender-vs-relationship combos, but member
        // gender/marital status and the relationshipId->SPOUSE mapping are not in source data;
        // engagement checks applied unconditionally, combo checks skipped until that data lands
        if (insurer.isEngagedWithOtherLoans()) {
            errors.put("secondInsurer", LocalizedMessage.builder().key("INSURER_ENGAGED_LOANS").build());
        }
        if (insurer.isEngagedWithOtherInsurance()) {
            errors.put("secondInsurer", LocalizedMessage.builder().key("INSURER_ENGAGED_INSURANCE").build());
        }
    }
}
