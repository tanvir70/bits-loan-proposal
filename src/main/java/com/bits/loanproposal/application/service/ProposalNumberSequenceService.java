package com.bits.loanproposal.application.service;

import org.bson.Document;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

// DDD-REQ-002: supplies the monthly sequence for proposal numbers ({YYYY}{MM}-{seq:5}).
// findAndModify with $inc + upsert is atomic, so concurrent creates never share a number;
// a fresh counter document per month resets the sequence naturally.
@Service
public class ProposalNumberSequenceService {

    static final String COLLECTION = "loan_proposal_sequence";

    private final MongoTemplate mongoTemplate;

    public ProposalNumberSequenceService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public long next(LocalDate businessDate) {
        LocalDate date = businessDate != null ? businessDate : LocalDate.now();
        String yearMonth = String.format("%d%02d", date.getYear(), date.getMonthValue());
        Document counter = mongoTemplate.findAndModify(
                Query.query(Criteria.where("_id").is(yearMonth)),
                new Update().inc("seq", 1),
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                Document.class,
                COLLECTION);
        return counter.get("seq", Number.class).longValue();
    }
}
