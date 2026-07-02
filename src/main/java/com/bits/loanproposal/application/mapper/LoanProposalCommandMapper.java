package com.bits.loanproposal.application.mapper;

import com.bits.loanproposal.application.command.CreateLoanProposalCommand;
import com.bits.loanproposal.presentation.controller.dto.CreateLoanProposalRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanProposalCommandMapper {

    @Mapping(target = "tracerId", source = "tracerId")
    CreateLoanProposalCommand toCreateCommand(String tracerId, CreateLoanProposalRequestDto request);
}
