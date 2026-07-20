package com.bits.loanproposal.infrastructure.persistence.repository;

import com.bits.loanproposal.infrastructure.persistence.document.LoanProposalTestDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LoanProposalTestDocumentRepository
        extends MongoRepository<LoanProposalTestDocument, String> {
}
