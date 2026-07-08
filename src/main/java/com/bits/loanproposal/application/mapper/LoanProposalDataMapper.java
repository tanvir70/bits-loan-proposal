package com.bits.loanproposal.application.mapper;

import com.bits.loanproposal.application.command.CreateLoanProposalCommand;
import com.bits.loanproposal.application.command.DeleteLoanProposalCommand;
import com.bits.loanproposal.application.command.UpdateLoanProposalCommand;
import com.bits.loanproposal.application.dto.LoanProposalSourceData;
import com.bits.loanproposal.domain.entity.CoBorrower;
import com.bits.loanproposal.domain.entity.Guardian;
import com.bits.loanproposal.domain.entity.Nominee;
import com.bits.loanproposal.domain.entity.SecondInsurer;
import com.bits.loanproposal.domain.param.LoanProposalCreationData;
import com.bits.loanproposal.domain.param.LoanProposalDeletionData;
import com.bits.loanproposal.domain.param.LoanProposalUpdateData;
import com.bits.loanproposal.domain.value.AutoDebitCollection;
import com.bits.loanproposal.domain.value.FireInsuranceDetails;
import com.bits.loanproposal.domain.value.OtcModeOfPayment;
import com.bits.loanproposal.domain.value.ProgotiDocumentChecklist;
import com.bits.loanproposal.presentation.controller.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.util.List;

@Mapper(componentModel = "spring")
public interface LoanProposalDataMapper {

    @Mapping(target = "traceId", source = "command.tracerId")
    @Mapping(target = "branchCode", source = "sourceData.branch.code")
    @Mapping(target = "projectCode", source = "sourceData.project.code")
    @Mapping(target = "villageOrganisationCode", source = "sourceData.villageOrganisation.code")
    @Mapping(target = "isDigitalDisbursement", expression = "java(deriveIsDigital(command))")
    @Mapping(target = "transactionDescription", expression = "java(deriveTransactionDescription(command, sourceData))")
    @Mapping(target = "recalculatedInstallmentAmount", source = "command.installmentAmount")
    @Mapping(target = "guarantors", expression = "java(java.util.Collections.emptyList())")
    @Mapping(target = "loanProposalId", ignore = true)
    @Mapping(target = "proposalNumber", ignore = true)
    // not defined in ears: create request carries no premium; sourced from the selected
    // insurance product snapshot (the only premium on the creation path) — verify against legacy
    @Mapping(target = "premiumAmount", source = "sourceData.insuranceProduct.premiumAmount")
    @Mapping(target = "sequence", source = "sequence")
    @Mapping(target = "applicationDate", expression = "java(deriveApplicationDate(sourceData))")
    LoanProposalCreationData toCreationData(CreateLoanProposalCommand command, LoanProposalSourceData sourceData, long sequence);

    default LocalDate deriveApplicationDate(LoanProposalSourceData sourceData) {
        return sourceData != null && sourceData.getBranch() != null
                ? sourceData.getBranch().getLastAccountingBusinessDate()
                : null;
    }

