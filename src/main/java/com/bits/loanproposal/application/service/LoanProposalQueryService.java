package com.bits.loanproposal.application.service;

import com.bits.ddd.shared.exception.domain.DomainValidationException;
import com.bits.loanproposal.domain.aggregate.LoanProposal;
import com.bits.loanproposal.domain.aggregate.LoanProposalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.bits.loanproposal.domain.constant.DomainErrorConstant.LOAN_PROPOSAL_NOT_FOUND;
import static com.bits.loanproposal.domain.constant.DomainErrorConstant.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class LoanProposalQueryService {

    private final LoanProposalRepository repository;

    public LoanProposal fetchByIdOrHandleFailure(String id, String traceId) {
        return repository.findById(id).orElseThrow(() -> new DomainValidationException(NOT_FOUND, LOAN_PROPOSAL_NOT_FOUND));
    }
}
