package com.bits.loanproposal.application.mapper;

import com.bits.loanproposal.application.command.CreateLoanProposalCommand;
import com.bits.loanproposal.application.command.DeleteLoanProposalCommand;
import com.bits.loanproposal.application.command.BulkApproveLoanProposalsCommand;
import com.bits.loanproposal.application.command.UpdateLoanProposalCommand;
import com.bits.loanproposal.application.dto.DeleteLoanProposalMessageDto;
import com.bits.loanproposal.application.dto.UpdateLoanProposalMessageDto;
import com.bits.loanproposal.presentation.controller.dto.BulkApproveLoanProposalRequestDto;
import com.bits.loanproposal.presentation.controller.dto.CreateLoanProposalRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanProposalCommandMapper {

  @Mapping(target = "tracerId", source = "tracerId")
  CreateLoanProposalCommand toCreateCommand(String tracerId, CreateLoanProposalRequestDto request);

  default DeleteLoanProposalCommand toDeleteCommand(String tracerId, String id, Long branchId, String deletedBy) {
    return new DeleteLoanProposalCommand(tracerId, id, branchId, deletedBy);
  }

  default BulkApproveLoanProposalsCommand toBulkApproveCommand(
      String tracerId,
      BulkApproveLoanProposalRequestDto request,
      String approvedBy) {
    return new BulkApproveLoanProposalsCommand(tracerId, request.loanProposalIds(), approvedBy);
  }

  default DeleteLoanProposalCommand toDeleteCommand(DeleteLoanProposalMessageDto request) {
    if (request == null) {
      return null;
    }
    return new DeleteLoanProposalCommand(
        request.tracerId(),
        request.id(),
        request.branchId(),
        request.deletedBy()
    );
  }

  default UpdateLoanProposalCommand toUpdateCommand(UpdateLoanProposalMessageDto request) {
    if (request == null) {
      return null;
    }
    return new UpdateLoanProposalCommand(
        request.tracerId(),
        request.id(),
        request.loanProductId(),
        request.loanProductDetailsId(),
        request.loanProductPolicyId(),
        request.schemeId(),
        request.sectorId(),
        request.subSectorId(),
        request.frequencyId(),
        request.proposedLoanAmount(),
        request.proposedGrantAmount(),
        request.approvedGrantAmount(),
        request.preProposedLoanAmount(),
        request.interestRate(),
        request.numberOfInstallments(),
        request.installmentAmount(),
        request.recalculatedInstallmentAmount(),
        request.proposalDurationInMonths(),
        request.loanProposalType(),
        request.microInsurance(),
        request.policyTypeId(),
        request.insuranceProductId(),
        request.premiumAmount(),
        request.wantsFireInsurance(),
        request.fireInsuranceProductId(),
        request.fireInsuranceDetails(),
        request.modeOfPayment(),
        request.autoDebitCollection(),
        request.nominees(),
        request.guardian(),
        request.coBorrower(),
        request.secondInsurer(),
        request.specialSavingsAccountIds(),
        request.specialSavingsAccountNumbers(),
        request.countryId(),
        request.loanApproverId(),
        request.totalPovertyScore(),
        request.fieldOfficerId(),
        request.loanSecurityAmount(),
        request.loanSecurityBalance(),
        request.spousePrimaryIncomeSource(),
        request.spouseSecondaryIncomeSource(),
        request.firstChildName(),
        request.secondChildName(),
        request.largeGroupLeaderName(),
        request.largeGroupLeaderImage(),
        request.proposalReferenceNumber()
    );
  }
}
