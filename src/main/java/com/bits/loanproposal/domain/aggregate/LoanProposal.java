package com.bits.loanproposal.domain.aggregate;

import com.bits.ddd.aggregate.AggregateRoot;
import com.bits.ddd.annotation.Aggregate;
import com.bits.ddd.shared.domain.value.DomainStatus;
import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.loanproposal.application.dto.LoanProposalSourceData;
import com.bits.loanproposal.domain.entity.CoBorrower;
import com.bits.loanproposal.domain.entity.Guarantor;
import com.bits.loanproposal.domain.entity.Guardian;
import com.bits.loanproposal.domain.entity.Nominee;
import com.bits.loanproposal.domain.entity.SecondInsurer;
import com.bits.loanproposal.domain.enums.ApiDataSource;
import com.bits.loanproposal.domain.enums.LoanProposalStatus;
import com.bits.loanproposal.domain.enums.LoanProposalType;
import com.bits.loanproposal.domain.exception.LoanProposalValidationException;
import com.bits.loanproposal.domain.mapper.LoanProposalEventMapper;
import com.bits.loanproposal.domain.param.LoanProposalCreationData;
import com.bits.loanproposal.domain.param.LoanProposalDeletionData;
import com.bits.loanproposal.domain.param.LoanProposalUpdateData;
import com.bits.loanproposal.domain.specification.context.LoanProposalValidationContext;
import com.bits.loanproposal.domain.specification.rules.AgeLimitSpecification;
import com.bits.loanproposal.domain.specification.rules.BankModeOfPaymentSpecification;
import com.bits.loanproposal.domain.specification.rules.BranchProjectVoConsistencySpecification;
import com.bits.loanproposal.domain.specification.rules.CoBorrowerSpecification;
import com.bits.loanproposal.domain.specification.rules.DigitalDisbursementSpecification;
import com.bits.loanproposal.domain.specification.rules.FireInsuranceSpecification;
import com.bits.loanproposal.domain.specification.rules.InstallmentConfigurationSpecification;
import com.bits.loanproposal.domain.specification.rules.InsurancePolicyTypeSecondInsurerSpecification;
import com.bits.loanproposal.domain.specification.rules.LoanAmountSpecification;
import com.bits.loanproposal.domain.specification.rules.LoanExposureLimitSpecification;
import com.bits.loanproposal.domain.specification.rules.LoanProductPolicySpecification;
import com.bits.loanproposal.domain.specification.rules.MemberEligibilitySpecification;
import com.bits.loanproposal.domain.specification.rules.MigrationCountrySpecification;
import com.bits.loanproposal.domain.specification.rules.ModeOfPaymentRocketWalletSpecification;
import com.bits.loanproposal.domain.specification.rules.MoneyPlantSpecification;
import com.bits.loanproposal.domain.specification.rules.NomineeSpecification;
import com.bits.loanproposal.domain.specification.rules.ParallelCoExistingLoanSpecification;
import com.bits.loanproposal.domain.specification.rules.ProjectSpecificRulesSpecification;
import com.bits.loanproposal.domain.specification.rules.RepaymentFrequencyModeOfPaymentSpecification;
import com.bits.loanproposal.domain.specification.rules.SchemeSectorMappingSpecification;
import com.bits.loanproposal.domain.specification.rules.SpecialSavingsLienSpecification;
import com.bits.loanproposal.domain.value.AutoDebitCollection;
import com.bits.loanproposal.domain.value.FireInsuranceDetails;
import com.bits.loanproposal.domain.value.OtcModeOfPayment;
import com.bits.loanproposal.domain.value.ProgotiDocumentChecklist;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static com.bits.loanproposal.domain.constant.DomainErrorConstant.DELETE_FAILED;
import static com.bits.loanproposal.domain.constant.DomainErrorConstant.ID_NULL;
import static com.bits.loanproposal.domain.constant.DomainErrorConstant.LOAN_PROPOSAL_UPDATE_FAILED;
import static com.bits.loanproposal.domain.constant.DomainErrorConstant.PROPOSAL_ID_MUST_NOT_BE_NULL;
import static com.bits.loanproposal.domain.constant.DomainErrorConstant.APPROVE_FAILED;
import static com.bits.loanproposal.domain.constant.DomainErrorConstant.UPDATE_FAILED;

