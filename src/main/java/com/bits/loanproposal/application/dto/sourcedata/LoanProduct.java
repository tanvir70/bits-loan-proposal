package com.bits.loanproposal.application.dto.sourcedata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanProduct {
    private String id;
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

    public boolean isActiveOn(LocalDate date) {
        if (date == null) return false;
        boolean afterStart = (activePeriodStart == null || !date.isBefore(activePeriodStart));
        boolean beforeEnd = (activePeriodEnd == null || !date.isAfter(activePeriodEnd));
        return afterStart && beforeEnd;
    }

    public boolean isMappedWith(Project project, Long officeId, Long categoryId, Long frequencyId) {
        // Return true if maps match (if mappings are empty/null, default to true or false depending on logic)
        boolean projectMatch = project == null || projectMappings == null || projectMappings.contains(project.getProjectId());
        boolean officeMatch = officeId == null || officeMappings == null || officeMappings.contains(officeId);
        boolean categoryMatch = categoryId == null || categoryMappings == null || categoryMappings.contains(categoryId);
        boolean frequencyMatch = frequencyId == null || frequencyMappings == null || frequencyMappings.contains(frequencyId);
        return projectMatch && officeMatch && categoryMatch && frequencyMatch;
    }

    public boolean isMappedWithMemberCategory(Long categoryId) {
        return categoryId == null || memberCategoryMappings == null || memberCategoryMappings.contains(categoryId);
    }
}
