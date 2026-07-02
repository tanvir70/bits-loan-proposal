package com.bits.loanproposal.domain.event;

import com.bits.ddd.shared.messaging.DomainEventMessage;
import com.bits.loanproposal.domain.enums.*;
import com.bits.loanproposal.domain.entity.*;
import com.bits.loanproposal.domain.valueobject.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class LoanProposalCreatedEvent extends DomainEventMessage {
    public static final String TOPIC_EXCHANGE = "loan-proposal.exchange";
    public static final String ROUTING_KEY = "loan-proposal.created";

    private String id;
    private Long loanProposalId;
    private String proposalNumber;
    private Long branchId;
    private String branchCode;
    private Long projectId;
    private Long memberId;
    private Long loanProductId;
    private BigDecimal proposedLoanAmount;
    private BigDecimal approvedLoanAmount;
    private LoanProposalStatus loanProposalStatus;
    private ApiDataSource dataSource;
    private DomainStatus domainStatus;
    private Boolean isDigitalDisbursement;
    private List<Nominee> nominees;
    private FireInsuranceDetails fireInsuranceDetails;
    private OtcModeOfPayment modeOfPayment;
    private LocalDate applicationDate;
    private String traceId;

    public LoanProposalCreatedEvent(
            String id,
            Long loanProposalId,
            String proposalNumber,
            Long branchId,
            String branchCode,
            Long projectId,
            Long memberId,
            Long loanProductId,
            BigDecimal proposedLoanAmount,
            BigDecimal approvedLoanAmount,
            LoanProposalStatus loanProposalStatus,
            ApiDataSource dataSource,
            DomainStatus domainStatus,
            Boolean isDigitalDisbursement,
            List<Nominee> nominees,
            FireInsuranceDetails fireInsuranceDetails,
            OtcModeOfPayment modeOfPayment,
            LocalDate applicationDate,
            long version,
            String tracerId) {
        super(id, "LoanProposal", version, tracerId, TOPIC_EXCHANGE, ROUTING_KEY);
        this.id = id;
        this.loanProposalId = loanProposalId;
        this.proposalNumber = proposalNumber;
        this.branchId = branchId;
        this.branchCode = branchCode;
        this.projectId = projectId;
        this.memberId = memberId;
        this.loanProductId = loanProductId;
        this.proposedLoanAmount = proposedLoanAmount;
        this.approvedLoanAmount = approvedLoanAmount;
        this.loanProposalStatus = loanProposalStatus;
        this.dataSource = dataSource;
        this.domainStatus = domainStatus;
        this.isDigitalDisbursement = isDigitalDisbursement;
        this.nominees = nominees;
        this.fireInsuranceDetails = fireInsuranceDetails;
        this.modeOfPayment = modeOfPayment;
        this.applicationDate = applicationDate;
        this.traceId = tracerId;
    }
}
