package com.bits.loanproposal.application.service;

import lombok.AllArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

// need to find in the legacy code base how this sequence should be provided. and its uniqueness, month/branch or global
@Service
@AllArgsConstructor
public class ProposalNumberSequenceService {
    private final MongoTemplate mongoTemplate;

    static final String COLLECTION = "loan_proposal_sequence";

    public long next(LocalDate businessDate) {
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
