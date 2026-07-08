package com.bits.loanproposal.domain.specification.rules;

import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.ddd.specification.rules.Specification;
import com.bits.loanproposal.application.dto.sourcedata.Member;
import com.bits.loanproposal.application.dto.sourcedata.Project;
import com.bits.loanproposal.application.dto.sourcedata.ProjectPolicy;
import com.bits.loanproposal.application.dto.sourcedata.VillageOrganisation;
import com.bits.loanproposal.domain.aggregate.LoanProposal;
import com.bits.loanproposal.domain.specification.context.LoanProposalValidationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// DDD-REQ-010
public class BranchProjectVoConsistencySpecification implements Specification<LoanProposalValidationContext> {

    @Override
    public Map<String, LocalizedMessage> validate(LoanProposalValidationContext context) {
        Map<String, LocalizedMessage> errors = new HashMap<>();
        LoanProposal aggregate = context.aggregate();
        Project project = context.project();
        if (project == null) {
            errors.put("project", LocalizedMessage.builder()
                    .key("PROJECT_NOT_FOUND")
                    .args(new Object[]{aggregate.getProjectCode()})
                    .build());
            return errors;
        }
        ProjectPolicy projectPolicy = context.projectPolicy();
        if (projectPolicy == null) {
            errors.put("projectPolicy", LocalizedMessage.builder().key("PROJECT_POLICY_NOT_FOUND").build());
        }
        Member member = context.member();
        if (member != null) {
            if (!Objects.equals(member.getBranchId(), aggregate.getBranchId())) {
                errors.put("branch", LocalizedMessage.builder().key("MEMBER_BRANCH_MISMATCH").build());
            }
            if (!Objects.equals(member.getProjectId(), aggregate.getProjectId())) {
                errors.put("project", LocalizedMessage.builder().key("MEMBER_PROJECT_MISMATCH").build());
            }
        }
        validateVillageOrganisation(context, errors, aggregate, projectPolicy);
        if (context.branch() != null && project.getBranchMappings() != null
                && !project.getBranchMappings().contains(context.branch().getBranchId())) {
            errors.put("branchProject", LocalizedMessage.builder().key("NO_ACTIVE_BRANCH_PROJECT_MAPPING").build());
        }
        return errors;
    }

    // doc pseudocode flags missing VO unconditionally, which would reject every
    // non-group proposal (VO source data is only fetched when a voCode is supplied);
    // scoped the not-found check to group-based projects instead.
    private void validateVillageOrganisation(LoanProposalValidationContext context,
                                             Map<String, LocalizedMessage> errors,
                                             LoanProposal aggregate,
                                             ProjectPolicy projectPolicy) {
        boolean groupBased = projectPolicy != null
                && "GROUP".equalsIgnoreCase(projectPolicy.getAssociationType());
        VillageOrganisation vo = context.villageOrganisation();
        if (groupBased && (aggregate.getVillageOrganisationCode() == null
                || vo == null || vo.getCode() == null)) {
            errors.put("voCode", LocalizedMessage.builder().key("VO_CODE_NOT_FOUND").build());
        } else if (vo != null && vo.getCode() != null
                && aggregate.getVillageOrganisationCode() != null
                && !vo.getCode().equals(aggregate.getVillageOrganisationCode())) {
            errors.put("voCode", LocalizedMessage.builder().key("MEMBER_VO_MISMATCH").build());
        }
    }
}
