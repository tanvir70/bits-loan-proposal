package com.bits.loanproposal.application.dto.sourcedata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceProduct {
    private String id;
    private Long insuranceProductId;
    private String name;
    private BigDecimal coverageMinAmount;
    private BigDecimal coverageMaxAmount;
    private BigDecimal premiumAmount;
    private List<Long> branchMappings;
    private List<Long> projectMappings;
    private String type;

    public boolean coversAmount(BigDecimal amount) {
        if (amount == null) return false;
        boolean afterMin = (coverageMinAmount == null || amount.compareTo(coverageMinAmount) >= 0);
        boolean beforeMax = (coverageMaxAmount == null || amount.compareTo(coverageMaxAmount) <= 0);
        return afterMin && beforeMax;
    }

    public boolean isMappedWithBranch(Long branchId) {
        return branchId == null || branchMappings == null || branchMappings.contains(branchId);
    }

    public boolean isMappedWithProject(Long projectId) {
        return projectId == null || projectMappings == null || projectMappings.contains(projectId);
    }
}
