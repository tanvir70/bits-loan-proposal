package com.bits.loanproposal.domain.event;

import com.bits.ddd.shared.domain.value.DomainStatus;
import com.bits.ddd.shared.messaging.DomainEventMessage;
import com.bits.loanproposal.domain.entity.Nominee;
import com.bits.loanproposal.domain.enums.ApiDataSource;
import com.bits.loanproposal.domain.enums.LoanProposalStatus;
import com.bits.loanproposal.domain.enums.LoanProposalType;
import com.bits.loanproposal.domain.value.FireInsuranceDetails;
import com.bits.loanproposal.domain.value.OtcModeOfPayment;
import com.bits.loanproposal.infrastructure.messaging.RabbitMQConstants;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class LoanProposalUpdatedEvent extends DomainEventMessage {
    public static final String TOPIC_EXCHANGE = RabbitMQConstants.LOAN_PROPOSAL_EXCHANGE;
    public static final String ROUTING_KEY = RabbitMQConstants.LOAN_PROPOSAL_UPDATED_EVENT_ROUTING_KEY;

    private Long loanProposalId;
    private String proposalNumber;
    private Long branchId;
    private String branchCode;
    private Long projectId;
    private Long memberId;
    private Long loanProductId;
    private Long loanProductDetailsId;
    private Long loanProductPolicyId;
    private Long schemeId;
    private Long frequencyId;
    private BigDecimal proposedLoanAmount;
    private BigDecimal approvedLoanAmount;
    private BigDecimal proposedGrantAmount;
    private BigDecimal approvedGrantAmount;
    private BigDecimal installmentAmount;
    private Integer proposalDurationInMonths;
    private LoanProposalType loanProposalType;
    private LoanProposalStatus loanProposalStatus;
    private ApiDataSource dataSource;
    private DomainStatus domainStatus;
    private Boolean isDigitalDisbursement;
    private List<Nominee> nominees;
    private FireInsuranceDetails fireInsuranceDetails;
    private OtcModeOfPayment modeOfPayment;
    private LocalDate applicationDate;
    private String proposalReferenceNumber;
    private String traceId;

    public LoanProposalUpdatedEvent(
            String id,
            Long loanProposalId,
            String proposalNumber,
            Long branchId,
            String branchCode,
            Long projectId,
            Long memberId,
            Long loanProductId,
            Long loanProductDetailsId,
            Long loanProductPolicyId,
            Long schemeId,
            Long frequencyId,
            BigDecimal proposedLoanAmount,
            BigDecimal approvedLoanAmount,
            BigDecimal proposedGrantAmount,
            BigDecimal approvedGrantAmount,
            BigDecimal installmentAmount,
            Integer proposalDurationInMonths,
            LoanProposalType loanProposalType,
            LoanProposalStatus loanProposalStatus,
            ApiDataSource dataSource,
            DomainStatus domainStatus,
            Boolean isDigitalDisbursement,
            List<Nominee> nominees,
            FireInsuranceDetails fireInsuranceDetails,
            OtcModeOfPayment modeOfPayment,
            LocalDate applicationDate,
            String proposalReferenceNumber,
            long version,
            String traceId) {
        super(id, "LoanProposal", version, traceId, TOPIC_EXCHANGE, ROUTING_KEY);
        this.loanProposalId = loanProposalId;
        this.proposalNumber = proposalNumber;
        this.branchId = branchId;
        this.branchCode = branchCode;
        this.projectId = projectId;
        this.memberId = memberId;
        this.loanProductId = loanProductId;
        this.loanProductDetailsId = loanProductDetailsId;
        this.loanProductPolicyId = loanProductPolicyId;
        this.schemeId = schemeId;
        this.frequencyId = frequencyId;
        this.proposedLoanAmount = proposedLoanAmount;
        this.approvedLoanAmount = approvedLoanAmount;
        this.proposedGrantAmount = proposedGrantAmount;
        this.approvedGrantAmount = approvedGrantAmount;
        this.installmentAmount = installmentAmount;
        this.proposalDurationInMonths = proposalDurationInMonths;
        this.loanProposalType = loanProposalType;
        this.loanProposalStatus = loanProposalStatus;
        this.dataSource = dataSource;
        this.domainStatus = domainStatus;
        this.isDigitalDisbursement = isDigitalDisbursement;
        this.nominees = nominees;
        this.fireInsuranceDetails = fireInsuranceDetails;
        this.modeOfPayment = modeOfPayment;
        this.applicationDate = applicationDate;
        this.proposalReferenceNumber = proposalReferenceNumber;
        this.traceId = traceId;
    }
}