@Getter
@Setter
@NoArgsConstructor
@Aggregate
@Document(collection = "loan_proposal")
public class LoanProposal extends AggregateRoot<String> {

    @Id
    private String id;
    private Long loanProposalId;
    private String proposalNumber;
    private String proposalReferenceNumber;
    private Long branchId;
    private String branchCode;
    private Long projectId;
    private String projectCode;
    private Long villageOrganisationId;
    private String villageOrganisationCode;
    private Long memberId;
    private Long memberClassificationId;
    private Long loanProductId;
    private Long loanProductDetailsId;
    private Long loanProductPolicyId;
    private Long schemeId;
    private Long sectorId;
    private Long subSectorId;
    private Long frequencyId;
    private BigDecimal proposedLoanAmount;
    private BigDecimal approvedLoanAmount;
    private BigDecimal proposedGrantAmount;
    private BigDecimal approvedGrantAmount;
    private BigDecimal preProposedLoanAmount;
    private BigDecimal interestRate;
    private Integer numberOfInstallments;
    private Integer approvedNumberOfInstallments;
    private BigDecimal installmentAmount;
    private BigDecimal approvedInstallmentAmount;
    private Integer proposalDurationInMonths;
    private Integer approvedDurationInMonths;
    private LoanProposalStatus loanProposalStatus;
    private LoanProposalType loanProposalType;
    private String approvalFlowStatus;
    private String approvalStatus;
    private ApiDataSource dataSource;
    private Boolean microInsurance;
    private Long policyTypeId;
    private Long insuranceProductId;
    private BigDecimal premiumAmount;
    private SecondInsurer secondInsurer;
    private Boolean wantsFireInsurance;
    private Long fireInsuranceProductId;
    private FireInsuranceDetails fireInsuranceDetails;
    private OtcModeOfPayment modeOfPayment;
    private AutoDebitCollection autoDebitCollection;
    private Boolean isDigitalDisbursement;
    private String transactionDescription;
    private List<Nominee> nominees;
    private Guardian guardian;
    private CoBorrower coBorrower;
    private List<Guarantor> guarantors;
    private List<String> specialSavingsAccountIds;
    private List<String> specialSavingsAccountNumbers;
    private Long countryId;
    private Long loanApproverId;
    private BigDecimal totalPovertyScore;
    private Long fieldOfficerId;
    private BigDecimal loanSecurityAmount;
    private BigDecimal loanSecurityBalance;
    private String spousePrimaryIncomeSource;
    private String spouseSecondaryIncomeSource;
    private String firstChildName;
    private String secondChildName;
    private String largeGroupLeaderName;
    private String largeGroupLeaderImage;
    private String assetBufferId;
    private LocalDate applicationDate;
    private LocalDate disbursementDate;
    private LocalDate voDisbursementDate;
    private LocalDate firstRepaymentDate;
    private String approvalCode;
    private String transactionNumber;
    private String scannedFileName;
    private Integer flag;
    private Long cohortMappingId;
    private Long assetPurchaseId;
    private Integer disbursementSubStatus;
    private String longitude;
    private String latitude;
    private String reasonForLoan;
    private Integer numberOfChildGoToSchool;
    private Integer noOfPreviousLoanFromBrac;
    private Boolean rcaEnabled;
    private String memberMobileNumber;
    private String address;
    private String contactNo;
    private Long voLeaderId;
    private String voLeaderName;
    private String spouseContactNumber;
    private Integer earner;
    private BigDecimal ownIncome;
    private Integer loanUser;
    private Integer ageType;
    private Boolean isNewInsurer;
    private Long loanRecommenderId;
    private Integer disbursementRetryCount;
    private String disbursedBy;
    private String bmNotVerifiedDisbursementReason;
    private Boolean loanAccountFound;
    private Integer digitalDisbursementStatusId;
    private LocalDateTime digitalDisbursementHoApprovalDate;
    private Boolean digitalDisbursementSignConsent;
    private LocalDateTime digitalDisbursementBankInstructionDate;
    private String digitalDisbursementHoApprovalBy;
    private String loanReferrerName;
    private String loanReferrerContactNo;
    private String voToSpotDistanceInstruction;
    private String rejectionReason;
    private Boolean signConsent;
    private String consentUrl;
    private ProgotiDocumentChecklist progotiDocumentChecklist;
    private Long loanAccountId;
    private BigDecimal disbursedAmount;
    private Long approvalLogId;
    private Long changeLogId;
    private Boolean deleted;
    private String deletedBy;
    private LocalDateTime deletedAt;

