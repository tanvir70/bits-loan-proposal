package com.bits.loanproposal.presentation.listener;

import com.bits.ddd.infra.core.bus.CommandBus;
import com.bits.ddd.shared.util.JsonUtil;
import com.bits.loanproposal.application.command.DeleteLoanProposalCommand;
import com.bits.loanproposal.application.command.UpdateLoanProposalCommand;
import com.bits.loanproposal.application.dto.DeleteLoanProposalMessageDto;
import com.bits.loanproposal.application.dto.UpdateLoanProposalMessageDto;
import com.bits.loanproposal.application.mapper.LoanProposalCommandMapper;
import com.bits.loanproposal.infrastructure.messaging.RabbitMQConstants;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class LoanProposalCommandListener {

    private final CommandBus commandBus;
    private final LoanProposalCommandMapper commandMapper;

    public LoanProposalCommandListener(CommandBus commandBus,
                                       LoanProposalCommandMapper commandMapper) {
        this.commandBus = commandBus;
        this.commandMapper = commandMapper;
    }

    @RabbitListener(queues = RabbitMQConstants.LOAN_PROPOSAL_UPDATE_COMMAND_QUEUE, errorHandler = "messageExceptionHandler")
    public void onUpdateCommand(Message message) {
        UpdateLoanProposalMessageDto payload = JsonUtil.deserialize(message.getBody(), UpdateLoanProposalMessageDto.class);
        UpdateLoanProposalCommand command = commandMapper.toUpdateCommand(payload);
        commandBus.handle(command);
    }

    @RabbitListener(queues = RabbitMQConstants.LOAN_PROPOSAL_DELETE_COMMAND_QUEUE, errorHandler = "messageExceptionHandler")
    public void onDeleteCommand(Message message) {
        DeleteLoanProposalMessageDto payload = JsonUtil.deserialize(message.getBody(), DeleteLoanProposalMessageDto.class);
        DeleteLoanProposalCommand command = commandMapper.toDeleteCommand(payload);
        commandBus.handle(command);
    }
}
