package com.bits.loanproposal.infrastructure.config;

import com.bits.loanproposal.infrastructure.messaging.RabbitMQConstants;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.bits.ddd.infra.messaging.broker.rabbitmq.RabbitMQConstants.DLX_EXCHANGE;

@Configuration
public class RabbitMQConfig {

    @Bean
    public TopicExchange loanProposalExchange() {
        return new TopicExchange(RabbitMQConstants.LOAN_PROPOSAL_EXCHANGE, true, false);
    }

    @Bean
    public Queue loanProposalUpdateCommandQueue() {
        return durableQueue(
                RabbitMQConstants.LOAN_PROPOSAL_UPDATE_COMMAND_QUEUE,
                RabbitMQConstants.LOAN_PROPOSAL_UPDATE_COMMAND_DLQ);
    }

    @Bean
    public Queue loanProposalDeleteCommandQueue() {
        return durableQueue(
                RabbitMQConstants.LOAN_PROPOSAL_DELETE_COMMAND_QUEUE,
                RabbitMQConstants.LOAN_PROPOSAL_DELETE_COMMAND_DLQ);
    }

    @Bean
    public Queue loanProposalCreatedEventQueue() {
        return durableQueue(
                RabbitMQConstants.LOAN_PROPOSAL_CREATED_EVENT_QUEUE,
                RabbitMQConstants.LOAN_PROPOSAL_CREATED_EVENT_DLQ);
    }

    @Bean
    public Queue loanProposalUpdatedEventQueue() {
        return durableQueue(
                RabbitMQConstants.LOAN_PROPOSAL_UPDATED_EVENT_QUEUE,
                RabbitMQConstants.LOAN_PROPOSAL_UPDATED_EVENT_DLQ);
    }

    @Bean
    public Queue loanProposalDeletedEventQueue() {
        return durableQueue(
                RabbitMQConstants.LOAN_PROPOSAL_DELETED_EVENT_QUEUE,
                RabbitMQConstants.LOAN_PROPOSAL_DELETED_EVENT_DLQ);
    }

    @Bean
    public Queue loanProposalApprovedEventQueue() {
        return durableQueue(
                RabbitMQConstants.LOAN_PROPOSAL_APPROVED_EVENT_QUEUE,
                RabbitMQConstants.LOAN_PROPOSAL_APPROVED_EVENT_DLQ);
    }

    @Bean
    public Binding loanProposalUpdateCommandBinding(
            @Qualifier("loanProposalExchange") TopicExchange loanProposalExchange) {
        return BindingBuilder.bind(loanProposalUpdateCommandQueue())
                .to(loanProposalExchange)
                .with(RabbitMQConstants.LOAN_PROPOSAL_UPDATE_COMMAND_ROUTING_KEY);
    }

    @Bean
    public Binding loanProposalDeleteCommandBinding(
            @Qualifier("loanProposalExchange") TopicExchange loanProposalExchange) {
        return BindingBuilder.bind(loanProposalDeleteCommandQueue())
                .to(loanProposalExchange)
                .with(RabbitMQConstants.LOAN_PROPOSAL_DELETE_COMMAND_ROUTING_KEY);
    }

    @Bean
    public Binding loanProposalCreatedEventBinding(
            @Qualifier("loanProposalExchange") TopicExchange loanProposalExchange) {
        return BindingBuilder.bind(loanProposalCreatedEventQueue())
                .to(loanProposalExchange)
                .with(RabbitMQConstants.LOAN_PROPOSAL_CREATED_EVENT_ROUTING_KEY);
    }

    @Bean
    public Binding loanProposalUpdatedEventBinding(
            @Qualifier("loanProposalExchange") TopicExchange loanProposalExchange) {
        return BindingBuilder.bind(loanProposalUpdatedEventQueue())
                .to(loanProposalExchange)
                .with(RabbitMQConstants.LOAN_PROPOSAL_UPDATED_EVENT_ROUTING_KEY);
    }

    @Bean
    public Binding loanProposalDeletedEventBinding(
            @Qualifier("loanProposalExchange") TopicExchange loanProposalExchange) {
        return BindingBuilder.bind(loanProposalDeletedEventQueue())
                .to(loanProposalExchange)
                .with(RabbitMQConstants.LOAN_PROPOSAL_DELETED_EVENT_ROUTING_KEY);
    }

    @Bean
    public Binding loanProposalApprovedEventBinding(
            @Qualifier("loanProposalExchange") TopicExchange loanProposalExchange) {
        return BindingBuilder.bind(loanProposalApprovedEventQueue())
                .to(loanProposalExchange)
                .with(RabbitMQConstants.LOAN_PROPOSAL_APPROVED_EVENT_ROUTING_KEY);
    }

    @Bean
    public Binding loanProposalUpdateCommandDlqBinding(
            @Qualifier("errorQueue") Queue errorQueue,
            @Qualifier("dlqExchange") DirectExchange dlqExchange) {
        return bindDlq(errorQueue, dlqExchange, RabbitMQConstants.LOAN_PROPOSAL_UPDATE_COMMAND_DLQ);
    }

    @Bean
    public Binding loanProposalDeleteCommandDlqBinding(
            @Qualifier("errorQueue") Queue errorQueue,
            @Qualifier("dlqExchange") DirectExchange dlqExchange) {
        return bindDlq(errorQueue, dlqExchange, RabbitMQConstants.LOAN_PROPOSAL_DELETE_COMMAND_DLQ);
    }

    @Bean
    public Binding loanProposalCreatedEventDlqBinding(
            @Qualifier("errorQueue") Queue errorQueue,
            @Qualifier("dlqExchange") DirectExchange dlqExchange) {
        return bindDlq(errorQueue, dlqExchange, RabbitMQConstants.LOAN_PROPOSAL_CREATED_EVENT_DLQ);
    }

    @Bean
    public Binding loanProposalUpdatedEventDlqBinding(
            @Qualifier("errorQueue") Queue errorQueue,
            @Qualifier("dlqExchange") DirectExchange dlqExchange) {
        return bindDlq(errorQueue, dlqExchange, RabbitMQConstants.LOAN_PROPOSAL_UPDATED_EVENT_DLQ);
    }

    @Bean
    public Binding loanProposalDeletedEventDlqBinding(
            @Qualifier("errorQueue") Queue errorQueue,
            @Qualifier("dlqExchange") DirectExchange dlqExchange) {
        return bindDlq(errorQueue, dlqExchange, RabbitMQConstants.LOAN_PROPOSAL_DELETED_EVENT_DLQ);
    }

    @Bean
    public Binding loanProposalApprovedEventDlqBinding(
            @Qualifier("errorQueue") Queue errorQueue,
            @Qualifier("dlqExchange") DirectExchange dlqExchange) {
        return bindDlq(errorQueue, dlqExchange, RabbitMQConstants.LOAN_PROPOSAL_APPROVED_EVENT_DLQ);
    }

    private Queue durableQueue(String queueName, String deadLetterRoutingKey) {
        return QueueBuilder.durable(queueName)
                .deadLetterExchange(DLX_EXCHANGE)
                .deadLetterRoutingKey(deadLetterRoutingKey)
                .build();
    }

    private Binding bindDlq(Queue queue, DirectExchange dlqExchange, String routingKey) {
        return BindingBuilder.bind(queue)
                .to(dlqExchange)
                .with(routingKey);
    }
}