    public static LoanProposal create(LoanProposalCreationData creationData, LoanProposalSourceData sourceData) {
        if (creationData.id() == null) {
            throw new LoanProposalValidationException(ID_NULL, PROPOSAL_ID_MUST_NOT_BE_NULL);
        }
        LoanProposal proposal = new LoanProposal();
        proposal.id = creationData.id();
        proposal.loanProposalId = creationData.loanProposalId();
        proposal.proposalNumber = generateProposalNumber(creationData.applicationDate(), creationData.sequence());
        proposal.proposalReferenceNumber = creationData.proposalReferenceNumber();
        proposal.branchId = creationData.branchId();
        proposal.branchCode = creationData.branchCode();
        proposal.projectId = creationData.projectId();
        proposal.projectCode = creationData.projectCode();
        proposal.villageOrganisationId = creationData.villageOrganisationId();
        proposal.villageOrganisationCode = creationData.villageOrganisationCode();
        proposal.memberId = creationData.memberId();
        proposal.memberClassificationId = creationData.memberClassificationId();
        proposal.loanProductId = creationData.loanProductId();
        proposal.loanProductDetailsId = creationData.loanProductDetailsId();
        proposal.loanProductPolicyId = creationData.loanProductPolicyId();
        proposal.schemeId = creationData.schemeId();
        proposal.sectorId = creationData.sectorId();
        proposal.subSectorId = creationData.subSectorId();
        proposal.frequencyId = creationData.frequencyId();
        proposal.proposedLoanAmount = creationData.proposedLoanAmount();
        proposal.approvedLoanAmount = creationData.proposedLoanAmount();
        proposal.proposedGrantAmount = creationData.proposedGrantAmount();
        proposal.approvedGrantAmount = creationData.approvedGrantAmount();
        proposal.preProposedLoanAmount = creationData.preProposedLoanAmount();
        proposal.interestRate = creationData.interestRate();
        proposal.numberOfInstallments = creationData.numberOfInstallments();
        proposal.approvedNumberOfInstallments = creationData.numberOfInstallments();
        proposal.installmentAmount = creationData.installmentAmount();
        proposal.approvedInstallmentAmount = creationData.recalculatedInstallmentAmount();
        proposal.proposalDurationInMonths = creationData.proposalDurationInMonths();
        proposal.approvedDurationInMonths = creationData.proposalDurationInMonths();
        proposal.loanProposalStatus = LoanProposalStatus.PENDING;
        proposal.loanProposalType = creationData.loanProposalType() != null ? creationData.loanProposalType() : LoanProposalType.NORMAL_LOAN;
        proposal.dataSource = ApiDataSource.OTC;
        proposal.status = DomainStatus.CREATED;
        // library AggregateRoot fields: persistence NPEs on a null version
        proposal.version = 0L;
        proposal.tracerId = creationData.traceId();
        proposal.microInsurance = creationData.microInsurance();
        proposal.policyTypeId = creationData.policyTypeId();
        proposal.insuranceProductId = creationData.insuranceProductId();
        proposal.premiumAmount = creationData.premiumAmount();
        proposal.secondInsurer = creationData.secondInsurer();
        proposal.wantsFireInsurance = creationData.wantsFireInsurance();
        proposal.fireInsuranceProductId = creationData.fireInsuranceProductId();
        proposal.fireInsuranceDetails = defaultFireInsuranceDetails(creationData.fireInsuranceDetails(),
                creationData.proposedLoanAmount(), creationData.proposalDurationInMonths());
        proposal.modeOfPayment = creationData.modeOfPayment();
        proposal.autoDebitCollection = creationData.autoDebitCollection();
        proposal.isDigitalDisbursement = creationData.isDigitalDisbursement();
        proposal.transactionDescription = creationData.transactionDescription();
        proposal.nominees = assignNomineeIds(creationData.nominees());
        proposal.guardian = linkGuardianToFirstNominee(creationData.guardian(), proposal.nominees);
        proposal.coBorrower = assignCoBorrowerId(creationData.coBorrower());
        proposal.guarantors = creationData.guarantors();
        proposal.specialSavingsAccountIds = creationData.specialSavingsAccountIds();
        proposal.specialSavingsAccountNumbers = creationData.specialSavingsAccountNumbers();
        proposal.countryId = creationData.countryId();
        proposal.loanApproverId = creationData.loanApproverId();
        proposal.totalPovertyScore = creationData.totalPovertyScore();
        proposal.fieldOfficerId = creationData.fieldOfficerId();
        proposal.loanSecurityAmount = creationData.loanSecurityAmount() != null ? creationData.loanSecurityAmount() : BigDecimal.ZERO;
        proposal.loanSecurityBalance = creationData.loanSecurityBalance() != null ? creationData.loanSecurityBalance() : BigDecimal.ZERO;
        proposal.spousePrimaryIncomeSource = creationData.spousePrimaryIncomeSource();
        proposal.spouseSecondaryIncomeSource = creationData.spouseSecondaryIncomeSource();
        proposal.firstChildName = creationData.firstChildName();
        proposal.secondChildName = creationData.secondChildName();
        proposal.largeGroupLeaderName = creationData.largeGroupLeaderName();
        proposal.largeGroupLeaderImage = creationData.largeGroupLeaderImage();
        proposal.applicationDate = creationData.applicationDate();
        proposal.disbursementDate = creationData.disbursementDate();
        proposal.voDisbursementDate = creationData.voDisbursementDate();
        proposal.firstRepaymentDate = creationData.firstRepaymentDate();
        proposal.approvalCode = creationData.approvalCode();
        proposal.transactionNumber = creationData.transactionNumber();
        proposal.scannedFileName = creationData.scannedFileName();
        proposal.flag = creationData.flag();
        proposal.cohortMappingId = creationData.cohortMappingId();
        proposal.assetPurchaseId = creationData.assetPurchaseId();
        proposal.disbursementSubStatus = creationData.disbursementSubStatus();
        proposal.longitude = creationData.longitude();
        proposal.latitude = creationData.latitude();
        proposal.reasonForLoan = creationData.reasonForLoan();
        proposal.numberOfChildGoToSchool = creationData.numberOfChildGoToSchool();
        proposal.noOfPreviousLoanFromBrac = creationData.noOfPreviousLoanFromBrac();
        proposal.rcaEnabled = creationData.rcaEnabled();
        proposal.memberMobileNumber = creationData.memberMobileNumber();
        proposal.address = creationData.address();
        proposal.contactNo = creationData.contactNo();
        proposal.voLeaderId = creationData.voLeaderId();
        proposal.voLeaderName = creationData.voLeaderName();
        proposal.spouseContactNumber = creationData.spouseContactNumber();
        proposal.earner = creationData.earner();
        proposal.ownIncome = creationData.ownIncome();
        proposal.loanUser = creationData.loanUser();
        proposal.ageType = creationData.ageType();
        proposal.isNewInsurer = creationData.isNewInsurer();
        proposal.loanRecommenderId = creationData.loanRecommenderId();
        proposal.disbursementRetryCount = creationData.disbursementRetryCount() != null ? creationData.disbursementRetryCount() : 0;
        proposal.disbursedBy = creationData.disbursedBy();
        proposal.bmNotVerifiedDisbursementReason = creationData.bmNotVerifiedDisbursementReason();
        proposal.loanAccountFound = creationData.loanAccountFound();
        proposal.digitalDisbursementStatusId = creationData.digitalDisbursementStatusId();
        proposal.digitalDisbursementHoApprovalDate = creationData.digitalDisbursementHoApprovalDate();
        proposal.digitalDisbursementSignConsent = creationData.digitalDisbursementSignConsent();
        proposal.digitalDisbursementBankInstructionDate = creationData.digitalDisbursementBankInstructionDate();
        proposal.digitalDisbursementHoApprovalBy = creationData.digitalDisbursementHoApprovalBy();
        proposal.loanReferrerName = creationData.loanReferrerName();
        proposal.loanReferrerContactNo = creationData.loanReferrerContactNo();
        proposal.voToSpotDistanceInstruction = creationData.voToSpotDistanceInstruction();
        proposal.rejectionReason = creationData.rejectionReason();
        proposal.signConsent = creationData.signConsent();
        proposal.consentUrl = creationData.consentUrl();
        proposal.progotiDocumentChecklist = creationData.progotiDocumentChecklist();
        proposal.loanAccountId = creationData.loanAccountId();
        proposal.disbursedAmount = creationData.disbursedAmount();
        proposal.approvalLogId = creationData.approvalLogId();
        proposal.changeLogId = creationData.changeLogId();

        proposal.validate(sourceData, creationData.traceId());

        proposal.addEvent(LoanProposalEventMapper.INSTANCE.toCreatedEvent(proposal));
        return proposal;
    }

