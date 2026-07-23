package com.bits.loanproposal.presentation.controller;

import com.bits.ddd.infra.core.bus.CommandBus;
import com.bits.ddd.shared.dto.ApiResponse;
import com.bits.loanproposal.application.command.BulkApproveLoanProposalsCommand;
import com.bits.loanproposal.application.command.BulkCreateLoanProposalsCommand;
import com.bits.loanproposal.application.command.CreateLoanProposalCommand;
import com.bits.loanproposal.application.command.DeleteLoanProposalCommand;
import com.bits.loanproposal.application.mapper.LoanProposalCommandMapper;
import com.bits.loanproposal.presentation.controller.dto.BulkApproveLoanProposalRequestDto;
import com.bits.loanproposal.presentation.controller.dto.BulkCreateLoanProposalRequestDto;
import com.bits.loanproposal.presentation.controller.dto.CreateLoanProposalRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.bits.loanproposal.presentation.constant.CommandResponseConstant.ACCEPTED;
import static com.bits.loanproposal.presentation.constant.RouteConstant.*;

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

        CreateLoanProposalCommand createLoanProposalCommand = commandMapper.toCreateCommand(tracerId,
                createLoanProposalRequestDto);
        commandBus.handle(createLoanProposalCommand);

        return successResponse(HttpStatus.CREATED, tracerId);
    }

    @PostMapping(LOAN_PROPOSALS_BULK_CREATE)
    public ResponseEntity<ApiResponse<Void>> bulkCreateLoanProposals(
            @RequestAttribute(name = "trace_id", required = false) String tracerId,
            @Valid @RequestBody BulkCreateLoanProposalRequestDto bulkCreateLoanProposalRequestDto) {

        if (tracerId == null) {
            tracerId = UUID.randomUUID().toString();
        }

        BulkCreateLoanProposalsCommand bulkCreateLoanProposalsCommand = commandMapper.toBulkCreateCommand(tracerId, bulkCreateLoanProposalRequestDto);
        commandBus.handle(bulkCreateLoanProposalsCommand);

        return successResponse(HttpStatus.CREATED, tracerId);
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

        return successResponse(HttpStatus.ACCEPTED, tracerId);
    }

    @PostMapping(LOAN_PROPOSALS_BULK_APPROVAL)
    public ResponseEntity<ApiResponse<Void>> bulkApproveLoanProposals(
            @RequestAttribute(name = "trace_id", required = false) String tracerId,
            @RequestAttribute(name = "user_id", required = false) String approvedBy,
            @RequestHeader(name = "user_id", required = false) String approvedByHeader,
            @Valid @RequestBody BulkApproveLoanProposalRequestDto bulkApproveLoanProposalRequestDto) {

        if (tracerId == null) {
            tracerId = UUID.randomUUID().toString();
        }

        BulkApproveLoanProposalsCommand bulkApproveLoanProposalsCommand = commandMapper.toBulkApproveCommand(
                tracerId, bulkApproveLoanProposalRequestDto, resolveUserId(approvedBy, approvedByHeader));
        commandBus.handle(bulkApproveLoanProposalsCommand);

        return successResponse(HttpStatus.ACCEPTED, tracerId);
    }

    private <T> ResponseEntity<ApiResponse<T>> successResponse(HttpStatus status, String tracerId) {
        return ResponseEntity.ok(ApiResponse.success(null, ACCEPTED, status.value(), tracerId));
    }

    private String resolveUserId(String userIdAttribute, String userIdHeader) {
        return userIdAttribute != null && !userIdAttribute.isBlank() ? userIdAttribute : userIdHeader;
    }
}
