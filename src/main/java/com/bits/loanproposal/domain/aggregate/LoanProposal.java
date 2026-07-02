package com.bits.loanproposal.domain.aggregate;

import com.bits.ddd.domain.aggregate.AggregateRoot;
import com.bits.loanproposal.domain.mapper.LoanProposalEventMapper;
import com.bits.loanproposal.domain.param.LoanProposalCreationData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Document(collection = "loan_proposals")
public class LoanProposal extends AggregateRoot<String> {

    @Id
    private String id;
    private String applicantName;
    private double amount;

    public static LoanProposal create(LoanProposalCreationData creationData) {
        LoanProposal proposal = new LoanProposal();
        proposal.id = UUID.randomUUID().toString();
        proposal.applicantName = creationData.applicantName();
        proposal.amount = creationData.amount();
        proposal.addEvent(LoanProposalEventMapper.toCreatedEvent(proposal));
        return proposal;
    }

    @Override
    public String id() {
        return id;
    }
}
