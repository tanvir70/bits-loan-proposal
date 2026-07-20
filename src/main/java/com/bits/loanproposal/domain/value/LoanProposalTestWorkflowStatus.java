package com.bits.loanproposal.domain.value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/** Client-owned enum-like value with no inheritance or interface dependency on bits-ddd. */
public final class LoanProposalTestWorkflowStatus {

    public static final LoanProposalTestWorkflowStatus DRAFT =
            new LoanProposalTestWorkflowStatus("DRAFT");
    public static final LoanProposalTestWorkflowStatus UNDER_REVIEW =
            new LoanProposalTestWorkflowStatus("UNDER_REVIEW");
    public static final LoanProposalTestWorkflowStatus APPROVED =
            new LoanProposalTestWorkflowStatus("APPROVED");

    private static final Map<String, LoanProposalTestWorkflowStatus> BY_CODE = Map.of(
            DRAFT.code(), DRAFT,
            UNDER_REVIEW.code(), UNDER_REVIEW,
            APPROVED.code(), APPROVED
    );

    private final String code;

    private LoanProposalTestWorkflowStatus(String code) {
        this.code = normalize(code);
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static LoanProposalTestWorkflowStatus fromCode(String code) {
        String normalizedCode = normalize(code);
        LoanProposalTestWorkflowStatus status = BY_CODE.get(normalizedCode);
        if (status == null) {
            throw new IllegalArgumentException(
                    "Unknown loan proposal test workflow status: " + normalizedCode);
        }
        return status;
    }

    @JsonValue
    public String code() {
        return code;
    }

    private static String normalize(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException(
                    "Loan proposal test workflow status code cannot be null or blank");
        }
        return code.trim().toUpperCase(Locale.ROOT);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return object instanceof LoanProposalTestWorkflowStatus that
                && Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return code;
    }
}
