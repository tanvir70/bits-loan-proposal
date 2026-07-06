package com.bits.loanproposal.domain.specification.rules;

import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.ddd.specification.rules.Specification;
import com.bits.loanproposal.application.dto.sourcedata.Member;
import com.bits.loanproposal.application.dto.sourcedata.Project;
import com.bits.loanproposal.domain.specification.context.LoanProposalValidationContext;

import java.util.HashMap;
import java.util.Map;

// DDD-REQ-019
public class ProjectSpecificRulesSpecification implements Specification<LoanProposalValidationContext> {

    @Override
    public Map<String, LocalizedMessage> validate(LoanProposalValidationContext context) {
        Map<String, LocalizedMessage> errors = new HashMap<>();
        if (context.aggregate().getProgotiDocumentChecklist() != null
                && !isProgotiOrAdp(context.project())) {
            errors.put("progotiChecklist",
                    LocalizedMessage.builder().key("PROGOTI_CHECKLIST_NOT_APPLICABLE").build());
        }
        Member member = context.member();
        if (member == null || !isProgotiOrAdp(context.project())) {
            return errors;
        }
        String productType = context.loanProduct() != null
                ? context.loanProduct().getLoanProductType() : null;
        if ("GOTI".equalsIgnoreCase(productType)) {
            if (member.isHasActiveRemittanceOrMigrationOrGeneralLoan()) {
                errors.put("parallelLoan",
                        LocalizedMessage.builder().key("PARALLEL_LOAN_NOT_ALLOWED").build());
            }
            if (member.isHasGeneralLoanNotCurrentOrClosedOrWithOverdue()) {
                errors.put("memberEligibility",
                        LocalizedMessage.builder().key("MEMBER_CANNOT_AVAIL_LOAN").build());
            }
        }
        if ("SHONDHI".equalsIgnoreCase(productType) && member.isHasActiveGotiLoan()) {
            errors.put("parallelLoan",
                    LocalizedMessage.builder().key("CANNOT_AVAIL_PARALLEL_WITH_GOTI").build());
        }
        if ("GENERAL".equalsIgnoreCase(productType) && member.isHasActiveGotiLoan()) {
            errors.put("parallelLoan",
                    LocalizedMessage.builder().key("PARALLEL_LOAN_NOT_ALLOWED").build());
        }
        // ponytail: doc's DCS branch-recommender checks (OTC channel skips them per the doc) and
        // Goti approver-role rules (AM/RM/AAM/ARM, SM, DM) skipped — no approver-role source data
        return errors;
    }

    // ponytail: doc's isProgotiOrAdpProject is undefined; matched on project code/name
    // containing PROGOTI or ADP, verify against legacy project codes
    private boolean isProgotiOrAdp(Project project) {
        if (project == null) {
            return false;
        }
        String code = project.getCode() != null ? project.getCode().toUpperCase() : "";
        String name = project.getName() != null ? project.getName().toUpperCase() : "";
        return code.contains("PROGOTI") || code.contains("ADP")
                || name.contains("PROGOTI") || name.contains("ADP");
    }
}
