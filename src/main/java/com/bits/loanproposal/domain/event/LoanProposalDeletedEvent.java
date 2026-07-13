package com.bits.loanproposal.domain.event;

import com.bits.ddd.shared.domain.value.DomainStatus;
import com.bits.ddd.shared.messaging.DomainEventMessage;
import com.bits.loanproposal.domain.enums.LoanProposalStatus;
import com.bits.loanproposal.infrastructure.messaging.RabbitMQConstants;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class LoanProposalDeletedEvent extends DomainEventMessage {
    public static final String TOPIC_EXCHANGE = RabbitMQConstants.LOAN_PROPOSAL_EXCHANGE;
    public static final String ROUTING_KEY = RabbitMQConstants.LOAN_PROPOSAL_DELETED_EVENT_ROUTING_KEY;

    private Long branchId;
    private Long memberId;
    private String proposalNumber;
    private LoanProposalStatus loanProposalStatus;
    private DomainStatus domainStatus;
    private String deletedBy;
    private LocalDateTime deletedAt;
    private String traceId;

    public LoanProposalDeletedEvent(
            String id,
            Long branchId,
            Long memberId,
            String proposalNumber,
            LoanProposalStatus loanProposalStatus,
            DomainStatus domainStatus,
            String deletedBy,
            LocalDateTime deletedAt,
            long version,
            String traceId) {
        super(id, "LoanProposal", version, traceId, TOPIC_EXCHANGE, ROUTING_KEY);
        this.branchId = branchId;
        this.memberId = memberId;
        this.proposalNumber = proposalNumber;
        this.loanProposalStatus = loanProposalStatus;
        this.domainStatus = domainStatus;
        this.deletedBy = deletedBy;
        this.deletedAt = deletedAt;
        this.traceId = traceId;
    }
}
