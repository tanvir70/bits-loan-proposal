package com.bits.loanproposal.presentation.listener;

import com.bits.ddd.shared.exception.domain.BusinessRuleViolationException;
import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.loanproposal.infrastructure.messaging.RabbitMQConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

// ponytail: throwaway listener to verify globalRabbitMQExceptionHandler; delete before merge
@Slf4j
@Service
public class ExceptionTestEventListener {

    @RabbitListener(queues = RabbitMQConstants.LOAN_PROPOSAL_CREATED_EVENT_QUEUE,
            errorHandler = "globalRabbitMQExceptionHandler")
    public void onCreatedEvent(Message message) {
        log.info("Test listener received message, throwing: {}", new String(message.getBody()));
        throw new BusinessRuleViolationException("REQ-AMQP-001", "LoanProposalCreatedEvent",
                Map.of("proposedLoanAmount",
                        LocalizedMessage.builder().key("LOAN_AMOUNT_EXCEEDS_LIMIT").build()));
    }
}
