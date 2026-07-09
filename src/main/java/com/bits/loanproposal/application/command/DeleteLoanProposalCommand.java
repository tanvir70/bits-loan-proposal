package com.bits.loanproposal.application.command;

import com.bits.ddd.shared.messaging.Command;
import lombok.Getter;

@Getter
public class DeleteLoanProposalCommand extends Command {
    private final String id;
    private final Long branchId;
    private final String deletedBy;

    public DeleteLoanProposalCommand(String tracerId, String id, Long branchId, String deletedBy) {
        super(tracerId);
        this.id = id;
        this.branchId = branchId;
        this.deletedBy = deletedBy;
    }
}
