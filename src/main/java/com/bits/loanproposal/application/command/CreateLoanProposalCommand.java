package com.bits.loanproposal.application.command;

import com.bits.ddd.shared.messaging.CommandMessage;
import com.bits.loanproposal.domain.enums.LoanProposalType;
import com.bits.loanproposal.presentation.controller.dto.*;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CreateLoanProposalCommand extends CommandMessage {
    private final String id;
    private final Long memberId;
    private final Long projectId;
    private final Long memberClassificationId;
    private final Long loanProductId;
    private final Long loanProductDetailsId;
    private final Long loanProductPolicyId;
    private final Long schemeId;
    private final Long frequencyId;
    private final BigDecimal interestRate;
    private final BigDecimal installmentAmount;
    private final Integer proposalDurationInMonths;
    private final BigDecimal proposedLoanAmount;
    private final BigDecimal proposedGrantAmount;
    private final BigDecimal preProposedLoanAmount;
    private final Long sectorId;
    private final Long subSectorId;
    private final BigDecimal totalPovertyScore;
    private final Long branchId;
    private final Long villageOrganisationId;
    private final Long fieldOfficerId;
    private final BigDecimal approvedGrantAmount;
    private final BigDecimal loanSecurityAmount;
    private final BigDecimal loanSecurityBalance;
    private final Integer numberOfInstallments;
    private final Long loanAccountId;
    private final Long insuranceProductId;
    private final Long bankId;
    private final Long bankBranchId;
    private final String spousePrimaryIncomeSource;
    private final String spouseSecondaryIncomeSource;
    private final String firstChildName;
    private final String secondChildName;
    private final String largeGroupLeaderName;
    private final String largeGroupLeaderImage;
    private final List<NomineeRequestDto> nominees;
    private final GuardianRequestDto guardian;
    private final CoBorrowerRequestDto coBorrower;
    private final SecondInsurerRequestDto secondInsurer;
    private final OtcModeOfPaymentRequestDto modeOfPayment;
    private final AutoDebitCollectionRequestDto autoDebitCollection;
    private final FireInsuranceDetailsRequestDto fireInsuranceDetails;
    private final String proposalReferenceNumber;
    private final LoanProposalType loanProposalType;
    private final Boolean microInsurance;
    private final Long policyTypeId;
    private final Boolean wantsFireInsurance;
    private final Long fireInsuranceProductId;
    private final List<String> specialSavingsAccountIds;
    private final List<String> specialSavingsAccountNumbers;
    private final Long countryId;
    private final Long loanApproverId;
    private final LocalDate applicationDate;
    private final LocalDate disbursementDate;
    private final LocalDate voDisbursementDate;
    private final LocalDate firstRepaymentDate;
    private final String approvalCode;
    private final String transactionNumber;
    private final String scannedFileName;
    private final Integer flag;
    private final Long cohortMappingId;
    private final Long assetPurchaseId;
    private final Integer disbursementSubStatus;
    private final String longitude;
    private final String latitude;
    private final String reasonForLoan;
    private final Integer numberOfChildGoToSchool;
    private final Integer noOfPreviousLoanFromBrac;
    private final Boolean rcaEnabled;
    private final String memberMobileNumber;
    private final String address;
    private final String contactNo;
    private final Long voLeaderId;
    private final String voLeaderName;
    private final String spouseContactNumber;
    private final Integer earner;
    private final BigDecimal ownIncome;
    private final Integer loanUser;
    private final Integer ageType;
    private final Boolean isNewInsurer;
    private final Long loanRecommenderId;
    private final Integer disbursementRetryCount;
    private final String disbursedBy;
    private final String bmNotVerifiedDisbursementReason;
    private final Boolean loanAccountFound;
    private final Integer digitalDisbursementStatusId;
    private final LocalDateTime digitalDisbursementHoApprovalDate;
    private final Boolean digitalDisbursementSignConsent;
    private final LocalDateTime digitalDisbursementBankInstructionDate;
    private final String digitalDisbursementHoApprovalBy;
    private final String loanReferrerName;
    private final String loanReferrerContactNo;
    private final String voToSpotDistanceInstruction;
    private final String rejectionReason;
    private final Boolean signConsent;
    private final String consentUrl;
    private final ProgotiDocumentChecklistRequestDto progotiDocumentChecklist;
    private final BigDecimal disbursedAmount;
    private final Long approvalLogId;
    private final Long changeLogId;

    public CreateLoanProposalCommand(
            String tracerId,
            String id,
            Long memberId,
            Long projectId,
            Long memberClassificationId,
            Long loanProductId,
            Long loanProductDetailsId,
            Long loanProductPolicyId,
            Long schemeId,
            Long frequencyId,
            BigDecimal interestRate,
            BigDecimal installmentAmount,
            Integer proposalDurationInMonths,
            BigDecimal proposedLoanAmount,
            BigDecimal proposedGrantAmount,
            BigDecimal preProposedLoanAmount,
            Long sectorId,
            Long subSectorId,
            BigDecimal totalPovertyScore,
            Long branchId,
            Long villageOrganisationId,
            Long fieldOfficerId,
            BigDecimal approvedGrantAmount,
            BigDecimal loanSecurityAmount,
            BigDecimal loanSecurityBalance,
            Integer numberOfInstallments,
            Long loanAccountId,
            Long insuranceProductId,
            Long bankId,
            Long bankBranchId,
            String spousePrimaryIncomeSource,
            String spouseSecondaryIncomeSource,
            String firstChildName,
            String secondChildName,
            String largeGroupLeaderName,
            String largeGroupLeaderImage,
            List<NomineeRequestDto> nominees,
            GuardianRequestDto guardian,
            CoBorrowerRequestDto coBorrower,
            SecondInsurerRequestDto secondInsurer,
            OtcModeOfPaymentRequestDto modeOfPayment,
            AutoDebitCollectionRequestDto autoDebitCollection,
            FireInsuranceDetailsRequestDto fireInsuranceDetails,
            String proposalReferenceNumber,
            LoanProposalType loanProposalType,
            Boolean microInsurance,
            Long policyTypeId,
            Boolean wantsFireInsurance,
            Long fireInsuranceProductId,
            List<String> specialSavingsAccountIds,
            List<String> specialSavingsAccountNumbers,
            Long countryId,
            Long loanApproverId,
            LocalDate applicationDate,
            LocalDate disbursementDate,
            LocalDate voDisbursementDate,
            LocalDate firstRepaymentDate,
            String approvalCode,
            String transactionNumber,
            String scannedFileName,
            Integer flag,
            Long cohortMappingId,
            Long assetPurchaseId,
            Integer disbursementSubStatus,
            String longitude,
            String latitude,
            String reasonForLoan,
            Integer numberOfChildGoToSchool,
            Integer noOfPreviousLoanFromBrac,
            Boolean rcaEnabled,
            String memberMobileNumber,
            String address,
            String contactNo,
            Long voLeaderId,
            String voLeaderName,
            String spouseContactNumber,
            Integer earner,
            BigDecimal ownIncome,
            Integer loanUser,
            Integer ageType,
            Boolean isNewInsurer,
            Long loanRecommenderId,
            Integer disbursementRetryCount,
            String disbursedBy,
            String bmNotVerifiedDisbursementReason,
            Boolean loanAccountFound,
            Integer digitalDisbursementStatusId,
            LocalDateTime digitalDisbursementHoApprovalDate,
            Boolean digitalDisbursementSignConsent,
            LocalDateTime digitalDisbursementBankInstructionDate,
            String digitalDisbursementHoApprovalBy,
            String loanReferrerName,
            String loanReferrerContactNo,
            String voToSpotDistanceInstruction,
            String rejectionReason,
            Boolean signConsent,
            String consentUrl,
            ProgotiDocumentChecklistRequestDto progotiDocumentChecklist,
            BigDecimal disbursedAmount,
            Long approvalLogId,
            Long changeLogId) {
        super(tracerId);
        this.id = id;
        this.memberId = memberId;
        this.projectId = projectId;
        this.memberClassificationId = memberClassificationId;
        this.loanProductId = loanProductId;
        this.loanProductDetailsId = loanProductDetailsId;
        this.loanProductPolicyId = loanProductPolicyId;
        this.schemeId = schemeId;
        this.frequencyId = frequencyId;
        this.interestRate = interestRate;
        this.installmentAmount = installmentAmount;
        this.proposalDurationInMonths = proposalDurationInMonths;
        this.proposedLoanAmount = proposedLoanAmount;
        this.proposedGrantAmount = proposedGrantAmount;
        this.preProposedLoanAmount = preProposedLoanAmount;
        this.sectorId = sectorId;
        this.subSectorId = subSectorId;
        this.totalPovertyScore = totalPovertyScore;
        this.branchId = branchId;
        this.villageOrganisationId = villageOrganisationId;
        this.fieldOfficerId = fieldOfficerId;
        this.approvedGrantAmount = approvedGrantAmount;
        this.loanSecurityAmount = loanSecurityAmount;
        this.loanSecurityBalance = loanSecurityBalance;
        this.numberOfInstallments = numberOfInstallments;
        this.loanAccountId = loanAccountId;
        this.insuranceProductId = insuranceProductId;
        this.bankId = bankId;
        this.bankBranchId = bankBranchId;
        this.spousePrimaryIncomeSource = spousePrimaryIncomeSource;
        this.spouseSecondaryIncomeSource = spouseSecondaryIncomeSource;
        this.firstChildName = firstChildName;
        this.secondChildName = secondChildName;
        this.largeGroupLeaderName = largeGroupLeaderName;
        this.largeGroupLeaderImage = largeGroupLeaderImage;
        this.nominees = nominees;
        this.guardian = guardian;
        this.coBorrower = coBorrower;
        this.secondInsurer = secondInsurer;
        this.modeOfPayment = modeOfPayment;
        this.autoDebitCollection = autoDebitCollection;
        this.fireInsuranceDetails = fireInsuranceDetails;
        this.proposalReferenceNumber = proposalReferenceNumber;
        this.loanProposalType = loanProposalType;
        this.microInsurance = microInsurance;
        this.policyTypeId = policyTypeId;
        this.wantsFireInsurance = wantsFireInsurance;
        this.fireInsuranceProductId = fireInsuranceProductId;
        this.specialSavingsAccountIds = specialSavingsAccountIds;
        this.specialSavingsAccountNumbers = specialSavingsAccountNumbers;
        this.countryId = countryId;
        this.loanApproverId = loanApproverId;
        this.applicationDate = applicationDate;
        this.disbursementDate = disbursementDate;
        this.voDisbursementDate = voDisbursementDate;
        this.firstRepaymentDate = firstRepaymentDate;
        this.approvalCode = approvalCode;
        this.transactionNumber = transactionNumber;
        this.scannedFileName = scannedFileName;
        this.flag = flag;
        this.cohortMappingId = cohortMappingId;
        this.assetPurchaseId = assetPurchaseId;
        this.disbursementSubStatus = disbursementSubStatus;
        this.longitude = longitude;
        this.latitude = latitude;
        this.reasonForLoan = reasonForLoan;
        this.numberOfChildGoToSchool = numberOfChildGoToSchool;
        this.noOfPreviousLoanFromBrac = noOfPreviousLoanFromBrac;
        this.rcaEnabled = rcaEnabled;
        this.memberMobileNumber = memberMobileNumber;
        this.address = address;
        this.contactNo = contactNo;
        this.voLeaderId = voLeaderId;
        this.voLeaderName = voLeaderName;
        this.spouseContactNumber = spouseContactNumber;
        this.earner = earner;
        this.ownIncome = ownIncome;
        this.loanUser = loanUser;
        this.ageType = ageType;
        this.isNewInsurer = isNewInsurer;
        this.loanRecommenderId = loanRecommenderId;
        this.disbursementRetryCount = disbursementRetryCount;
        this.disbursedBy = disbursedBy;
        this.bmNotVerifiedDisbursementReason = bmNotVerifiedDisbursementReason;
        this.loanAccountFound = loanAccountFound;
        this.digitalDisbursementStatusId = digitalDisbursementStatusId;
        this.digitalDisbursementHoApprovalDate = digitalDisbursementHoApprovalDate;
        this.digitalDisbursementSignConsent = digitalDisbursementSignConsent;
        this.digitalDisbursementBankInstructionDate = digitalDisbursementBankInstructionDate;
        this.digitalDisbursementHoApprovalBy = digitalDisbursementHoApprovalBy;
        this.loanReferrerName = loanReferrerName;
        this.loanReferrerContactNo = loanReferrerContactNo;
        this.voToSpotDistanceInstruction = voToSpotDistanceInstruction;
        this.rejectionReason = rejectionReason;
        this.signConsent = signConsent;
        this.consentUrl = consentUrl;
        this.progotiDocumentChecklist = progotiDocumentChecklist;
        this.disbursedAmount = disbursedAmount;
        this.approvalLogId = approvalLogId;
        this.changeLogId = changeLogId;
    }
}
