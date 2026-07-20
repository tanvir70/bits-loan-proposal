package com.bits.loanproposal.presentation.controller;

import com.bits.ddd.shared.dto.ApiResponse;
import com.bits.loanproposal.infrastructure.persistence.document.LoanProposalTestDocument;
import com.bits.loanproposal.infrastructure.persistence.repository.LoanProposalTestDocumentRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loan-proposal-test-documents")
@RequiredArgsConstructor
public class LoanProposalTestDocumentController {

    private static final String CREATED = "Loan proposal test document created";
    private static final String FOUND = "Loan proposal test document found";
    private static final String NOT_FOUND = "Loan proposal test document not found";

    private final LoanProposalTestDocumentRepository repository;

    @PostMapping
    public ResponseEntity<ApiResponse<LoanProposalTestDocument>> create(
            @RequestAttribute(name = "trace_id", required = false) String tracerId,
            @RequestBody LoanProposalTestDocument request) {

        String resolvedTraceId = resolveTraceId(tracerId);
        String id = request.id() == null || request.id().isBlank()
                ? UUID.randomUUID().toString()
                : request.id();

        LoanProposalTestDocument document = new LoanProposalTestDocument(
                id,
                request.domainStatus(),
                request.workflowStatus(),
                request.decisionCode(),
                request.priority(),
                request.origin());
        LoanProposalTestDocument savedDocument = repository.save(document);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        savedDocument,
                        CREATED,
                        HttpStatus.CREATED.value(),
                        resolvedTraceId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LoanProposalTestDocument>> getById(
            @RequestAttribute(name = "trace_id", required = false) String tracerId,
            @PathVariable String id) {

        String resolvedTraceId = resolveTraceId(tracerId);

        return repository.findById(id)
                .map(document -> ResponseEntity.ok(ApiResponse.success(
                        document,
                        FOUND,
                        HttpStatus.OK.value(),
                        resolvedTraceId)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.failure(
                                NOT_FOUND,
                                HttpStatus.NOT_FOUND,
                                resolvedTraceId)));
    }

    private static String resolveTraceId(String tracerId) {
        return tracerId == null || tracerId.isBlank()
                ? UUID.randomUUID().toString()
                : tracerId;
    }
}
