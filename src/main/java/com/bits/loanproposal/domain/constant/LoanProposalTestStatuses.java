package com.bits.loanproposal.domain.constant;

import com.bits.ddd.shared.domain.value.DomainStatus;

import java.util.Map;

/**
 * Client-owned registry for the {@link DomainStatus} values accepted by this service.
 *
 * <p>The library deliberately permits {@code DomainStatus.of("CLIENT_VALUE")}. This registry
 * adds this client's closed set and gives MongoDB converters a place to reject unknown values.
 */
public final class LoanProposalTestStatuses {

    public static final DomainStatus AWAITING_DOCUMENTS = DomainStatus.of("AWAITING_DOCUMENTS");
    public static final DomainStatus READY_FOR_DECISION = DomainStatus.of("READY_FOR_DECISION");
    public static final DomainStatus MANUALLY_REVIEWED = DomainStatus.of("MANUALLY_REVIEWED");

    private static final Map<String, DomainStatus> BY_CODE = Map.ofEntries(
            Map.entry(DomainStatus.CREATED.code(), DomainStatus.CREATED),
            Map.entry(DomainStatus.UPDATED.code(), DomainStatus.UPDATED),
            Map.entry(DomainStatus.DELETED.code(), DomainStatus.DELETED),
            Map.entry(DomainStatus.ACTIVE.code(), DomainStatus.ACTIVE),
            Map.entry(DomainStatus.INACTIVE.code(), DomainStatus.INACTIVE),
            Map.entry(DomainStatus.PENDING.code(), DomainStatus.PENDING),
            Map.entry(DomainStatus.APPROVED.code(), DomainStatus.APPROVED),
            Map.entry(DomainStatus.REJECTED.code(), DomainStatus.REJECTED),
            Map.entry(DomainStatus.COMPLETED.code(), DomainStatus.COMPLETED),
            Map.entry(DomainStatus.FAILED.code(), DomainStatus.FAILED),
            Map.entry(AWAITING_DOCUMENTS.code(), AWAITING_DOCUMENTS),
            Map.entry(READY_FOR_DECISION.code(), READY_FOR_DECISION),
            Map.entry(MANUALLY_REVIEWED.code(), MANUALLY_REVIEWED)
    );

    private LoanProposalTestStatuses() {
    }

    public static DomainStatus fromCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("Domain status code cannot be null");
        }

        String normalizedCode = DomainStatus.of(code).code();
        DomainStatus status = BY_CODE.get(normalizedCode);
        if (status == null) {
            throw new IllegalArgumentException("Unknown domain status code: " + normalizedCode);
        }
        return status;
    }

    public static DomainStatus requireSupported(DomainStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Domain status cannot be null");
        }
        return fromCode(status.code());
    }
}
