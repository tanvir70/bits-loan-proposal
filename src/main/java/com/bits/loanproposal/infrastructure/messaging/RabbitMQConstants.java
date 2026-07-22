package com.bits.loanproposal.infrastructure.messaging;

public final class RabbitMQConstants {
    public static final String LOAN_PROPOSAL_EXCHANGE = "loan-proposal.exchange";

    public static final String LOAN_PROPOSAL_UPDATE_COMMAND_QUEUE = "loan-proposal.update-command.queue";
    public static final String LOAN_PROPOSAL_DELETE_COMMAND_QUEUE = "loan-proposal.delete-command.queue";
    public static final String LOAN_PROPOSAL_CREATED_EVENT_QUEUE = "loan-proposal.created.queue";
    public static final String LOAN_PROPOSAL_UPDATED_EVENT_QUEUE = "loan-proposal.updated.queue";
    public static final String LOAN_PROPOSAL_DELETED_EVENT_QUEUE = "loan-proposal.deleted.queue";
    public static final String LOAN_PROPOSAL_APPROVED_EVENT_QUEUE = "loan-proposal.approved.queue";

    public static final String LOAN_PROPOSAL_UPDATE_COMMAND_DLQ = "loan-proposal.update-command.dlq";
    public static final String LOAN_PROPOSAL_DELETE_COMMAND_DLQ = "loan-proposal.delete-command.dlq";
    public static final String LOAN_PROPOSAL_CREATED_EVENT_DLQ = "loan-proposal.created.dlq";
    public static final String LOAN_PROPOSAL_UPDATED_EVENT_DLQ = "loan-proposal.updated.dlq";
    public static final String LOAN_PROPOSAL_DELETED_EVENT_DLQ = "loan-proposal.deleted.dlq";
    public static final String LOAN_PROPOSAL_APPROVED_EVENT_DLQ = "loan-proposal.approved.dlq";

    public static final String LOAN_PROPOSAL_UPDATE_COMMAND_ROUTING_KEY = "loan-proposal.update-command";
    public static final String LOAN_PROPOSAL_DELETE_COMMAND_ROUTING_KEY = "loan-proposal.delete-command";
    public static final String LOAN_PROPOSAL_CREATED_EVENT_ROUTING_KEY = "loan-proposal.created";
    public static final String LOAN_PROPOSAL_UPDATED_EVENT_ROUTING_KEY = "loan-proposal.updated";
    public static final String LOAN_PROPOSAL_DELETED_EVENT_ROUTING_KEY = "loan-proposal.deleted";
    public static final String LOAN_PROPOSAL_APPROVED_EVENT_ROUTING_KEY = "loan-proposal.approved";

    private RabbitMQConstants() {
    }
}
