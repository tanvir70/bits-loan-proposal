package com.bits.loanproposal.application.mapper;

import com.bits.loanproposal.application.command.CreateLoanProposalCommand;
import com.bits.loanproposal.presentation.controller.dto.CreateLoanProposalRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoanProposalCommandMapper {

  CreateLoanProposalCommand toCreateCommand(String tracerId, CreateLoanProposalRequestDto request);
}
