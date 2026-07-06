package com.bits.loanproposal.domain.specification.rules;

import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.ddd.specification.rules.Specification;
import com.bits.loanproposal.application.dto.sourcedata.Member;
import com.bits.loanproposal.domain.aggregate.LoanProposal;
import com.bits.loanproposal.domain.specification.context.LoanProposalValidationContext;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// DDD-REQ-027
public class MoneyPlantSpecification implements Specification<LoanProposalValidationContext> {

    @Override
    public Map<String, LocalizedMessage> validate(LoanProposalValidationContext context) {
        Map<String, LocalizedMessage> errors = new HashMap<>();
        if (!ProductTypes.is(context.loanProduct(), "MONEY_PLANT")) {
            return errors;
        }
        LoanProposal aggregate = context.aggregate();
        if (aggregate.getProposalDurationInMonths() == null) {
            errors.put("duration", LocalizedMessage.builder().key("MONEY_PLANT_DURATION_NULL").build());
        }
        if (aggregate.getProposedLoanAmount() == null) {
            errors.put("proposedLoanAmount",
                    LocalizedMessage.builder().key("MONEY_PLANT_AMOUNT_NULL").build());
        }
        Member member = context.member();
        if (member != null && member.getDateOfBirth() != null) {
            LocalDate applicationDate = aggregate.getApplicationDate() != null
                    ? aggregate.getApplicationDate() : LocalDate.now();
            Period age = Period.between(member.getDateOfBirth(), applicationDate);
            boolean golden = Boolean.TRUE.equals(member.getIsGolden());
            if (age.getYears() < 18 || age.getYears() >= 80 || (age.getYears() >= 70 && !golden)) {
                errors.put("memberAge", LocalizedMessage.builder()
                        .key("MEMBER_AGE_INELIGIBLE")
                        .args(new Object[]{age.getYears(), age.getMonths(), age.getDays()})
                        .build());
            }
        }
        List<String> accountNumbers = aggregate.getSpecialSavingsAccountNumbers();
        if (accountNumbers == null || accountNumbers.isEmpty()) {
            errors.put("specialSavings",
                    LocalizedMessage.builder().key("SPECIAL_SAVINGS_NOT_FOUND").build());
        } else if (member != null) {
            // ponytail: member snapshot doesn't distinguish money-plant accounts from other
            // special savings, and carries no per-account loan portion — ownership is the only
            // implementable check; the amount-vs-loan-portion match is skipped
            for (String accountNumber : accountNumbers) {
                if (!member.ownsSpecialSavingsAccount(accountNumber)) {
                    errors.put("specialSavings",
                            LocalizedMessage.builder().key("MONEY_PLANT_ACCOUNT_INVALID").build());
                }
            }
        }
        return errors;
    }
}
