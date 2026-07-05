package com.bits.loanproposal.domain.aggregate;

import com.bits.ddd.aggregate.AggregateRoot;
import com.bits.ddd.shared.domain.value.DomainStatus;
import com.bits.ddd.shared.exception.domain.DomainValidationException;
import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.loanproposal.application.dto.LoanProposalSourceData;
import com.bits.loanproposal.domain.enums.*;
import com.bits.loanproposal.domain.entity.*;
import com.bits.loanproposal.domain.value.*;
import com.bits.loanproposal.domain.param.LoanProposalCreationData;
import com.bits.loanproposal.domain.mapper.LoanProposalEventMapper;
import com.bits.loanproposal.domain.specification.context.LoanProposalValidationContext;
import com.bits.loanproposal.domain.specification.rules.LoanAmountSpecification;
import com.bits.loanproposal.domain.specification.rules.MemberEligibilitySpecification;
import lombok.Getter;

import static com.bits.loanproposal.domain.constant.DomainErrorConstant.ID_NULL;
import static com.bits.loanproposal.domain.constant.DomainErrorConstant.PROPOSAL_ID_MUST_NOT_BE_NULL;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
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
    private DomainStatus domainStatus;
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

    public static LoanProposal create(LoanProposalCreationData creationData, LoanProposalSourceData sourceData) {
        if (creationData.id() == null) {
            throw new DomainValidationException(ID_NULL, PROPOSAL_ID_MUST_NOT_BE_NULL);
        }
        LoanProposal proposal = new LoanProposal();
        proposal.id = creationData.id();
        proposal.loanProposalId = creationData.loanProposalId();
        proposal.proposalNumber = generateProposalNumber(creationData.applicationDate());
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
        proposal.approvedLoanAmount = creationData.proposedLoanAmount(); // Initialized to proposed
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
        proposal.domainStatus = DomainStatus.CREATED;
        proposal.microInsurance = creationData.microInsurance();
        proposal.policyTypeId = creationData.policyTypeId();
        proposal.insuranceProductId = creationData.insuranceProductId();
        proposal.premiumAmount = creationData.premiumAmount();
        proposal.secondInsurer = creationData.secondInsurer();
        proposal.wantsFireInsurance = creationData.wantsFireInsurance();
        proposal.fireInsuranceProductId = creationData.fireInsuranceProductId();
        proposal.fireInsuranceDetails = creationData.fireInsuranceDetails();
        proposal.modeOfPayment = creationData.modeOfPayment();
        proposal.autoDebitCollection = creationData.autoDebitCollection();
        proposal.isDigitalDisbursement = creationData.isDigitalDisbursement();
        proposal.transactionDescription = creationData.transactionDescription();
        proposal.nominees = creationData.nominees();
        proposal.guardian = creationData.guardian();
        proposal.coBorrower = creationData.coBorrower();
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

        proposal.validate(sourceData);

        proposal.addEvent(LoanProposalEventMapper.INSTANCE.toCreatedEvent(proposal));
        return proposal;
    }

    private void validate(LoanProposalSourceData sourceData) {
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

        // ponytail: 2 of the 21 DDD-REQ spec categories implemented; append .and(...) here as the rest land
        Map<String, LocalizedMessage> errors = new MemberEligibilitySpecification()
                .and(new LoanAmountSpecification())
                .validate(context);

        if (!errors.isEmpty()) {
            String detail = errors.entrySet().stream()
                    .map(e -> e.getKey() + ": " + e.getValue().getKey())
                    .collect(Collectors.joining("; "));
            throw new DomainValidationException("LOAN_PROPOSAL_VALIDATION_FAILED", detail);
        }
    }

    private static String generateProposalNumber(LocalDate applicationDate) {
        LocalDate date = applicationDate != null ? applicationDate : LocalDate.now();
        // ponytail: random 5-digit suffix guarded by the unique proposalNumber+branch index; switch to a Mongo counter if collisions surface
        return String.format("%d%02d-%05d", date.getYear(), date.getMonthValue(),
                ThreadLocalRandom.current().nextInt(100_000));
    }

    @Override
    public String id() {
        return id;
    }
}
