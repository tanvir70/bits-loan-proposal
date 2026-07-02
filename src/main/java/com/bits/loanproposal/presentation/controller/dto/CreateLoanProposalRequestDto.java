package com.bits.loanproposal.presentation.controller.dto;

import com.bits.loanproposal.domain.enums.LoanProposalType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record CreateLoanProposalRequestDto(
    @NotBlank(message = "Buffer loan proposal id cannot be null.")
    String id,

    @NotNull(message = "Member Information could not be null.")
    Long memberId,

    @NotNull(message = "Project ID is required")
    Long projectId,

    @NotNull(message = "Member Classification not found.")
    @Min(value = 1, message = "Member Classification not found.")
    Long memberClassificationId,

    @NotNull(message = "Loan product id is required")
    @Min(value = 1, message = "Loan product id is required")
    Long loanProductId,

    @NotNull(message = "Loan product detail id is invalid.")
    @Min(value = 1, message = "Loan product detail id is invalid.")
    Long loanProductDetailsId,

    @NotNull(message = "Loan product policy id is required")
    Long loanProductPolicyId,

    @NotNull(message = "Scheme id is invalid.")
    @Min(value = 1, message = "Scheme id is invalid.")
    Long schemeId,

    @NotNull(message = "Frequency id is invalid.")
    @Min(value = 1, message = "Frequency id is invalid.")
    Long frequencyId,

    @NotNull(message = "Interest rate is invalid.")
    @Min(value = 0, message = "Interest rate is invalid.")
    BigDecimal interestRate,

    @NotNull(message = "Installment amount must be greater than zero.")
    @DecimalMin(value = "0.01", message = "Installment amount must be greater than zero.")
    BigDecimal installmentAmount,

    @Min(value = 1, message = "Proposal duration in months is invalid.")
    Integer proposalDurationInMonths,

    @NotNull(message = "Proposed loan amount is invalid.")
    @Min(value = 0, message = "Proposed loan amount is invalid.")
    BigDecimal proposedLoanAmount,

    @Min(value = 0, message = "Proposed grant amount is invalid.")
    BigDecimal proposedGrantAmount,

    @Min(value = 0, message = "Pre-proposed loan amount is invalid.")
    BigDecimal preProposedLoanAmount,

    @Min(value = 1, message = "Sector id is invalid.")
    Long sectorId,

    @Min(value = 0, message = "Sub Sector id is invalid.")
    Long subSectorId,

    @Min(value = 0, message = "Total poverty score is invalid.")
    BigDecimal totalPovertyScore,

    @Min(value = 1, message = "Branch id is invalid.")
    Long branchId,

    @Min(value = 0, message = "VO id is invalid.")
    Long villageOrganisationId,

    @Min(value = 0, message = "PO id is invalid.")
    Long fieldOfficerId,

    @Min(value = 0, message = "Approved grant amount is invalid.")
    BigDecimal approvedGrantAmount,

    @Min(value = 0, message = "Loan security amount is invalid.")
    BigDecimal loanSecurityAmount,

    @Min(value = 0, message = "Loan security balance is invalid.")
    BigDecimal loanSecurityBalance,

    @Min(value = 0, message = "Number of installment is invalid.")
    Integer numberOfInstallments,

    @Min(value = 1, message = "Loan account id is invalid.")
    Long loanAccountId,

    @Min(value = 1, message = "Insurance product id is invalid.")
    Long insuranceProductId,

    @Min(value = 1, message = "Bank id is invalid.")
    Long bankId,

    @Min(value = 1, message = "Bank branch id is invalid.")
    Long bankBranchId,

    @Size(max = 100, message = "Can have max 100 characters")
    String spousePrimaryIncomeSource,

    @Size(max = 100, message = "Can have max 100 characters")
    String spouseSecondaryIncomeSource,

    @Size(max = 100, message = "Can have max 100 characters")
    String firstChildName,

    @Size(max = 100, message = "Can have max 100 characters")
    String secondChildName,

    @Size(max = 100, message = "Can have max 100 characters")
    String largeGroupLeaderName,

    @Size(max = 100, message = "Can have max 100 characters")
    String largeGroupLeaderImage,

    @Valid
    List<NomineeRequestDto> nominees,

    @Valid
    GuardianRequestDto guardian,

    @Valid
    CoBorrowerRequestDto coBorrower,

    @Valid
    SecondInsurerRequestDto secondInsurer,

    @Valid
    OtcModeOfPaymentRequestDto modeOfPayment,

    @Valid
    AutoDebitCollectionRequestDto autoDebitCollection,

    @Valid
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
    Long changeLogId
) {}
