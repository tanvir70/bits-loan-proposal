package com.bits.loanproposal.infrastructure.persistence.document.sourcedata;

import com.bits.ddd.annotation.MongoSourceData;
import com.bits.ddd.domain.sourcedata.SourceData;
import com.bits.loanproposal.application.dto.sourcedata.InsuranceProduct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Document(collection = "insurance_product_snapshot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MongoSourceData
public class InsuranceProductDocument extends SourceData<Long> {

    @Id
    private Long insuranceProductId;
    private String name;
    private BigDecimal coverageMinAmount;
    private BigDecimal coverageMaxAmount;
    private BigDecimal premiumAmount;
    private List<Long> branchMappings;
    private List<Long> projectMappings;
    private String type;

    @Override
    public Long id() {
        return insuranceProductId;
    }

    public InsuranceProduct toModel() {
        return InsuranceProduct.builder()
                .id(insuranceProductId != null ? String.valueOf(insuranceProductId) : null)
                .insuranceProductId(insuranceProductId)
                .name(name)
                .coverageMinAmount(coverageMinAmount)
                .coverageMaxAmount(coverageMaxAmount)
                .premiumAmount(premiumAmount)
                .branchMappings(branchMappings)
                .projectMappings(projectMappings)
                .type(type)
                .build();
    }
}