    public void update(LoanProposalUpdateData updateData) {
        if (this.loanProposalStatus != LoanProposalStatus.PENDING) {
            throw new LoanProposalValidationException(UPDATE_FAILED, LOAN_PROPOSAL_UPDATE_FAILED);
        }

        this.loanProductId = coalesce(updateData.loanProductId(), this.loanProductId);
        this.loanProductDetailsId = coalesce(updateData.loanProductDetailsId(), this.loanProductDetailsId);
        this.loanProductPolicyId = coalesce(updateData.loanProductPolicyId(), this.loanProductPolicyId);
        this.schemeId = coalesce(updateData.schemeId(), this.schemeId);
        this.sectorId = coalesce(updateData.sectorId(), this.sectorId);
        this.subSectorId = coalesce(updateData.subSectorId(), this.subSectorId);
        this.frequencyId = coalesce(updateData.frequencyId(), this.frequencyId);
        this.proposedLoanAmount = coalesce(updateData.proposedLoanAmount(), this.proposedLoanAmount);
        this.approvedLoanAmount = updateData.proposedLoanAmount() != null ? updateData.proposedLoanAmount() : this.approvedLoanAmount;
        this.proposedGrantAmount = coalesce(updateData.proposedGrantAmount(), this.proposedGrantAmount);
        this.approvedGrantAmount = coalesce(updateData.approvedGrantAmount(), this.approvedGrantAmount);
        this.preProposedLoanAmount = coalesce(updateData.preProposedLoanAmount(), this.preProposedLoanAmount);
        this.interestRate = coalesce(updateData.interestRate(), this.interestRate);
        this.numberOfInstallments = coalesce(updateData.numberOfInstallments(), this.numberOfInstallments);
        this.approvedNumberOfInstallments = updateData.numberOfInstallments() != null ? updateData.numberOfInstallments() : this.approvedNumberOfInstallments;
        this.installmentAmount = coalesce(updateData.installmentAmount(), this.installmentAmount);
        this.approvedInstallmentAmount = updateData.recalculatedInstallmentAmount() != null ? updateData.recalculatedInstallmentAmount() : this.approvedInstallmentAmount;
        this.proposalDurationInMonths = coalesce(updateData.proposalDurationInMonths(), this.proposalDurationInMonths);
        this.approvedDurationInMonths = updateData.proposalDurationInMonths() != null ? updateData.proposalDurationInMonths() : this.approvedDurationInMonths;
        this.loanProposalType = coalesce(updateData.loanProposalType(), this.loanProposalType);
        this.microInsurance = coalesce(updateData.microInsurance(), this.microInsurance);
        this.policyTypeId = coalesce(updateData.policyTypeId(), this.policyTypeId);
        this.insuranceProductId = coalesce(updateData.insuranceProductId(), this.insuranceProductId);
        this.premiumAmount = coalesce(updateData.premiumAmount(), this.premiumAmount);
        this.wantsFireInsurance = coalesce(updateData.wantsFireInsurance(), this.wantsFireInsurance);
        this.fireInsuranceProductId = coalesce(updateData.fireInsuranceProductId(), this.fireInsuranceProductId);
        this.fireInsuranceDetails = coalesce(updateData.fireInsuranceDetails(), this.fireInsuranceDetails);
        this.modeOfPayment = coalesce(updateData.modeOfPayment(), this.modeOfPayment);
        this.autoDebitCollection = coalesce(updateData.autoDebitCollection(), this.autoDebitCollection);
        this.nominees = coalesce(updateData.nominees(), this.nominees);
        this.guardian = coalesce(updateData.guardian(), this.guardian);
        this.coBorrower = coalesce(updateData.coBorrower(), this.coBorrower);
        this.secondInsurer = coalesce(updateData.secondInsurer(), this.secondInsurer);
        this.specialSavingsAccountIds = coalesce(updateData.specialSavingsAccountIds(), this.specialSavingsAccountIds);
        this.specialSavingsAccountNumbers = coalesce(updateData.specialSavingsAccountNumbers(), this.specialSavingsAccountNumbers);
        this.countryId = coalesce(updateData.countryId(), this.countryId);
        this.loanApproverId = coalesce(updateData.loanApproverId(), this.loanApproverId);
        this.totalPovertyScore = coalesce(updateData.totalPovertyScore(), this.totalPovertyScore);
        this.fieldOfficerId = coalesce(updateData.fieldOfficerId(), this.fieldOfficerId);
        this.loanSecurityAmount = coalesce(updateData.loanSecurityAmount(), this.loanSecurityAmount);
        this.loanSecurityBalance = coalesce(updateData.loanSecurityBalance(), this.loanSecurityBalance);
        this.spousePrimaryIncomeSource = coalesce(updateData.spousePrimaryIncomeSource(), this.spousePrimaryIncomeSource);
        this.spouseSecondaryIncomeSource = coalesce(updateData.spouseSecondaryIncomeSource(), this.spouseSecondaryIncomeSource);
        this.firstChildName = coalesce(updateData.firstChildName(), this.firstChildName);
        this.secondChildName = coalesce(updateData.secondChildName(), this.secondChildName);
        this.largeGroupLeaderName = coalesce(updateData.largeGroupLeaderName(), this.largeGroupLeaderName);
        this.largeGroupLeaderImage = coalesce(updateData.largeGroupLeaderImage(), this.largeGroupLeaderImage);
        this.proposalReferenceNumber = coalesce(updateData.proposalReferenceNumber(), this.proposalReferenceNumber);
        this.isDigitalDisbursement = deriveDigitalDisbursementFlag(this.modeOfPayment);
        this.transactionDescription = deriveTransactionDescription(this.modeOfPayment);
        this.loanProposalStatus = LoanProposalStatus.PENDING;
        this.status = DomainStatus.UPDATED;

        validate(updateData.sourceData(), updateData.traceId());

        addEvent(LoanProposalEventMapper.INSTANCE.toUpdatedEvent(this));
    }

