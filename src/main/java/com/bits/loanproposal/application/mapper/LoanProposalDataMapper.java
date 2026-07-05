package com.bits.loanproposal.application.mapper;

import com.bits.loanproposal.application.command.CreateLoanProposalCommand;
import com.bits.loanproposal.application.dto.LoanProposalSourceData;
import com.bits.loanproposal.domain.entity.*;
import com.bits.loanproposal.domain.value.*;
import com.bits.loanproposal.domain.param.LoanProposalCreationData;
import com.bits.loanproposal.presentation.controller.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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
    @Mapping(target = "premiumAmount", ignore = true)
    @Mapping(target = "applicationDate", expression = "java(java.time.LocalDate.now())")
    LoanProposalCreationData toCreationData(CreateLoanProposalCommand command, LoanProposalSourceData sourceData);

    // not defined in ears: the DDD-EARS doc only names derivedDigitalDisbursementFlag(modeOfPayment) without
    // defining it; this wallet-number/mode-id heuristic is a guess. Need to know verify from code base.
    default boolean deriveIsDigital(CreateLoanProposalCommand command) {
        return command.getModeOfPayment() != null && (command.getModeOfPayment().digitalDisbursementModeId() != null ||
                 command.getModeOfPayment().rocketWalletNumber() != null ||
                 command.getModeOfPayment().bkashWalletNumber() != null);
    }

    // not defined in ears: the "OTC-{branchCode}-{voCode}-{memberId}" . Need to know from code base
    default String deriveTransactionDescription(CreateLoanProposalCommand command, LoanProposalSourceData sourceData) {
        boolean isDigital = deriveIsDigital(command);
        if (!isDigital) return null;
        String branchCode = sourceData.getBranch() != null ? sourceData.getBranch().getCode() : null;
        String voCode = sourceData.getVillageOrganisation() != null ? sourceData.getVillageOrganisation().getCode() : null;
        return String.format("OTC-%s-%s-%s", branchCode, voCode, command.getMemberId());
    }

    Nominee mapNominee(NomineeRequestDto dto);

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
