package com.bits.loanproposal.domain.specification.rules;

import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.ddd.specification.rules.Specification;
import com.bits.loanproposal.application.dto.sourcedata.LoanProduct;
import com.bits.loanproposal.domain.aggregate.LoanProposal;
import com.bits.loanproposal.domain.specification.context.LoanProposalValidationContext;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

// DDD-REQ-011
public class LoanProductPolicySpecification implements Specification<LoanProposalValidationContext> {

    @Override
    public Map<String, LocalizedMessage> validate(LoanProposalValidationContext context) {
        Map<String, LocalizedMessage> errors = new HashMap<>();
        LoanProposal aggregate = context.aggregate();
        LocalDate businessDate = aggregate.getApplicationDate();
        LoanProduct loanProduct = context.loanProduct();
        if (loanProduct == null) {
            errors.put("loanProduct", LocalizedMessage.builder().key("LOAN_PRODUCT_NOT_FOUND").build());
            return errors;
        }
        if (context.loanProductDetails() == null) {
            errors.put("loanProductDetails",
                    LocalizedMessage.builder().key("LOAN_PRODUCT_DETAILS_NOT_FOUND").build());
        } else if (!context.loanProductDetails().isActiveOn(businessDate)) {
            errors.put("loanProductDetails",
                    LocalizedMessage.builder().key("LOAN_PRODUCT_DETAILS_EXPIRED").build());
        }
        if (context.loanProductPolicy() == null) {
            errors.put("loanProductPolicy",
                    LocalizedMessage.builder().key("LOAN_PRODUCT_POLICY_NOT_FOUND").build());
        } else if (!context.loanProductPolicy().isActiveOn(businessDate)) {
            errors.put("loanProductPolicy",
                    LocalizedMessage.builder().key("LOAN_PRODUCT_POLICY_EXPIRED").build());
        }
        if (!loanProduct.isActiveOn(businessDate)) {
            errors.put("loanProduct", LocalizedMessage.builder().key("LOAN_PRODUCT_EXPIRED").build());
        }
        // doc's "officeId" has no aggregate field; branch is the office in this context
        if (!loanProduct.isMappedWith(context.project(), aggregate.getBranchId(),
                aggregate.getMemberClassificationId(), aggregate.getFrequencyId())) {
            errors.put("loanProduct", LocalizedMessage.builder().key("LOAN_PRODUCT_MAPPING_INVALID").build());
        }
        if (!loanProduct.isMappedWithMemberCategory(aggregate.getMemberClassificationId())) {
            errors.put("loanProductMemberCategory",
                    LocalizedMessage.builder().key("LOAN_PRODUCT_MEMBER_CATEGORY_MISMATCH").build());
        }
        return errors;
    }
}
