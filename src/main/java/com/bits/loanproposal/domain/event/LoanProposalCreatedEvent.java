package com.bits.loanproposal.domain.event;

import com.bits.ddd.shared.messaging.DomainEventMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoanProposalCreatedEvent extends DomainEventMessage {
    public static final String TOPIC_EXCHANGE = "loan-proposal.event.exchange";
    public static final String ROUTING_KEY = "loan-proposal.created";

    private String loanProposalId;
    private String applicantName;
    private double amount;

    public LoanProposalCreatedEvent(String loanProposalId, String applicantName, double amount, long version, String tracerId) {
        super(loanProposalId, "LoanProposal", version, tracerId, TOPIC_EXCHANGE, ROUTING_KEY);
        this.loanProposalId = loanProposalId;
        this.applicantName = applicantName;
        this.amount = amount;
    }
}