    default LoanProposalUpdateData toUpdateData(UpdateLoanProposalCommand command, LoanProposalSourceData sourceData) {
        if (command == null) {
            return null;
        }
        return new LoanProposalUpdateData(
                command.getTracerId(),
                command.getId(),
                sourceData,
                command.getLoanProductId(),
                command.getLoanProductDetailsId(),
                command.getLoanProductPolicyId(),
                command.getSchemeId(),
                command.getSectorId(),
                command.getSubSectorId(),
                command.getFrequencyId(),
                command.getProposedLoanAmount(),
                command.getProposedGrantAmount(),
                command.getApprovedGrantAmount(),
                command.getPreProposedLoanAmount(),
                command.getInterestRate(),
                command.getNumberOfInstallments(),
                command.getInstallmentAmount(),
                command.getRecalculatedInstallmentAmount(),
                command.getProposalDurationInMonths(),
                command.getLoanProposalType(),
                command.getMicroInsurance(),
                command.getPolicyTypeId(),
                command.getInsuranceProductId(),
                command.getPremiumAmount(),
                command.getWantsFireInsurance(),
                command.getFireInsuranceProductId(),
                mapFireInsuranceDetails(command.getFireInsuranceDetails()),
                mapModeOfPayment(command.getModeOfPayment()),
                mapAutoDebitCollection(command.getAutoDebitCollection()),
                mapNominees(command.getNominees()),
                mapGuardian(command.getGuardian()),
                mapCoBorrower(command.getCoBorrower()),
                mapSecondInsurer(command.getSecondInsurer()),
                command.getSpecialSavingsAccountIds(),
                command.getSpecialSavingsAccountNumbers(),
                command.getCountryId(),
                command.getLoanApproverId(),
                command.getTotalPovertyScore(),
                command.getFieldOfficerId(),
                command.getLoanSecurityAmount(),
                command.getLoanSecurityBalance(),
                command.getSpousePrimaryIncomeSource(),
                command.getSpouseSecondaryIncomeSource(),
                command.getFirstChildName(),
                command.getSecondChildName(),
                command.getLargeGroupLeaderName(),
                command.getLargeGroupLeaderImage(),
                command.getProposalReferenceNumber()
        );
    }

    default LoanProposalDeletionData toDeletionData(DeleteLoanProposalCommand command) {
        if (command == null) {
            return null;
        }
        return new LoanProposalDeletionData(
                command.getTracerId(),
                command.getId(),
                command.getBranchId(),
                command.getDeletedBy(),
                java.time.LocalDateTime.now()
        );
    }

    // not defined in ears: the DDD-EARS doc only names derivedDigitalDisbursementFlag(modeOfPayment) without
    // defining it; this wallet-number/mode-id heuristic is a guess. Need to know verify from code base.
    default boolean deriveIsDigital(CreateLoanProposalCommand command) {
        return command.getModeOfPayment() != null && (command.getModeOfPayment().digitalDisbursementModeId() != null ||
                 command.getModeOfPayment().rocketWalletNumber() != null || command.getModeOfPayment().bkashWalletNumber() != null);
    }

    // not defined in ears: the "OTC-{branchCode}-{voCode}-{memberId}" . Need to know from code base
    default String deriveTransactionDescription(CreateLoanProposalCommand command, LoanProposalSourceData sourceData) {
        boolean isDigital = deriveIsDigital(command);
        if (!isDigital) return null;
        String branchCode = sourceData.getBranch() != null ? sourceData.getBranch().getCode() : null;
        String voCode = sourceData.getVillageOrganisation() != null ? sourceData.getVillageOrganisation().getCode() : null;
        if (branchCode == null || voCode == null || command.getMemberId() == null) {
            return null; // no description over a literal "null" segment
        }
        return String.format("OTC-%s-%s-%s", branchCode, voCode, command.getMemberId());
    }

    Nominee mapNominee(NomineeRequestDto dto);

    default List<Nominee> mapNominees(List<NomineeRequestDto> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream().map(this::mapNominee).toList();
    }

    Guardian mapGuardian(GuardianRequestDto dto);

    CoBorrower mapCoBorrower(CoBorrowerRequestDto dto);

    @Mapping(target = "isEngagedWithOtherLoans", constant = "false")
    @Mapping(target = "isEngagedWithOtherInsurance", constant = "false")
    @Mapping(target = "hasOtherLoanAccounts", constant = "false")
    SecondInsurer mapSecondInsurer(SecondInsurerRequestDto dto);

    OtcModeOfPayment mapModeOfPayment(OtcModeOfPaymentRequestDto dto);

    AutoDebitCollection mapAutoDebitCollection(AutoDebitCollectionRequestDto dto);

    ProgotiDocumentChecklist mapProgotiDocumentChecklist(ProgotiDocumentChecklistRequestDto dto);

    FireInsuranceDetails mapFireInsuranceDetails(FireInsuranceDetailsRequestDto dto);
}
