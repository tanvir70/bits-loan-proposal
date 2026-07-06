package com.bits.loanproposal.domain.specification.rules;

import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.ddd.specification.rules.Specification;
import com.bits.loanproposal.application.dto.sourcedata.Member;
import com.bits.loanproposal.domain.aggregate.LoanProposal;
import com.bits.loanproposal.domain.enums.LoanProposalType;
import com.bits.loanproposal.domain.specification.context.LoanProposalValidationContext;

import java.util.HashMap;
import java.util.Map;

// DDD-REQ-020
public class ParallelCoExistingLoanSpecification implements Specification<LoanProposalValidationContext> {

    @Override
    public Map<String, LocalizedMessage> validate(LoanProposalValidationContext context) {
        Map<String, LocalizedMessage> errors = new HashMap<>();
        Member member = context.member();
        if (member == null) {
            return errors;
        }
        LoanProposal aggregate = context.aggregate();
        if (context.loanProduct() != null && !context.loanProduct().isAllowsParallelLoans()
                && member.isHasExistingLoan()) {
            errors.put("parallelLoan", LocalizedMessage.builder().key("PARALLEL_LOAN_NOT_ALLOWED").build());
        }
        if (ProductTypes.is(context.loanProduct(), "REMITTANCE") && member.isHasActiveGeneralLoan()) {
            errors.put("parallelLoan", LocalizedMessage.builder().key("PARALLEL_LOAN_NOT_ALLOWED").build());
        }
        if (ProductTypes.is(context.loanProduct(), "GENERAL") && member.isHasActiveRemittanceLoan()) {
            errors.put("parallelLoan", LocalizedMessage.builder().key("PARALLEL_LOAN_NOT_ALLOWED").build());
        }
        // ponytail: doc gates on isUPGProject(project) but Project has no type field;
        // the aggregate's LoanProposalType.UPG is stronger data for the same intent
        if (aggregate.getLoanProposalType() == LoanProposalType.UPG) {
            if (!member.isHasPriorTUPLoan()) {
                errors.put("upgFirstLoan",
                        LocalizedMessage.builder().key("UPG_MUST_TAKE_GENERAL_LOAN_FIRST").build());
            }
            if (member.hasActiveNonClosedLoanOfSameProduct(aggregate.getLoanProductId())) {
                errors.put("parallelLoan",
                        LocalizedMessage.builder().key("PARALLEL_LOAN_NOT_ALLOWED").build());
            }
        }
        return errors;
    }
}
