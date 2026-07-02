package com.bits.loanproposal.infrastructure.persistence.document.sourcedata;

import com.bits.ddd.annotation.MongoSourceData;
import com.bits.ddd.domain.sourcedata.SourceData;
import com.bits.loanproposal.application.dto.sourcedata.LoanProductPolicy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;

@Document(collection = "loan_product_policy_snapshot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MongoSourceData
public class LoanProductPolicyDocument extends SourceData<Long> {

    @Id
    private Long loanProductPolicyId;
    private Long loanProductId;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private BigDecimal grantPercentage;
    private boolean enforcesLoanExposureLimit;
    private BigDecimal officeAndProjectExposureLimit;
    private LocalDate activePeriodStart;
    private LocalDate activePeriodEnd;

    @Override
    public Long id() {
        return loanProductPolicyId;
    }

    public LoanProductPolicy toModel() {
        return LoanProductPolicy.builder()
                .id(loanProductPolicyId != null ? String.valueOf(loanProductPolicyId) : null)
                .loanProductPolicyId(loanProductPolicyId)
                .loanProductId(loanProductId)
                .minAmount(minAmount)
                .maxAmount(maxAmount)
                .grantPercentage(grantPercentage)
                .enforcesLoanExposureLimit(enforcesLoanExposureLimit)
                .officeAndProjectExposureLimit(officeAndProjectExposureLimit)
                .activePeriodStart(activePeriodStart)
                .activePeriodEnd(activePeriodEnd)
                .build();
    }
}
