package com.bits.loanproposal.infrastructure.persistence.document.sourcedata;

import com.bits.ddd.annotation.MongoSourceData;
import com.bits.ddd.dto.SourceData;
import com.bits.loanproposal.application.dto.sourcedata.ProjectPolicy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "project_policy_snapshot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MongoSourceData
public class ProjectPolicyDocument extends SourceData<Long> {

    @Id
    private Long projectPolicyId;
    private Long projectId;
    private String associationType;
    private boolean enforcesLoanExposureLimit;
    private BigDecimal maxProttashaParallelAmount;

    @Override
    public Long id() {
        return projectPolicyId;
    }

    public ProjectPolicy toModel() {
        return ProjectPolicy.builder()
                .id(projectPolicyId != null ? String.valueOf(projectPolicyId) : null)
                .projectPolicyId(projectPolicyId)
                .projectId(projectId)
                .associationType(associationType)
                .enforcesLoanExposureLimit(enforcesLoanExposureLimit)
                .maxProttashaParallelAmount(maxProttashaParallelAmount)
                .build();
    }
}