    public void delete(LoanProposalDeletionData deletionData) {
        if (this.loanProposalStatus != LoanProposalStatus.PENDING) {
            Map<String, LocalizedMessage> errors = Map.of("loanProposal",
                    LocalizedMessage.builder()
                            .key(DELETE_FAILED)
                            .args(new Object[]{this.loanProposalStatus})
                            .build());
            throw new LoanProposalValidationException(errors);
        }

        this.tracerId = deletionData.traceId();
        this.deleted = true;
        this.deletedBy = deletionData.deletedBy();
        this.deletedAt = deletionData.deletedAt();
        this.status = DomainStatus.INACTIVE;

        addEvent(LoanProposalEventMapper.INSTANCE.toDeletedEvent(this));
    }

    public void approve(String traceId, String approvedBy) {
        if (!isEligibleForApproval()) {
            Map<String, LocalizedMessage> errors = Map.of("loanProposal",
                    LocalizedMessage.builder()
                            .key(APPROVE_FAILED)
                            .args(new Object[]{this.approvalIneligibilityReason()})
                            .build());
            throw new LoanProposalValidationException(errors);
        }

        this.tracerId = traceId;
        this.loanProposalStatus = LoanProposalStatus.APPROVED;
        this.approvalStatus = LoanProposalStatus.APPROVED.name();
        this.approvalFlowStatus = LoanProposalStatus.APPROVED.name();
        this.status = DomainStatus.UPDATED;

        addEvent(LoanProposalEventMapper.INSTANCE.toApprovedEvent(this, approvedBy));
    }

