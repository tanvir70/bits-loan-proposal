package com.bits.loanproposal.domain.specification.rules;

import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.ddd.specification.rules.Specification;
import com.bits.loanproposal.application.dto.sourcedata.Member;
import com.bits.loanproposal.domain.specification.context.LoanProposalValidationContext;

import java.util.HashMap;
import java.util.Map;

public class MemberEligibilitySpecification implements Specification<LoanProposalValidationContext> {

    @Override
    public Map<String, LocalizedMessage> validate(LoanProposalValidationContext context) {
        Map<String, LocalizedMessage> errors = new HashMap<>();
        Member member = context.member();
        if (member == null) {
            errors.put("member", LocalizedMessage.builder().key("MEMBER_NOT_FOUND").build());
            return errors;
        }
        if (Boolean.TRUE.equals(member.getIsScreened())) {
            errors.put("member", LocalizedMessage.builder().key("MEMBER_SCREENED").build());
        }
        if (member.getMemberClassificationId() == null) {
            errors.put("memberClassification",
                    LocalizedMessage.builder().key("MEMBER_CLASSIFICATION_NOT_FOUND").build());
        }
        if (!"ACTIVE".equalsIgnoreCase(member.getStatus())) {
            errors.put("memberStatus", LocalizedMessage.builder()
                    .key("MEMBER_STATUS_INVALID")
                    .args(new Object[]{member.getStatus()})
                    .build());
        }
        if (member.getNationalId() == null || member.getNationalId().isBlank()) {
            errors.put("memberIdentity", LocalizedMessage.builder().key("MEMBER_NO_IDENTITY").build());
        }
        return errors;
    }
}
