package com.bits.loanproposal.domain.event;

import com.bits.ddd.shared.domain.value.DomainStatus;
import com.bits.ddd.shared.messaging.DomainEventMessage;
import com.bits.loanproposal.domain.enums.LoanProposalStatus;
import com.bits.loanproposal.infrastructure.messaging.RabbitMQConstants;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoanProposalApprovedEvent extends DomainEventMessage {
    public static final String TOPIC_EXCHANGE = RabbitMQConstants.LOAN_PROPOSAL_EXCHANGE;
    public static final String ROUTING_KEY = RabbitMQConstants.LOAN_PROPOSAL_APPROVED_EVENT_ROUTING_KEY;

    private Long loanProposalId;
    private String proposalNumber;
    private Long branchId;
    private Long memberId;
    private LoanProposalStatus loanProposalStatus;
    private DomainStatus domainStatus;
    private String approvedBy;
    private LocalDateTime approvedAt;
    private String traceId;

    public LoanProposalApprovedEvent(
            String id,
            Long loanProposalId,
            String proposalNumber,
            Long branchId,
            Long memberId,
            LoanProposalStatus loanProposalStatus,
            DomainStatus domainStatus,
            String approvedBy,
            LocalDateTime approvedAt,
            long version,
            String traceId) {
        super(id, "LoanProposal", version, traceId, TOPIC_EXCHANGE, ROUTING_KEY);
        this.loanProposalId = loanProposalId;
        this.proposalNumber = proposalNumber;
        this.branchId = branchId;
        this.memberId = memberId;
        this.loanProposalStatus = loanProposalStatus;
        this.domainStatus = domainStatus;
        this.approvedBy = approvedBy;
        this.approvedAt = approvedAt;
        this.traceId = traceId;
    }
}
