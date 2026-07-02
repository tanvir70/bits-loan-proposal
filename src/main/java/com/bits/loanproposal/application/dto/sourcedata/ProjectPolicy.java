package com.bits.loanproposal.application.dto.sourcedata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectPolicy {
    private String id;
    private Long projectPolicyId;
    private Long projectId;
    private String associationType;
    private boolean enforcesLoanExposureLimit;
    private BigDecimal maxProttashaParallelAmount;
}
