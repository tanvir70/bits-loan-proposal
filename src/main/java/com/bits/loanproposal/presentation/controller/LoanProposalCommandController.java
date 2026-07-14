package com.bits.loanproposal.presentation.controller;

import static com.bits.loanproposal.presentation.constant.CommandResponseConstant.ACCEPTED;
import static com.bits.loanproposal.presentation.constant.RouteConstant.LOAN_PROPOSALS;
import static com.bits.loanproposal.presentation.constant.RouteConstant.LOAN_PROPOSALS_DELETE;

import com.bits.ddd.infra.core.bus.CommandBus;
import com.bits.ddd.shared.dto.ApiResponse;
import com.bits.loanproposal.application.command.CreateLoanProposalCommand;
import com.bits.loanproposal.application.command.DeleteLoanProposalCommand;
import com.bits.loanproposal.application.mapper.LoanProposalCommandMapper;
import com.bits.loanproposal.presentation.controller.dto.CreateLoanProposalRequestDto;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(LOAN_PROPOSALS)
@RequiredArgsConstructor
public class LoanProposalCommandController {

  private final CommandBus commandBus;
  private final LoanProposalCommandMapper commandMapper;

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

    return ResponseEntity.ok(
        ApiResponse.success(null, ACCEPTED, HttpStatus.CREATED.value(),
            tracerId));
  }

  @DeleteMapping(LOAN_PROPOSALS_DELETE)
  public ResponseEntity<ApiResponse<Void>> deleteLoanProposal(
      @RequestAttribute(name = "trace_id", required = false) String tracerId,
      @RequestAttribute(name = "user_id", required = false) String deletedBy,
      @PathVariable Long branchId, @PathVariable String id) {

    if (tracerId == null) {
      tracerId = UUID.randomUUID().toString();
    }
    if (deletedBy == null || deletedBy.isBlank()) {
      deletedBy = "system";
    }

    DeleteLoanProposalCommand deleteLoanProposalCommand = commandMapper.toDeleteCommand(tracerId,
        id, branchId, deletedBy);
    commandBus.handle(deleteLoanProposalCommand);

    return ResponseEntity.ok(
        ApiResponse.success(null, ACCEPTED, HttpStatus.ACCEPTED.value(),
            tracerId));
  }
}
