package com.bits.loanproposal.application.command;

import com.bits.ddd.shared.messaging.Command;
import java.util.List;
import lombok.Getter;

@Getter
public class BulkCreateLoanProposalsCommand extends Command {

    private final List<CreateLoanProposalCommand> loanProposals;

    public BulkCreateLoanProposalsCommand(String tracerId, List<CreateLoanProposalCommand> loanProposals) {
        super(tracerId);
        this.loanProposals = loanProposals;
    }
}