    public boolean isEligibleForApproval() {
        return this.loanProposalStatus == LoanProposalStatus.PENDING
                && !Boolean.TRUE.equals(this.deleted)
                && this.status != DomainStatus.INACTIVE;
    }

    public String approvalIneligibilityReason() {
        if (Boolean.TRUE.equals(this.deleted) || this.status == DomainStatus.INACTIVE) {
            return "Loan proposal is deleted or inactive";
        }
        if (this.loanProposalStatus != LoanProposalStatus.PENDING) {
            return "Only pending loan proposal can be approved. Loan proposal status: " + this.loanProposalStatus;
        }
        return null;
    }

    private static <T> T coalesce(T incoming, T current) {
        return incoming != null ? incoming : current;
    }

    private Boolean deriveDigitalDisbursementFlag(OtcModeOfPayment modeOfPayment) {
        if (modeOfPayment == null) {
            return this.isDigitalDisbursement;
        }
        return modeOfPayment.digitalDisbursementModeId() != null;
    }

    private String deriveTransactionDescription(OtcModeOfPayment modeOfPayment) {
        if (modeOfPayment == null) {
            return this.transactionDescription;
        }
        if (!Boolean.TRUE.equals(deriveDigitalDisbursementFlag(modeOfPayment))) {
            return null;
        }
        return String.format("OTC-%s-%s-%s", this.branchCode, this.villageOrganisationCode, this.memberId);
    }

