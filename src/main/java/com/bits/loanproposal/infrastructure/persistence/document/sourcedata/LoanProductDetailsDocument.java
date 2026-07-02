package com.bits.loanproposal.infrastructure.persistence.document.sourcedata;

import com.bits.ddd.annotation.MongoSourceData;
import com.bits.ddd.domain.sourcedata.SourceData;
import com.bits.loanproposal.application.dto.sourcedata.LoanProductDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;

@Document(collection = "loan_product_details_snapshot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MongoSourceData
public class LoanProductDetailsDocument extends SourceData<Long> {

    @Id
    private Long loanProductDetailsId;
    private Long loanProductId;
    private Long frequencyId;
    private Integer durationMonths;
    private Integer installmentCount;
    private BigDecimal interestRate;
    private LocalDate activePeriodStart;
    private LocalDate activePeriodEnd;

    @Override
    public Long id() {
        return loanProductDetailsId;
    }

    public LoanProductDetails toModel() {
        return LoanProductDetails.builder()
                .id(loanProductDetailsId != null ? String.valueOf(loanProductDetailsId) : null)
                .loanProductDetailsId(loanProductDetailsId)
                .loanProductId(loanProductId)
                .frequencyId(frequencyId)
                .durationMonths(durationMonths)
                .installmentCount(installmentCount)
                .interestRate(interestRate)
                .activePeriodStart(activePeriodStart)
                .activePeriodEnd(activePeriodEnd)
                .build();
    }
}
