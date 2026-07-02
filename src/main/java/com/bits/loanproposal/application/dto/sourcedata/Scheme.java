package com.bits.loanproposal.application.dto.sourcedata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Scheme {
    private String id;
    private Long schemeId;
    private String name;
    private List<Long> loanProductMappings;
    private List<Long> sectorMappings;
    private Map<String, BigDecimal> assetGrantPercentageByVoCategory;

    public boolean isMappedToLoanProduct(LoanProduct product) {
        return product == null || loanProductMappings == null || loanProductMappings.contains(product.getLoanProductId());
    }

    public boolean isMappedToSector(Long sectorId) {
        return sectorId == null || sectorMappings == null || sectorMappings.contains(sectorId);
    }
}