    private void validate(LoanProposalSourceData sourceData, String traceId) {
        LoanProposalValidationContext context = new LoanProposalValidationContext(
                sourceData.getMember(),
                sourceData.getLoanProduct(),
                sourceData.getLoanProductDetails(),
                sourceData.getLoanProductPolicy(),
                sourceData.getScheme(),
                sourceData.getProject(),
                sourceData.getProjectPolicy(),
                sourceData.getBranch(),
                sourceData.getVillageOrganisation(),
                sourceData.getInsuranceProduct(),
                sourceData.getCountry(),
                sourceData.getBank(),
                this);

        Map<String, LocalizedMessage> errors = new MemberEligibilitySpecification()
                .and(new BranchProjectVoConsistencySpecification())
                .and(new LoanProductPolicySpecification())
                .and(new RepaymentFrequencyModeOfPaymentSpecification())
                .and(new LoanAmountSpecification())
                .and(new LoanExposureLimitSpecification())
                .and(new CoBorrowerSpecification())
                .and(new InsurancePolicyTypeSecondInsurerSpecification())
                .and(new NomineeSpecification())
                .and(new SpecialSavingsLienSpecification())
                .and(new ProjectSpecificRulesSpecification())
                .and(new ParallelCoExistingLoanSpecification())
                .and(new InstallmentConfigurationSpecification())
                .and(new ModeOfPaymentRocketWalletSpecification())
                .and(new DigitalDisbursementSpecification())
                .and(new MigrationCountrySpecification())
                .and(new FireInsuranceSpecification())
                .and(new AgeLimitSpecification())
                .and(new MoneyPlantSpecification())
                .and(new SchemeSectorMappingSpecification())
                .and(new BankModeOfPaymentSpecification())
                .validate(context);

        if (!errors.isEmpty()) {
            throw new LoanProposalValidationException(errors);
        }
    }

