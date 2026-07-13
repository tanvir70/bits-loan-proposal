package com.bits.loanproposal.presentation.listener;

import com.bits.ddd.infra.core.bus.CommandBus;
import com.bits.ddd.shared.util.JsonUtil;
import com.bits.loanproposal.application.command.DeleteLoanProposalCommand;
import com.bits.loanproposal.application.command.UpdateLoanProposalCommand;
import com.bits.loanproposal.application.dto.DeleteLoanProposalMessageDto;
import com.bits.loanproposal.application.dto.UpdateLoanProposalMessageDto;
import com.bits.loanproposal.application.mapper.LoanProposalCommandMapper;
import com.bits.loanproposal.infrastructure.messaging.RabbitMQConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanProposalCommandListener {

    private final CommandBus commandBus;
    private final LoanProposalCommandMapper commandMapper;

    @RabbitListener(queues = RabbitMQConstants.LOAN_PROPOSAL_UPDATE_COMMAND_QUEUE)
    public void onUpdateCommand(Message message) {
        UpdateLoanProposalMessageDto payload = JsonUtil.deserialize(message.getBody(), UpdateLoanProposalMessageDto.class);
        UpdateLoanProposalCommand command = commandMapper.toUpdateCommand(payload);
        commandBus.handle(command);
    }

    @RabbitListener(queues = RabbitMQConstants.LOAN_PROPOSAL_DELETE_COMMAND_QUEUE)
    public void onDeleteCommand(Message message) {
        DeleteLoanProposalMessageDto payload = JsonUtil.deserialize(message.getBody(), DeleteLoanProposalMessageDto.class);
        DeleteLoanProposalCommand command = commandMapper.toDeleteCommand(payload);
        commandBus.handle(command);
    }
}
