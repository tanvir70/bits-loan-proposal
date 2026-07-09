package com.bits.loanproposal.application.command;

import com.bits.ddd.shared.messaging.Command;
import com.bits.loanproposal.domain.enums.LoanProposalType;
import com.bits.loanproposal.presentation.controller.dto.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
public class UpdateLoanProposalCommand extends Command {
    private final String id;
    private final Long loanProductId;
    private final Long loanProductDetailsId;
    private final Long loanProductPolicyId;
    private final Long schemeId;
    private final Long sectorId;
    private final Long subSectorId;
    private final Long frequencyId;
    private final BigDecimal proposedLoanAmount;
    private final BigDecimal proposedGrantAmount;
    private final BigDecimal approvedGrantAmount;
    private final BigDecimal preProposedLoanAmount;
    private final BigDecimal interestRate;
    private final Integer numberOfInstallments;
    private final BigDecimal installmentAmount;
    private final BigDecimal recalculatedInstallmentAmount;
    private final Integer proposalDurationInMonths;
    private final LoanProposalType loanProposalType;
    private final Boolean microInsurance;
    private final Long policyTypeId;
    private final Long insuranceProductId;
    private final BigDecimal premiumAmount;
    private final Boolean wantsFireInsurance;
    private final Long fireInsuranceProductId;
    private final FireInsuranceDetailsRequestDto fireInsuranceDetails;
    private final OtcModeOfPaymentRequestDto modeOfPayment;
    private final AutoDebitCollectionRequestDto autoDebitCollection;
    private final List<NomineeRequestDto> nominees;
    private final GuardianRequestDto guardian;
    private final CoBorrowerRequestDto coBorrower;
    private final SecondInsurerRequestDto secondInsurer;
    private final List<String> specialSavingsAccountIds;
    private final List<String> specialSavingsAccountNumbers;
    private final Long countryId;
    private final Long loanApproverId;
    private final BigDecimal totalPovertyScore;
    private final Long fieldOfficerId;
    private final BigDecimal loanSecurityAmount;
    private final BigDecimal loanSecurityBalance;
    private final String spousePrimaryIncomeSource;
    private final String spouseSecondaryIncomeSource;
    private final String firstChildName;
    private final String secondChildName;
    private final String largeGroupLeaderName;
    private final String largeGroupLeaderImage;
    private final String proposalReferenceNumber;

    public UpdateLoanProposalCommand(
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
            String proposalReferenceNumber) {
        super(tracerId);
        this.id = id;
        this.loanProductId = loanProductId;
        this.loanProductDetailsId = loanProductDetailsId;
        this.loanProductPolicyId = loanProductPolicyId;
        this.schemeId = schemeId;
        this.sectorId = sectorId;
        this.subSectorId = subSectorId;
        this.frequencyId = frequencyId;
        this.proposedLoanAmount = proposedLoanAmount;
        this.proposedGrantAmount = proposedGrantAmount;
        this.approvedGrantAmount = approvedGrantAmount;
        this.preProposedLoanAmount = preProposedLoanAmount;
        this.interestRate = interestRate;
        this.numberOfInstallments = numberOfInstallments;
        this.installmentAmount = installmentAmount;
        this.recalculatedInstallmentAmount = recalculatedInstallmentAmount;
        this.proposalDurationInMonths = proposalDurationInMonths;
        this.loanProposalType = loanProposalType;
        this.microInsurance = microInsurance;
        this.policyTypeId = policyTypeId;
        this.insuranceProductId = insuranceProductId;
        this.premiumAmount = premiumAmount;
        this.wantsFireInsurance = wantsFireInsurance;
        this.fireInsuranceProductId = fireInsuranceProductId;
        this.fireInsuranceDetails = fireInsuranceDetails;
        this.modeOfPayment = modeOfPayment;
        this.autoDebitCollection = autoDebitCollection;
        this.nominees = nominees;
        this.guardian = guardian;
        this.coBorrower = coBorrower;
        this.secondInsurer = secondInsurer;
        this.specialSavingsAccountIds = specialSavingsAccountIds;
        this.specialSavingsAccountNumbers = specialSavingsAccountNumbers;
        this.countryId = countryId;
        this.loanApproverId = loanApproverId;
        this.totalPovertyScore = totalPovertyScore;
        this.fieldOfficerId = fieldOfficerId;
        this.loanSecurityAmount = loanSecurityAmount;
        this.loanSecurityBalance = loanSecurityBalance;
        this.spousePrimaryIncomeSource = spousePrimaryIncomeSource;
        this.spouseSecondaryIncomeSource = spouseSecondaryIncomeSource;
        this.firstChildName = firstChildName;
        this.secondChildName = secondChildName;
        this.largeGroupLeaderName = largeGroupLeaderName;
        this.largeGroupLeaderImage = largeGroupLeaderImage;
        this.proposalReferenceNumber = proposalReferenceNumber;
    }
}