    static String generateProposalNumber(LocalDate applicationDate, Long sequence) {
        LocalDate date = applicationDate != null ? applicationDate : LocalDate.now();
        long seq = sequence != null ? sequence : ThreadLocalRandom.current().nextInt(100_000);
        return String.format("%d%02d-%05d", date.getYear(), date.getMonthValue(), seq);
    }

    static FireInsuranceDetails defaultFireInsuranceDetails(FireInsuranceDetails fireInsuranceDetails,
                                                            BigDecimal proposedLoanAmount,
                                                            Integer proposalDurationInMonths) {
        if (fireInsuranceDetails == null) {
            return null;
        }
        BigDecimal insuredAmount = fireInsuranceDetails.fireInsuranceInsuredAmount() != null
                ? fireInsuranceDetails.fireInsuranceInsuredAmount() : proposedLoanAmount;
        Integer duration = fireInsuranceDetails.durationOfFireInsurance();
        if (duration == null) {
            duration = proposalDurationInMonths != null ? Math.max(proposalDurationInMonths, 12) : 12;
        }
        return new FireInsuranceDetails(fireInsuranceDetails.businessName(), fireInsuranceDetails.businessAddress(),
                fireInsuranceDetails.businessPhone(), fireInsuranceDetails.businessEmail(), fireInsuranceDetails.divisionId(),
                fireInsuranceDetails.districtId(), fireInsuranceDetails.thanaId(), fireInsuranceDetails.businessTypeId(),
                fireInsuranceDetails.constructionOfPremisesId(), fireInsuranceDetails.fireInsurancePremiumAmount(),
                insuredAmount, duration, fireInsuranceDetails.fireInsuranceProductName(),
                fireInsuranceDetails.bracCommissionAmount(), fireInsuranceDetails.memberCommissionAmount());
    }

    static List<Nominee> assignNomineeIds(List<Nominee> nominees) {
        if (nominees == null || nominees.isEmpty()) {
            return nominees;
        }
        nominees.stream().filter(n -> n.getId() == null)
                .forEach(n -> n.setId(UUID.randomUUID().toString()));
        if (nominees.stream().anyMatch(n -> n.getSharePercentage() == null)) {
            double equalShare = 100.0 / nominees.size();
            nominees.forEach(n -> n.setSharePercentage(equalShare));
        }
        return nominees;
    }

    //need to verify against legacy code
    static Guardian linkGuardianToFirstNominee(Guardian guardian, List<Nominee> nominees) {
        if (guardian != null && guardian.getId() == null && nominees != null && !nominees.isEmpty()) {
            guardian.setId(nominees.get(0).getId());
        }
        return guardian;
    }

    static CoBorrower assignCoBorrowerId(CoBorrower coBorrower) {
        if (coBorrower != null && coBorrower.getId() == null) {
            coBorrower.setId(UUID.randomUUID().toString());
        }
        return coBorrower;
    }

    @Override
    public String id() {
        return id;
    }
}
