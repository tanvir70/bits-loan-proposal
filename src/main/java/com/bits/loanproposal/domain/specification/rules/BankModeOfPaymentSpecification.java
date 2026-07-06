package com.bits.loanproposal.domain.specification.rules;

import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.ddd.specification.rules.Specification;
import com.bits.loanproposal.domain.aggregate.LoanProposal;
import com.bits.loanproposal.domain.enums.ModeOfPaymentSubType;
import com.bits.loanproposal.domain.specification.context.LoanProposalValidationContext;
import com.bits.loanproposal.domain.value.OtcModeOfPayment;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

// DDD-REQ-029
public class BankModeOfPaymentSpecification implements Specification<LoanProposalValidationContext> {

    private static final Set<ModeOfPaymentSubType> WALLET_SUBTYPES =
            EnumSet.of(ModeOfPaymentSubType.ROCKET, ModeOfPaymentSubType.BKASH);
    private static final Set<ModeOfPaymentSubType> DOCUMENT_SUBTYPES =
            EnumSet.of(ModeOfPaymentSubType.CHEQUE, ModeOfPaymentSubType.ONLINE,
                    ModeOfPaymentSubType.CASH_DEPOSIT);

    @Override
    public Map<String, LocalizedMessage> validate(LoanProposalValidationContext context) {
        Map<String, LocalizedMessage> errors = new HashMap<>();
        LoanProposal aggregate = context.aggregate();
        OtcModeOfPayment modeOfPayment = aggregate.getModeOfPayment();
        // ponytail: doc's isBankMode() is undefined; treated as "carries bank coordinates"
        if (modeOfPayment == null
                || (modeOfPayment.bankId() == null && modeOfPayment.bankAccountNumber() == null)) {
            return errors;
        }
        if (modeOfPayment.subType() == null) {
            errors.put("modeOfPaymentSubType",
                    LocalizedMessage.builder().key("NO_BANK_MOP_SUBTYPE").build());
            return errors;
        }
        if (WALLET_SUBTYPES.contains(modeOfPayment.subType())) {
            errors.put("modeOfPaymentSubType",
                    LocalizedMessage.builder().key("UNSUPPORTED_BANK_PAYMENT_SUBTYPE").build());
        }
        if (context.bank() == null) {
            errors.put("memberBank",
                    LocalizedMessage.builder().key("MEMBER_BANK_INFO_NOT_FOUND").build());
        } else {
            if (!Boolean.TRUE.equals(aggregate.getIsDigitalDisbursement())
                    && modeOfPayment.bankAccountNumber() == null) {
                errors.put("bankAccountNumber",
                        LocalizedMessage.builder().key("BANK_ACCOUNT_NUMBER_NOT_FOUND").build());
            }
            if (!context.bank().isOverdraftAccount()
                    && context.bank().getBalance() != null
                    && aggregate.getProposedLoanAmount() != null
                    && context.bank().getBalance().compareTo(aggregate.getProposedLoanAmount()) < 0) {
                errors.put("bankBalance",
                        LocalizedMessage.builder().key("BANK_INSUFFICIENT_BALANCE").build());
            }
        }
        Long bankOfAccount = context.bank() != null ? context.bank().getBankId() : null;
        Long bankOfBranch = context.branch() != null ? context.branch().getBankId() : null;
        if (bankOfAccount != null && bankOfBranch != null) {
            boolean sameBank = Objects.equals(bankOfAccount, bankOfBranch);
            if (modeOfPayment.subType() == ModeOfPaymentSubType.BEFTN && sameBank) {
                errors.put("modeOfPayment",
                        LocalizedMessage.builder().key("BEFTN_NOT_ALLOWED_SAME_BANK").build());
            }
            if (modeOfPayment.subType() == ModeOfPaymentSubType.FUND_TRANSFER && !sameBank) {
                errors.put("modeOfPayment",
                        LocalizedMessage.builder().key("FUND_TRANSFER_NOT_ALLOWED_DIFFERENT_BANK").build());
            }
        }
        if (DOCUMENT_SUBTYPES.contains(modeOfPayment.subType())) {
            // ponytail: doc's isValidDocumentNumber() is undefined; non-blank is the check
            if (modeOfPayment.paymentSubTypeNumber() == null
                    || modeOfPayment.paymentSubTypeNumber().isBlank()) {
                errors.put("paymentSubTypeNumber", LocalizedMessage.builder()
                        .key("BANK_PAYMENT_DOCUMENT_NUMBER_INVALID")
                        .args(new Object[]{modeOfPayment.subType().name()})
                        .build());
            }
            if (modeOfPayment.paymentSubTypeDate() == null) {
                errors.put("paymentSubTypeDate", LocalizedMessage.builder()
                        .key("BANK_PAYMENT_DOCUMENT_DATE_INVALID")
                        .args(new Object[]{modeOfPayment.subType().name()})
                        .build());
            }
        }
        return errors;
    }
}
