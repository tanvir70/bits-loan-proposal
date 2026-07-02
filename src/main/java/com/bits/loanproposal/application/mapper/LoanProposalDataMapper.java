package com.bits.loanproposal.application.mapper;

import com.bits.loanproposal.application.command.CreateLoanProposalCommand;
import com.bits.loanproposal.application.dto.LoanProposalSourceData;
import com.bits.loanproposal.domain.entity.*;
import com.bits.loanproposal.domain.valueobject.*;
import com.bits.loanproposal.domain.param.LoanProposalCreationData;
import com.bits.loanproposal.presentation.controller.dto.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class LoanProposalDataMapper {

    private LoanProposalDataMapper() {}

    public static LoanProposalCreationData toCreationData(CreateLoanProposalCommand command, LoanProposalSourceData sourceData) {
        String branchCode = sourceData.getBranch() != null ? sourceData.getBranch().getCode() : null;
        String projectCode = sourceData.getProject() != null ? sourceData.getProject().getCode() : null;
        String voCode = sourceData.getVillageOrganisation() != null ? sourceData.getVillageOrganisation().getCode() : null;
        
        // Derive digital disbursement flag and description
        boolean isDigital = command.getModeOfPayment() != null && 
                (command.getModeOfPayment().digitalDisbursementModeId() != null || 
                 command.getModeOfPayment().rocketWalletNumber() != null || 
                 command.getModeOfPayment().bkashWalletNumber() != null);

        String txDesc = isDigital ? String.format("OTC-%s-%s-%s", branchCode, voCode, command.getMemberId()) : null;

        return new LoanProposalCreationData(
            command.getId(),
            command.getTracerId(),
            null, // loanProposalId
            null, // proposalNumber
            command.getProposalReferenceNumber(),
            command.getBranchId(),
            branchCode,
            command.getProjectId(),
            projectCode,
            command.getVillageOrganisationId(),
            voCode,
            command.getMemberId(),
            command.getMemberClassificationId(),
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
            command.getInstallmentAmount(), // recalculatedInstallmentAmount defaults to supplied
            command.getProposalDurationInMonths(),
            command.getLoanProposalType(),
            command.getMicroInsurance(),
            command.getPolicyTypeId(),
            command.getInsuranceProductId(),
            null, // premiumAmount is calculated/looked up
            mapSecondInsurer(command.getSecondInsurer()),
            command.getWantsFireInsurance(),
            command.getFireInsuranceProductId(),
            mapFireInsuranceDetails(command.getFireInsuranceDetails()),
            mapModeOfPayment(command.getModeOfPayment()),
            mapAutoDebitCollection(command.getAutoDebitCollection()),
            isDigital,
            txDesc,
            mapNominees(command.getNominees()),
            mapGuardian(command.getGuardian()),
            mapCoBorrower(command.getCoBorrower()),
            Collections.emptyList(), // guarantors
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
            LocalDate.now(), // applicationDate default
            command.getDisbursementDate(),
            command.getVoDisbursementDate(),
            command.getFirstRepaymentDate(),
            command.getApprovalCode(),
            command.getTransactionNumber(),
            command.getScannedFileName(),
            command.getFlag(),
            command.getCohortMappingId(),
            command.getAssetPurchaseId(),
            command.getDisbursementSubStatus(),
            command.getLongitude(),
            command.getLatitude(),
            command.getReasonForLoan(),
            command.getNumberOfChildGoToSchool(),
            command.getNoOfPreviousLoanFromBrac(),
            command.getRcaEnabled(),
            command.getMemberMobileNumber(),
            command.getAddress(),
            command.getContactNo(),
            command.getVoLeaderId(),
            command.getVoLeaderName(),
            command.getSpouseContactNumber(),
            command.getEarner(),
            command.getOwnIncome(),
            command.getLoanUser(),
            command.getAgeType(),
            command.getIsNewInsurer(),
            command.getLoanRecommenderId(),
            command.getDisbursementRetryCount(),
            command.getDisbursedBy(),
            command.getBmNotVerifiedDisbursementReason(),
            command.getLoanAccountFound(),
            command.getDigitalDisbursementStatusId(),
            command.getDigitalDisbursementHoApprovalDate(),
            command.getDigitalDisbursementSignConsent(),
            command.getDigitalDisbursementBankInstructionDate(),
            command.getDigitalDisbursementHoApprovalBy(),
            command.getLoanReferrerName(),
            command.getLoanReferrerContactNo(),
            command.getVoToSpotDistanceInstruction(),
            command.getRejectionReason(),
            command.getSignConsent(),
            command.getConsentUrl(),
            mapProgotiDocumentChecklist(command.getProgotiDocumentChecklist()),
            command.getLoanAccountId(),
            command.getDisbursedAmount(),
            command.getApprovalLogId(),
            command.getChangeLogId()
        );
    }

    private static List<Nominee> mapNominees(List<NomineeRequestDto> dtos) {
        if (dtos == null) return new ArrayList<>();
        return dtos.stream().map(dto -> Nominee.builder()
            .id(dto.id())
            .name(dto.name())
            .relationshipId(dto.relationshipId())
            .sharePercentage(dto.sharePercentage())
            .insuranceTypes(dto.insuranceTypes())
            .build()
        ).collect(Collectors.toList());
    }

    private static Guardian mapGuardian(GuardianRequestDto dto) {
        if (dto == null) return null;
        return Guardian.builder()
            .id(dto.id())
            .name(dto.name())
            .relationshipId(dto.relationshipId())
            .nationalId(dto.nationalId())
            .dateOfBirth(dto.dateOfBirth())
            .build();
    }

    private static CoBorrower mapCoBorrower(CoBorrowerRequestDto dto) {
        if (dto == null) return null;
        return CoBorrower.builder()
            .id(dto.id())
            .name(dto.name())
            .relationshipId(dto.relationshipId())
            .nationalId(dto.nationalId())
            .dateOfBirth(dto.dateOfBirth())
            .build();
    }

    private static SecondInsurer mapSecondInsurer(SecondInsurerRequestDto dto) {
        if (dto == null) return null;
        return SecondInsurer.builder()
            .id(dto.id())
            .name(dto.name())
            .genderId(dto.genderId())
            .relationshipId(dto.relationshipId())
            .dateOfBirth(dto.dateOfBirth())
            .nationalId(dto.nationalId())
            .isEngagedWithOtherLoans(false)
            .isEngagedWithOtherInsurance(false)
            .hasOtherLoanAccounts(false)
            .build();
    }

    private static OtcModeOfPayment mapModeOfPayment(OtcModeOfPaymentRequestDto dto) {
        if (dto == null) return null;
        return new OtcModeOfPayment(
            dto.modeOfPaymentId(),
            dto.subType(),
            dto.bankAccountNumber(),
            dto.bankRoutingNumber(),
            dto.bankId(),
            dto.bankBranchId(),
            dto.paymentSubTypeNumber(),
            dto.paymentSubTypeDate(),
            dto.bkashWalletNumber(),
            dto.rocketWalletNumber(),
            dto.premiumModeOfPaymentId(),
            dto.digitalDisbursementModeId()
        );
    }

    private static AutoDebitCollection mapAutoDebitCollection(AutoDebitCollectionRequestDto dto) {
        if (dto == null) return null;
        return new AutoDebitCollection(
            dto.subType(),
            dto.memberBankManagementLinkId(),
            dto.chequeNumbers(),
            dto.micrNumbers(),
            dto.rocketWalletNumber()
        );
    }

    private static ProgotiDocumentChecklist mapProgotiDocumentChecklist(ProgotiDocumentChecklistRequestDto dto) {
        if (dto == null) return null;
        return new ProgotiDocumentChecklist(
            dto.commitmentLetter(),
            dto.collateralBond(),
            dto.bankStatement(),
            dto.securityCheck(),
            dto.originalDeed(),
            dto.bayaDeed(),
            dto.pittDeed(),
            dto.positionDeed(),
            dto.duplicateDocumentWithWithdrawalReceipt(),
            dto.dcr(),
            dto.dismissalForm(),
            dto.saOriginalPapers(),
            dto.rsOriginalPapers(),
            dto.taxReceipt(),
            dto.heirCertificate(),
            dto.stopRentOrAdvanceAgreement(),
            dto.seizedPropertyInvestigativeReport(),
            dto.other()
        );
    }

    private static FireInsuranceDetails mapFireInsuranceDetails(FireInsuranceDetailsRequestDto dto) {
        if (dto == null) return null;
        return new FireInsuranceDetails(
            dto.businessName(),
            dto.businessAddress(),
            dto.businessPhone(),
            dto.businessEmail(),
            dto.divisionId(),
            dto.districtId(),
            dto.thanaId(),
            dto.businessTypeId(),
            dto.constructionOfPremisesId(),
            dto.fireInsurancePremiumAmount(),
            dto.fireInsuranceInsuredAmount(),
            dto.durationOfFireInsurance(),
            dto.fireInsuranceProductName(),
            dto.bracCommissionAmount(),
            dto.memberCommissionAmount()
        );
    }
}
