package com.bits.loanproposal.domain.specification.rules;

import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.ddd.specification.rules.Specification;
import com.bits.loanproposal.domain.aggregate.LoanProposal;
import com.bits.loanproposal.domain.entity.Nominee;
import com.bits.loanproposal.domain.enums.ApiDataSource;
import com.bits.loanproposal.domain.specification.context.LoanProposalValidationContext;
import com.bits.loanproposal.domain.value.FireInsuranceDetails;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// DDD-REQ-025
public class FireInsuranceSpecification implements Specification<LoanProposalValidationContext> {

    @Override
    public Map<String, LocalizedMessage> validate(LoanProposalValidationContext context) {
        Map<String, LocalizedMessage> errors = new HashMap<>();
        LoanProposal aggregate = context.aggregate();
        if (Boolean.TRUE.equals(aggregate.getWantsFireInsurance())) {
            validateRequested(errors, aggregate);
        } else {
            validateNotRequested(errors, aggregate);
        }
        return errors;
    }

    private void validateRequested(Map<String, LocalizedMessage> errors, LoanProposal aggregate) {
        if (aggregate.getFireInsuranceProductId() == null) {
            errors.put("fireInsuranceProductId",
                    LocalizedMessage.builder().key("FIRE_INSURANCE_ID_NOT_FOUND").build());
        }
        FireInsuranceDetails details = aggregate.getFireInsuranceDetails();
        if (details == null) {
            errors.put("fireInsuranceDetails",
                    LocalizedMessage.builder().key("FIRE_INSURANCE_REQUIRED_FIELD").build());
        } else {
            if (details.businessPhone() == null || !details.businessPhone().matches("\\d{11}")) {
                errors.put("businessPhone",
                        LocalizedMessage.builder().key("FIRE_INSURANCE_PHONE_INVALID").build());
            }
            if (details.fireInsuranceInsuredAmount() != null
                    && aggregate.getProposedLoanAmount() != null
                    && details.fireInsuranceInsuredAmount().compareTo(aggregate.getProposedLoanAmount()) != 0) {
                errors.put("fireInsuranceInsuredAmount",
                        LocalizedMessage.builder().key("FIRE_INSURANCE_INSURED_AMOUNT_MISMATCH").build());
            }
        }
        // ponytail: premium/duration recalculation, trading-sector check, and product
        // branch/project mapping skipped — no fire-insurance product source data entity, no
        // premium formula, and no trading-sector id are defined anywhere available to us;
        // DCS business-details rule skipped (this service is the OTC channel)
        if (countByInsuranceType(aggregate.getNominees(), "CSI") > 3) {
            errors.put("nominees",
                    LocalizedMessage.builder().key("CSI_NOMINEES_LIMIT_EXCEEDED").build());
        }
        if (countByInsuranceType(aggregate.getNominees(), "FIRE") > 3) {
            errors.put("fireNominees",
                    LocalizedMessage.builder().key("FIRE_INSURANCE_NOMINEES_LIMIT_EXCEEDED").build());
        }
    }

    private void validateNotRequested(Map<String, LocalizedMessage> errors, LoanProposal aggregate) {
        if (countByInsuranceType(aggregate.getNominees(), "FIRE") > 0) {
            errors.put("nominee",
                    LocalizedMessage.builder().key("FIRE_INSURANCE_NOT_APPLICABLE_FOR_NOMINEE").build());
        }
        if (aggregate.getPolicyTypeId() != null
                && !Boolean.TRUE.equals(aggregate.getMicroInsurance())
                && aggregate.getDataSource() == ApiDataSource.OTC) {
            errors.put("policyTypeId",
                    LocalizedMessage.builder().key("POLICY_TYPE_NOT_APPLICABLE").build());
        }
    }

    private long countByInsuranceType(List<Nominee> nominees, String type) {
        if (nominees == null) {
            return 0;
        }
        return nominees.stream()
                .filter(n -> n.getInsuranceTypes() != null && n.getInsuranceTypes().contains(type))
                .count();
    }
}
