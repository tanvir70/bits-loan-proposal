package com.bits.loanproposal.domain.event;

import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.ddd.shared.messaging.EventMessage;
import com.bits.loanproposal.infrastructure.messaging.RabbitMQConstants;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

// DDD-REQ-032. Extends EventMessage (not DomainEventMessage): a failed proposal is never
// persisted, so there is no aggregate identifier/version to carry.
@Getter
@NoArgsConstructor
public class LoanProposalFailedEvent extends EventMessage {

    public static final String TOPIC_EXCHANGE = RabbitMQConstants.LOAN_PROPOSAL_EXCHANGE;
    public static final String ROUTING_KEY = RabbitMQConstants.LOAN_PROPOSAL_FAILED_EVENT_ROUTING_KEY;

    private String errorCode;
    private Map<String, String> errors;

    private LoanProposalFailedEvent(String tracerId, String errorCode, Map<String, String> errors) {
        super(tracerId, TOPIC_EXCHANGE, ROUTING_KEY);
        this.errorCode = errorCode;
        this.errors = errors;
    }

    public static LoanProposalFailedEvent validationError(String traceId,
                                                          Map<String, LocalizedMessage> errors) {
        Map<String, String> flattened = new LinkedHashMap<>();
        errors.forEach((field, message) -> flattened.put(field, message.getKey()));
        return new LoanProposalFailedEvent(traceId, "LOAN_PROPOSAL_VALIDATION_FAILED", flattened);
    }
    // ponytail: doc's second factory sourceDataError(traceId, errorCode, errors) omitted —
    // the source-data path keeps the library's SourceDataValidationException; add when needed
}
