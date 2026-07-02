package com.bits.loanproposal.infrastructure.persistence.document.sourcedata;

import com.bits.ddd.annotation.MongoSourceData;
import com.bits.ddd.domain.sourcedata.SourceData;
import com.bits.loanproposal.application.dto.sourcedata.LoanProduct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "loan_product_snapshot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MongoSourceData
public class LoanProductDocument extends SourceData<Long> {

    @Id
    private Long loanProductId;
    private String productCode;
    private String name;
    private LocalDate activePeriodStart;
    private LocalDate activePeriodEnd;
    private boolean allowsParallelLoans;
    private boolean requiresCoBorrower;
    private boolean isMicroInsuranceMandatory;
    private boolean usesVariableInstallments;
    private String loanProductType;

    private List<Long> projectMappings;
    private List<Long> officeMappings;
    private List<Long> categoryMappings;
    private List<Long> frequencyMappings;
    private List<Long> memberCategoryMappings;

    @Override
    public Long id() {
        return loanProductId;
    }

    public LoanProduct toModel() {
        return LoanProduct.builder()
                .id(loanProductId != null ? String.valueOf(loanProductId) : null)
                .loanProductId(loanProductId)
                .productCode(productCode)
                .name(name)
                .activePeriodStart(activePeriodStart)
                .activePeriodEnd(activePeriodEnd)
                .allowsParallelLoans(allowsParallelLoans)
                .requiresCoBorrower(requiresCoBorrower)
                .isMicroInsuranceMandatory(isMicroInsuranceMandatory)
                .usesVariableInstallments(usesVariableInstallments)
                .loanProductType(loanProductType)
                .projectMappings(projectMappings)
                .officeMappings(officeMappings)
                .categoryMappings(categoryMappings)
                .frequencyMappings(frequencyMappings)
                .memberCategoryMappings(memberCategoryMappings)
                .build();
    }
}
