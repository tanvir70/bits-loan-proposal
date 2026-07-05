package com.bits.loanproposal.domain.specification;

import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.loanproposal.application.dto.sourcedata.LoanProductPolicy;
import com.bits.loanproposal.application.dto.sourcedata.Member;
import com.bits.loanproposal.domain.aggregate.LoanProposal;
import com.bits.loanproposal.domain.specification.context.LoanProposalValidationContext;
import com.bits.loanproposal.domain.specification.rules.LoanAmountSpecification;
import com.bits.loanproposal.domain.specification.rules.MemberEligibilitySpecification;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoanProposalSpecificationTest {

    private LoanProposalValidationContext context(Member member, LoanProductPolicy policy, BigDecimal amount) {
        LoanProposal aggregate = new LoanProposal();
        aggregate.setProposedLoanAmount(amount);
        return new LoanProposalValidationContext(member, null, null, policy,
                null, null, null, null, null, null, null, null, aggregate);
    }

    private Member activeMember() {
        return Member.builder()
                .memberId(1L)
                .memberClassificationId(2L)
                .status("ACTIVE")
                .isScreened(false)
                .nationalId("1234567890")
                .build();
    }

    private LoanProductPolicy policy(String min, String max) {
        return LoanProductPolicy.builder()
                .minAmount(new BigDecimal(min))
                .maxAmount(new BigDecimal(max))
                .build();
    }

    @Test
    void eligibleMemberAndAmountInRangePasses() {
        Map<String, LocalizedMessage> errors = new MemberEligibilitySpecification()
                .and(new LoanAmountSpecification())
                .validate(context(activeMember(), policy("1000", "500000"), new BigDecimal("5000")));
        assertTrue(errors.isEmpty(), () -> "expected no errors but got: " + errors);
    }

    @Test
    void screenedInactiveMemberWithoutIdentityFails() {
        Member member = Member.builder().memberId(1L).status("DORMANT").isScreened(true).build();
        Map<String, LocalizedMessage> errors = new MemberEligibilitySpecification()
                .validate(context(member, null, null));
        assertEquals("MEMBER_SCREENED", errors.get("member").getKey());
        assertEquals("MEMBER_STATUS_INVALID", errors.get("memberStatus").getKey());
        assertEquals("MEMBER_NO_IDENTITY", errors.get("memberIdentity").getKey());
        assertEquals("MEMBER_CLASSIFICATION_NOT_FOUND", errors.get("memberClassification").getKey());
    }

    @Test
    void missingMemberShortCircuits() {
        Map<String, LocalizedMessage> errors = new MemberEligibilitySpecification()
                .validate(context(null, null, null));
        assertEquals(1, errors.size());
        assertEquals("MEMBER_NOT_FOUND", errors.get("member").getKey());
    }

    @Test
    void amountOutsidePolicyRangeFails() {
        Map<String, LocalizedMessage> errors = new LoanAmountSpecification()
                .validate(context(activeMember(), policy("1000", "500000"), new BigDecimal("900")));
        assertEquals("LOAN_AMOUNT_OUT_OF_POLICY_RANGE", errors.get("proposedLoanAmount").getKey());

        errors = new LoanAmountSpecification()
                .validate(context(activeMember(), policy("1000", "500000"), new BigDecimal("500001")));
        assertEquals("LOAN_AMOUNT_OUT_OF_POLICY_RANGE", errors.get("proposedLoanAmount").getKey());
    }

    @Test
    void negativeOrMissingAmountFails() {
        Map<String, LocalizedMessage> errors = new LoanAmountSpecification()
                .validate(context(activeMember(), policy("1000", "500000"), new BigDecimal("-1")));
        assertEquals("LOAN_AMOUNT_INVALID", errors.get("proposedLoanAmount").getKey());
    }
}
