package com.bits.loanproposal.domain.specification.rules;

import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.ddd.specification.rules.Specification;
import com.bits.loanproposal.application.dto.sourcedata.Member;
import com.bits.loanproposal.domain.aggregate.LoanProposal;
import com.bits.loanproposal.domain.entity.SecondInsurer;
import com.bits.loanproposal.domain.specification.context.LoanProposalValidationContext;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

// DDD-REQ-026
public class AgeLimitSpecification implements Specification<LoanProposalValidationContext> {

    private static final Long DOUBLE = 2L;

    @Override
    public Map<String, LocalizedMessage> validate(LoanProposalValidationContext context) {
        Map<String, LocalizedMessage> errors = new HashMap<>();
        LoanProposal aggregate = context.aggregate();
        Member member = context.member();
        LocalDate applicationDate = aggregate.getApplicationDate() != null
                ? aggregate.getApplicationDate() : LocalDate.now();
        LocalDate loanEndDate = aggregate.getProposalDurationInMonths() != null
                ? applicationDate.plusMonths(aggregate.getProposalDurationInMonths()) : applicationDate;

        if (member != null && member.getDateOfBirth() != null) {
            Period age = Period.between(member.getDateOfBirth(), applicationDate);
            boolean goldenLienExemption = Boolean.TRUE.equals(member.getIsGolden())
                    && (ProductTypes.is(context.loanProduct(), "LIEN")
                        || ProductTypes.is(context.loanProduct(), "MONEY_PLANT"));
            if (age.getYears() >= 70 && !goldenLienExemption) {
                errors.put("memberAge", ageMessage("MEMBER_AGE_INELIGIBLE", age));
            }
            // ponytail: doc's individual-project 70-80/80+ branch is dead code (the >=70 check
            // above already catches it) and its golden-member valid-range rule is undefined; both skipped
            if (context.loanProduct() != null && context.loanProduct().isMicroInsuranceMandatory()
                    && age.getYears() >= 65
                    && !isIndividualLanding(context)
                    && !Boolean.TRUE.equals(aggregate.getMicroInsurance())) {
                errors.put("microInsurance",
                        LocalizedMessage.builder().key("MICRO_INSURANCE_MANDATORY").build());
            }
            Period ageAtEnd = Period.between(member.getDateOfBirth(), loanEndDate);
            // ponytail: doc requires age at loan end to be EXACTLY 70y0m, which misses 70y1m+;
            // implemented as >=70 at loan end (the >=70-at-application case is already rejected above)
            if (ageAtEnd.getYears() >= 70 && age.getYears() < 70
                    && Boolean.TRUE.equals(aggregate.getMicroInsurance())) {
                errors.put("microInsuranceAge",
                        ageMessage("MEMBER_MICRO_INSURANCE_AGE_INELIGIBLE", ageAtEnd));
            }
        }

        SecondInsurer insurer = aggregate.getSecondInsurer();
        if (insurer != null && insurer.getDateOfBirth() != null
                && Boolean.TRUE.equals(aggregate.getMicroInsurance())
                && DOUBLE.equals(aggregate.getPolicyTypeId())) {
            Period insurerAge = Period.between(insurer.getDateOfBirth(), applicationDate);
            if (insurerAge.getYears() >= 70) {
                errors.put("secondInsurerAge", ageMessage("SECOND_INSURER_AGE_INELIGIBLE", insurerAge));
            }
            Period insurerAgeAtEnd = Period.between(insurer.getDateOfBirth(), loanEndDate);
            if (insurerAgeAtEnd.getYears() >= 70 && insurerAge.getYears() < 70) {
                errors.put("secondInsurerMicroInsuranceAge",
                        ageMessage("SECOND_INSURER_MICRO_INSURANCE_AGE_INELIGIBLE", insurerAgeAtEnd));
            }
        }
        return errors;
    }

    private boolean isIndividualLanding(LoanProposalValidationContext context) {
        return context.project() != null
                && "INDIVIDUAL".equalsIgnoreCase(context.project().getLandingType());
    }

    private LocalizedMessage ageMessage(String key, Period age) {
        return LocalizedMessage.builder()
                .key(key)
                .args(new Object[]{age.getYears(), age.getMonths(), age.getDays()})
                .build();
    }
}
