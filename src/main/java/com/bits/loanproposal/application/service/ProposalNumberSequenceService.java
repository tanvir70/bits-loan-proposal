package com.bits.loanproposal.application.service;

import org.bson.Document;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

// DDD-REQ-002: supplies the sequence for proposal numbers ({YYYY}{MM}-{seq:5}).
// not defined in ears: counter scope is a guess — global per month here, but the unique index
// (proposalNumber + branchId) hints legacy may number per branch; if so, key by {branchCode}-{yearMonth}.
@Service
public class ProposalNumberSequenceService {

    static final String COLLECTION = "loan_proposal_sequence";

    private final MongoTemplate mongoTemplate;

    public ProposalNumberSequenceService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public long next(LocalDate businessDate) {
        // caller guarantees a business date; a null here is a bug, not a case to paper over
        String yearMonth = String.format("%d%02d", businessDate.getYear(), businessDate.getMonthValue());
        Document counter = mongoTemplate.findAndModify(
                Query.query(Criteria.where("_id").is(yearMonth)),
                new Update().inc("seq", 1),
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                Document.class,
                COLLECTION);
        return counter.get("seq", Number.class).longValue();
    }
}
