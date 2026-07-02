package com.bits.loanproposal.application.command;

import com.bits.ddd.shared.messaging.CommandMessage;
import lombok.Getter;

@Getter
public class CreateLoanProposalCommand extends CommandMessage {
    private final String applicantName;
    private final double amount;

    public CreateLoanProposalCommand(String tracerId, String applicantName, double amount) {
        super(tracerId);
        this.applicantName = applicantName;
        this.amount = amount;
    }
}
