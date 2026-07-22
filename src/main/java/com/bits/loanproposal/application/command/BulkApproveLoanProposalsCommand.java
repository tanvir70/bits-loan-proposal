package com.bits.loanproposal.application.command;

import com.bits.ddd.shared.messaging.Command;
import java.util.List;
import lombok.Getter;

@Getter
public class BulkApproveLoanProposalsCommand extends Command {
    private final List<String> loanProposalIds;
    private final String approvedBy;

    public BulkApproveLoanProposalsCommand(String tracerId, List<String> loanProposalIds, String approvedBy) {
        super(tracerId);
        this.loanProposalIds = loanProposalIds;
        this.approvedBy = approvedBy;
    }
}
