package com.bits.loanproposal.infrastructure.persistence.document;

import com.bits.ddd.shared.domain.value.DomainStatus;
import com.bits.loanproposal.domain.constant.LoanProposalTestStatuses;
import com.bits.loanproposal.domain.enums.LoanProposalTestDecisionCode;
import com.bits.loanproposal.domain.enums.LoanProposalTestOrigin;
import com.bits.loanproposal.domain.enums.LoanProposalTestPriority;
import com.bits.loanproposal.domain.value.LoanProposalTestWorkflowStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Focused MongoDB document demonstrating every requested representation without using an
 * aggregate root.
 *
 * <ul>
 *   <li>{@code domainStatus}: nested document, for example {@code {"code":"AWAITING_DOCUMENTS"}}</li>
 *   <li>{@code workflowStatus}: independent client type, for example {@code {"code":"UNDER_REVIEW"}}</li>
 *   <li>{@code decisionCode}: String value, for example {@code "ACC"}</li>
 *   <li>{@code priority}: numeric value, for example {@code 20}</li>
 *   <li>{@code origin}: normal enum constant name, for example {@code "MOBILE_APP"}</li>
 * </ul>
 */
@Document(collection = "loan_proposal_enum_test")
public record LoanProposalTestDocument(
        @Id String id,
        DomainStatus domainStatus,
        LoanProposalTestWorkflowStatus workflowStatus,
        LoanProposalTestDecisionCode decisionCode,
        LoanProposalTestPriority priority,
        LoanProposalTestOrigin origin
) {

    public LoanProposalTestDocument {
        domainStatus = LoanProposalTestStatuses.requireSupported(domainStatus);
        requireValue(workflowStatus, "workflowStatus");
        requireValue(decisionCode, "decisionCode");
        requireValue(priority, "priority");
        requireValue(origin, "origin");
    }

    private static void requireValue(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
    }
}
