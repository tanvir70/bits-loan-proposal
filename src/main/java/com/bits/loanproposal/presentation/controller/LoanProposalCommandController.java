package com.bits.loanproposal.presentation.controller;

import com.bits.ddd.controller.BaseApiController;
import com.bits.ddd.dto.ApiResponse;
import com.bits.ddd.infra.core.bus.CommandBus;
import com.bits.loanproposal.application.command.CreateLoanProposalCommand;
import com.bits.loanproposal.application.mapper.LoanProposalCommandMapper;
import com.bits.loanproposal.presentation.controller.dto.CreateLoanProposalRequestDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.bits.loanproposal.presentation.constant.CommandResponseConstant.ACCEPTED;
import static com.bits.loanproposal.presentation.constant.RouteConstant.LOAN_PROPOSALS;

@RestController
@RequestMapping(LOAN_PROPOSALS)
public class LoanProposalCommandController extends BaseApiController {

    private final CommandBus commandBus;
    private final LoanProposalCommandMapper commandMapper;

    public LoanProposalCommandController(CommandBus commandBus,
                                         LoanProposalCommandMapper commandMapper) {
        this.commandBus = commandBus;
        this.commandMapper = commandMapper;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createLoanProposal(
            @RequestAttribute(name = "trace_id", required = false) String tracerId,
            @Valid @RequestBody CreateLoanProposalRequestDto createLoanProposalRequestDto) {

        if (tracerId == null) {
            tracerId = UUID.randomUUID().toString();
        }

        CreateLoanProposalCommand command = commandMapper.toCreateCommand(tracerId,
                createLoanProposalRequestDto);
        commandBus.handle(command);

        return respond(HttpStatus.ACCEPTED, ACCEPTED, tracerId);
    }
}
