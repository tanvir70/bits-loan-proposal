package com.bits.loanproposal.application.dto;

import com.bits.loanproposal.domain.enums.LoanProposalType;
import com.bits.loanproposal.presentation.controller.dto.*;

import java.math.BigDecimal;
import java.util.List;

public record UpdateLoanProposalMessageDto(
        String tracerId,
        String id,
        Long loanProductId,
        Long loanProductDetailsId,
        Long loanProductPolicyId,
        Long schemeId,
        Long sectorId,
        Long subSectorId,
        Long frequencyId,
        BigDecimal proposedLoanAmount,
        BigDecimal proposedGrantAmount,
        BigDecimal approvedGrantAmount,
        BigDecimal preProposedLoanAmount,
        BigDecimal interestRate,
        Integer numberOfInstallments,
        BigDecimal installmentAmount,
        BigDecimal recalculatedInstallmentAmount,
        Integer proposalDurationInMonths,
        LoanProposalType loanProposalType,
        Boolean microInsurance,
        Long policyTypeId,
        Long insuranceProductId,
        BigDecimal premiumAmount,
        Boolean wantsFireInsurance,
        Long fireInsuranceProductId,
        FireInsuranceDetailsRequestDto fireInsuranceDetails,
        OtcModeOfPaymentRequestDto modeOfPayment,
        AutoDebitCollectionRequestDto autoDebitCollection,
        List<NomineeRequestDto> nominees,
        GuardianRequestDto guardian,
        CoBorrowerRequestDto coBorrower,
        SecondInsurerRequestDto secondInsurer,
        List<String> specialSavingsAccountIds,
        List<String> specialSavingsAccountNumbers,
        Long countryId,
        Long loanApproverId,
        BigDecimal totalPovertyScore,
        Long fieldOfficerId,
        BigDecimal loanSecurityAmount,
        BigDecimal loanSecurityBalance,
        String spousePrimaryIncomeSource,
        String spouseSecondaryIncomeSource,
        String firstChildName,
        String secondChildName,
        String largeGroupLeaderName,
        String largeGroupLeaderImage,
        String proposalReferenceNumber
) {
}
