package com.bits.loanproposal.domain.specification.rules;

import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.ddd.specification.rules.Specification;
import com.bits.loanproposal.application.dto.sourcedata.LoanProduct;
import com.bits.loanproposal.application.dto.sourcedata.Member;
import com.bits.loanproposal.domain.aggregate.LoanProposal;
import com.bits.loanproposal.domain.specification.context.LoanProposalValidationContext;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// DDD-REQ-018
public class SpecialSavingsLienSpecification implements Specification<LoanProposalValidationContext> {

    @Override
    public Map<String, LocalizedMessage> validate(LoanProposalValidationContext context) {
        Map<String, LocalizedMessage> errors = new HashMap<>();
        LoanProposal aggregate = context.aggregate();
        Member member = context.member();
        List<String> accountIds = aggregate.getSpecialSavingsAccountIds();
        if (accountIds != null && !accountIds.isEmpty()) {
            if (!isLienOrMoneyPlant(context.loanProduct())) {
                errors.put("specialSavings",
                        LocalizedMessage.builder().key("SPECIAL_SAVINGS_NOT_APPLICABLE").build());
            }
            List<String> accountNumbers = aggregate.getSpecialSavingsAccountNumbers();
            if (member != null && accountNumbers != null) {
                for (String accountNumber : accountNumbers) {
                    if (member.existingLoanForAccount(accountNumber)) {
                        errors.put("specialSavings", LocalizedMessage.builder()
                                .key("MEMBER_HAS_LOAN_WITH_ACCOUNT")
                                .args(new Object[]{accountNumber})
                                .build());
                    }
                    if (!member.ownsSpecialSavingsAccount(accountNumber)) {
                        errors.put("specialSavings", LocalizedMessage.builder()
                                .key("SPECIAL_SAVINGS_ACCOUNT_MISMATCH")
                                .args(new Object[]{accountNumber})
                                .build());
                    }
                }
            }
        }
        if (isLien(context.loanProduct()) && member != null && context.projectPolicy() != null) {
            BigDecimal disbursed = member.getTotalDisbursedProttashaAmount() != null
                    ? member.getTotalDisbursedProttashaAmount() : BigDecimal.ZERO;
            BigDecimal proposed = aggregate.getProposedLoanAmount() != null
                    ? aggregate.getProposedLoanAmount() : BigDecimal.ZERO;
            BigDecimal maximum = context.projectPolicy().getMaxProttashaParallelAmount();
            if (maximum != null && disbursed.add(proposed).compareTo(maximum) > 0) {
                errors.put("proposedLoanAmount", LocalizedMessage.builder()
                        .key("PROTTASHA_PARALLEL_LIMIT_EXCEEDED")
                        .args(new Object[]{maximum.toString()})
                        .build());
            }
        }
        // ponytail: doc's AAM/AM vs ABM/BM approver-role checks skipped — only loanApproverId
        // is available and there is no approver-role source data to resolve it against
        return errors;
    }

    // ponytail: doc's isLienProduct/isLienOrMoneyPlantProduct are undefined; matched on
    // loanProductType strings LIEN / MONEY_PLANT, verify against legacy product data
    private boolean isLien(LoanProduct product) {
        return product != null && "LIEN".equalsIgnoreCase(product.getLoanProductType());
    }

    private boolean isLienOrMoneyPlant(LoanProduct product) {
        return isLien(product)
                || (product != null && "MONEY_PLANT".equalsIgnoreCase(product.getLoanProductType()));
    }
}
