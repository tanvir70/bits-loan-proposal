package com.bits.loanproposal.presentation.controller;

import com.bits.ddd.infra.core.bus.CommandBus;
import com.bits.loanproposal.application.command.CreateLoanProposalCommand;
import com.bits.loanproposal.application.mapper.LoanProposalCommandMapper;
import com.bits.loanproposal.presentation.controller.dto.CreateLoanProposalRequestDto;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loan-proposals")
public class LoanProposalCommandController {

    private final CommandBus commandBus;
    private final LoanProposalCommandMapper commandMapper;

    public LoanProposalCommandController(CommandBus commandBus, LoanProposalCommandMapper commandMapper) {
        this.commandBus = commandBus;
        this.commandMapper = commandMapper;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createLoanProposal(
            @RequestAttribute(name = "trace_id", required = false) String tracerId,
            @Valid @RequestBody CreateLoanProposalRequestDto request) {
        
        if (tracerId == null) {
            tracerId = UUID.randomUUID().toString();
        }
        
        CreateLoanProposalCommand command = commandMapper.toCreateCommand(tracerId, request);
        commandBus.handle(command);
        
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(Map.of("status", "ACCEPTED", "traceId", tracerId));
    }
}
