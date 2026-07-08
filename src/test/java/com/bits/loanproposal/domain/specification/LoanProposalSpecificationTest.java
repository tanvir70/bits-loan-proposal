package com.bits.loanproposal.domain.specification;

import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.loanproposal.application.dto.sourcedata.Bank;
import com.bits.loanproposal.application.dto.sourcedata.Country;
import com.bits.loanproposal.application.dto.sourcedata.InsuranceProduct;
import com.bits.loanproposal.application.dto.sourcedata.LoanProduct;
import com.bits.loanproposal.application.dto.sourcedata.LoanProductPolicy;
import com.bits.loanproposal.application.dto.sourcedata.Member;
import com.bits.loanproposal.application.dto.sourcedata.Project;
import com.bits.loanproposal.application.dto.sourcedata.ProjectPolicy;
import com.bits.loanproposal.application.dto.sourcedata.Scheme;
import com.bits.loanproposal.application.dto.sourcedata.VillageOrganisation;
import com.bits.loanproposal.domain.aggregate.LoanProposal;
import com.bits.loanproposal.domain.entity.CoBorrower;
import com.bits.loanproposal.domain.entity.Nominee;
import com.bits.loanproposal.domain.entity.SecondInsurer;
import com.bits.loanproposal.domain.enums.ModeOfPaymentSubType;
import com.bits.loanproposal.domain.specification.context.LoanProposalValidationContext;
import com.bits.loanproposal.domain.specification.rules.AgeLimitSpecification;
import com.bits.loanproposal.domain.specification.rules.BankModeOfPaymentSpecification;
import com.bits.loanproposal.domain.specification.rules.BranchProjectVoConsistencySpecification;
import com.bits.loanproposal.domain.specification.rules.CoBorrowerSpecification;
import com.bits.loanproposal.domain.specification.rules.DigitalDisbursementSpecification;
import com.bits.loanproposal.domain.specification.rules.FireInsuranceSpecification;
import com.bits.loanproposal.domain.specification.rules.InsurancePolicyTypeSecondInsurerSpecification;
import com.bits.loanproposal.domain.specification.rules.LoanAmountSpecification;
import com.bits.loanproposal.domain.specification.rules.LoanExposureLimitSpecification;
import com.bits.loanproposal.domain.specification.rules.LoanProductPolicySpecification;
import com.bits.loanproposal.domain.specification.rules.MemberEligibilitySpecification;
import com.bits.loanproposal.domain.specification.rules.MigrationCountrySpecification;
import com.bits.loanproposal.domain.specification.rules.ModeOfPaymentRocketWalletSpecification;
import com.bits.loanproposal.domain.specification.rules.MoneyPlantSpecification;
import com.bits.loanproposal.domain.specification.rules.NomineeSpecification;
import com.bits.loanproposal.domain.specification.rules.ParallelCoExistingLoanSpecification;
import com.bits.loanproposal.domain.specification.rules.ProjectSpecificRulesSpecification;
import com.bits.loanproposal.domain.specification.rules.RepaymentFrequencyModeOfPaymentSpecification;
import com.bits.loanproposal.domain.specification.rules.SchemeSectorMappingSpecification;
import com.bits.loanproposal.domain.specification.rules.SpecialSavingsLienSpecification;
import com.bits.loanproposal.domain.enums.LoanProposalType;
import com.bits.loanproposal.domain.value.OtcModeOfPayment;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoanProposalSpecificationTest {

    private LoanProposalValidationContext context(Member member, LoanProductPolicy policy, BigDecimal amount) {
        LoanProposal aggregate = new LoanProposal();
        aggregate.setProposedLoanAmount(amount);
        return new LoanProposalValidationContext(member, null, null, policy,
                null, null, null, null, null, null, null, null, aggregate);
    }

    private Member activeMember() {
        return Member.builder()
                .memberId(1L)
                .memberClassificationId(2L)
                .status("ACTIVE")
                .isScreened(false)
                .nationalId("1234567890")
                .build();
    }

    private LoanProductPolicy policy(String min, String max) {
        return LoanProductPolicy.builder()
                .minAmount(new BigDecimal(min))
                .maxAmount(new BigDecimal(max))
                .build();
    }

    @Test
    void eligibleMemberAndAmountInRangePasses() {
        Map<String, LocalizedMessage> errors = new MemberEligibilitySpecification()
                .and(new LoanAmountSpecification())
                .validate(context(activeMember(), policy("1000", "500000"), new BigDecimal("5000")));
        assertTrue(errors.isEmpty(), () -> "expected no errors but got: " + errors);
    }

    @Test
    void screenedInactiveMemberWithoutIdentityFails() {
        Member member = Member.builder().memberId(1L).status("DORMANT").isScreened(true).build();
        Map<String, LocalizedMessage> errors = new MemberEligibilitySpecification()
                .validate(context(member, null, null));
        assertEquals("MEMBER_SCREENED", errors.get("member").getKey());
        assertEquals("MEMBER_STATUS_INVALID", errors.get("memberStatus").getKey());
        assertEquals("MEMBER_NO_IDENTITY", errors.get("memberIdentity").getKey());
        assertEquals("MEMBER_CLASSIFICATION_NOT_FOUND", errors.get("memberClassification").getKey());
    }

    @Test
    void missingMemberShortCircuits() {
        Map<String, LocalizedMessage> errors = new MemberEligibilitySpecification()
                .validate(context(null, null, null));
        assertEquals(1, errors.size());
        assertEquals("MEMBER_NOT_FOUND", errors.get("member").getKey());
    }

    @Test
    void amountOutsidePolicyRangeFails() {
        Map<String, LocalizedMessage> errors = new LoanAmountSpecification()
                .validate(context(activeMember(), policy("1000", "500000"), new BigDecimal("900")));
        assertEquals("LOAN_AMOUNT_OUT_OF_POLICY_RANGE", errors.get("proposedLoanAmount").getKey());

        errors = new LoanAmountSpecification()
                .validate(context(activeMember(), policy("1000", "500000"), new BigDecimal("500001")));
        assertEquals("LOAN_AMOUNT_OUT_OF_POLICY_RANGE", errors.get("proposedLoanAmount").getKey());
    }

    @Test
    void negativeOrMissingAmountFails() {
        Map<String, LocalizedMessage> errors = new LoanAmountSpecification()
                .validate(context(activeMember(), policy("1000", "500000"), new BigDecimal("-1")));
        assertEquals("LOAN_AMOUNT_INVALID", errors.get("proposedLoanAmount").getKey());
    }

    @Test
    void installmentAmountMismatchWithRecalculatedFails() {
        LoanProposal aggregate = new LoanProposal();
        aggregate.setProposedLoanAmount(new BigDecimal("5000"));
        aggregate.setInstallmentAmount(new BigDecimal("500"));
        aggregate.setApprovedInstallmentAmount(new BigDecimal("520"));
        Map<String, LocalizedMessage> errors = new LoanAmountSpecification()
                .validate(new LoanProposalValidationContext(null, null, null, policy("1000", "500000"),
                        null, null, null, null, null, null, null, null, aggregate));
        assertEquals("INSTALLMENT_AMOUNT_WRONG", errors.get("installmentAmount").getKey());
    }

    @Test
    void installmentConfigMismatchWithProductDetailsFails() {
        LoanProposal aggregate = new LoanProposal();
        aggregate.setProposedLoanAmount(new BigDecimal("5000"));
        aggregate.setNumberOfInstallments(12);
        aggregate.setProposalDurationInMonths(12);
        aggregate.setInterestRate(new BigDecimal("24"));
        com.bits.loanproposal.application.dto.sourcedata.LoanProductDetails details =
                com.bits.loanproposal.application.dto.sourcedata.LoanProductDetails.builder()
                        .installmentCount(24)
                        .durationMonths(12)
                        .interestRate(new BigDecimal("24"))
                        .build();
        Map<String, LocalizedMessage> errors = new LoanAmountSpecification()
                .validate(new LoanProposalValidationContext(null, null, details, policy("1000", "500000"),
                        null, null, null, null, null, null, null, null, aggregate));
        assertEquals("INSTALLMENT_CONFIG_MISMATCH", errors.get("installment").getKey());

        // matching config passes
        aggregate.setNumberOfInstallments(24);
        Map<String, LocalizedMessage> matching = new LoanAmountSpecification()
                .validate(new LoanProposalValidationContext(null, null, details, policy("1000", "500000"),
                        null, null, null, null, null, null, null, null, aggregate));
        assertTrue(matching.isEmpty(), () -> "expected no errors but got: " + matching);
    }

    private LoanProposalValidationContext fullContext(LoanProposal aggregate, Member member,
                                                      LoanProduct product, Project project,
                                                      ProjectPolicy projectPolicy, VillageOrganisation vo,
                                                      LoanProductPolicy productPolicy) {
        return new LoanProposalValidationContext(member, product, null, productPolicy,
                null, project, projectPolicy, null, vo, null, null, null, aggregate);
    }

    @Test
    void branchProjectVoConsistencyDetectsMismatches() {
        LoanProposal aggregate = new LoanProposal();
        aggregate.setBranchId(10L);
        aggregate.setProjectId(20L);
        aggregate.setVillageOrganisationCode("VO-2");
        Member member = Member.builder().branchId(11L).projectId(21L).build();
        Project project = Project.builder().projectId(20L).build();
        ProjectPolicy projectPolicy = ProjectPolicy.builder().associationType("GROUP").build();
        VillageOrganisation vo = VillageOrganisation.builder().code("VO-1").build();

        Map<String, LocalizedMessage> errors = new BranchProjectVoConsistencySpecification()
                .validate(fullContext(aggregate, member, null, project, projectPolicy, vo, null));
        assertEquals("MEMBER_BRANCH_MISMATCH", errors.get("branch").getKey());
        assertEquals("MEMBER_PROJECT_MISMATCH", errors.get("project").getKey());
        assertEquals("MEMBER_VO_MISMATCH", errors.get("voCode").getKey());
    }

    @Test
    void missingProjectShortCircuitsBranchProjectVo() {
        LoanProposal aggregate = new LoanProposal();
        aggregate.setProjectCode("P-1");
        Map<String, LocalizedMessage> errors = new BranchProjectVoConsistencySpecification()
                .validate(fullContext(aggregate, activeMember(), null, null, null, null, null));
        assertEquals(1, errors.size());
        assertEquals("PROJECT_NOT_FOUND", errors.get("project").getKey());
    }

    @Test
    void groupProjectWithoutVoFails() {
        LoanProposal aggregate = new LoanProposal();
        Member member = Member.builder().build();
        Project project = Project.builder().projectId(20L).build();
        ProjectPolicy projectPolicy = ProjectPolicy.builder().associationType("GROUP").build();
        Map<String, LocalizedMessage> errors = new BranchProjectVoConsistencySpecification()
                .validate(fullContext(aggregate, member, null, project, projectPolicy, null, null));
        assertEquals("VO_CODE_NOT_FOUND", errors.get("voCode").getKey());
    }

    @Test
    void expiredLoanProductFails() {
        LoanProposal aggregate = new LoanProposal();
        aggregate.setApplicationDate(LocalDate.of(2026, 7, 6));
        LoanProduct product = LoanProduct.builder()
                .activePeriodEnd(LocalDate.of(2025, 12, 31))
                .build();
        Map<String, LocalizedMessage> errors = new LoanProductPolicySpecification()
                .validate(fullContext(aggregate, activeMember(), product, null, null, null, null));
        assertEquals("LOAN_PRODUCT_EXPIRED", errors.get("loanProduct").getKey());
        assertEquals("LOAN_PRODUCT_DETAILS_NOT_FOUND", errors.get("loanProductDetails").getKey());
        assertEquals("LOAN_PRODUCT_POLICY_NOT_FOUND", errors.get("loanProductPolicy").getKey());
    }

    @Test
    void missingLoanProductShortCircuits() {
        Map<String, LocalizedMessage> errors = new LoanProductPolicySpecification()
                .validate(fullContext(new LoanProposal(), activeMember(), null, null, null, null, null));
        assertEquals(1, errors.size());
        assertEquals("LOAN_PRODUCT_NOT_FOUND", errors.get("loanProduct").getKey());
    }

    @Test
    void invalidFrequencyAndMissingRocketWalletFail() {
        LoanProposal aggregate = new LoanProposal();
        aggregate.setFrequencyId(11L);
        aggregate.setModeOfPayment(new OtcModeOfPayment(1L, ModeOfPaymentSubType.ROCKET,
                null, null, null, null, null, null, null, null, null, null));
        Member member = Member.builder().rocketWalletNumber(null).build();
        Map<String, LocalizedMessage> errors = new RepaymentFrequencyModeOfPaymentSpecification()
                .validate(fullContext(aggregate, member, null, null, null, null, null));
        assertEquals("LOAN_FREQUENCY_NOT_FOUND", errors.get("frequency").getKey());
        assertEquals("MEMBER_NO_ROCKET_WALLET", errors.get("rocketWallet").getKey());
    }

    @Test
    void validFrequencyAndRocketWalletPass() {
        LoanProposal aggregate = new LoanProposal();
        aggregate.setFrequencyId(2L);
        aggregate.setModeOfPayment(new OtcModeOfPayment(1L, ModeOfPaymentSubType.ROCKET,
                null, null, null, null, null, null, null, "01700000000", null, null));
        Member member = Member.builder().rocketWalletNumber("01700000000").build();
        Map<String, LocalizedMessage> errors = new RepaymentFrequencyModeOfPaymentSpecification()
                .validate(fullContext(aggregate, member, null, null, null, null, null));
        assertTrue(errors.isEmpty(), () -> "expected no errors but got: " + errors);
    }

    @Test
    void exposureLimitExceededFails() {
        LoanProposal aggregate = new LoanProposal();
        aggregate.setProposedLoanAmount(new BigDecimal("100001"));
        ProjectPolicy projectPolicy = ProjectPolicy.builder().enforcesLoanExposureLimit(true).build();
        LoanProductPolicy productPolicy = LoanProductPolicy.builder()
                .officeAndProjectExposureLimit(new BigDecimal("100000"))
                .build();
        Map<String, LocalizedMessage> errors = new LoanExposureLimitSpecification()
                .validate(fullContext(aggregate, null, null, null, projectPolicy, null, productPolicy));
        assertEquals("LOAN_EXPOSURE_LIMIT_EXCEEDED", errors.get("proposedLoanAmount").getKey());

        aggregate.setProposedLoanAmount(new BigDecimal("100000"));
        errors = new LoanExposureLimitSpecification()
                .validate(fullContext(aggregate, null, null, null, projectPolicy, null, productPolicy));
        assertTrue(errors.isEmpty());
    }

    @Test
    void doublePolicyMicroInsuranceRequiresCompleteSecondInsurer() {
        LoanProposal aggregate = new LoanProposal();
        aggregate.setMicroInsurance(true);
        aggregate.setPolicyTypeId(2L);
        aggregate.setInsuranceProductId(5L);
        aggregate.setSecondInsurer(SecondInsurer.builder().nationalId("1234567890").build());
        InsuranceProduct insurance = InsuranceProduct.builder().insuranceProductId(5L).build();
        LoanProposalValidationContext ctx = new LoanProposalValidationContext(
                activeMember(), null, null, null, null, null, null, null, null, insurance, null, null, aggregate);

        Map<String, LocalizedMessage> errors = new InsurancePolicyTypeSecondInsurerSpecification().validate(ctx);
        assertEquals("INSURER_GENDER_REQUIRED", errors.get("insurerGender").getKey());
        assertEquals("INSURER_RELATIONSHIP_REQUIRED", errors.get("insurerRelationship").getKey());
        assertEquals("INSURER_IDENTITY_DUPLICATE", errors.get("secondInsurer").getKey());
    }

    @Test
    void singlePolicyOrNoMicroInsuranceRejectsSecondInsurer() {
        LoanProposal aggregate = new LoanProposal();
        aggregate.setMicroInsurance(false);
        aggregate.setPolicyTypeId(1L);
        aggregate.setSecondInsurer(SecondInsurer.builder().build());
        Map<String, LocalizedMessage> errors = new InsurancePolicyTypeSecondInsurerSpecification()
                .validate(fullContext(aggregate, null, null, null, null, null, null));
        // single-policy check fires first, not-required check overwrites the same key
        assertTrue(errors.containsKey("secondInsurer"));
    }

    @Test
    void microInsuranceMissingPolicyTypeAndProductFails() {
        LoanProposal aggregate = new LoanProposal();
        aggregate.setMicroInsurance(true);
        Map<String, LocalizedMessage> errors = new InsurancePolicyTypeSecondInsurerSpecification()
                .validate(fullContext(aggregate, null, null, null, null, null, null));
        assertEquals("POLICY_TYPE_NULL_FOR_MICRO_INSURANCE", errors.get("policyTypeId").getKey());
        assertEquals("INSURANCE_PRODUCT_NULL_FOR_MICRO_INSURANCE", errors.get("insuranceProductId").getKey());
    }

    @Test
    void nomineeCountBeyondLimitFails() {
        LoanProposal aggregate = new LoanProposal();
        aggregate.setNominees(java.util.List.of(Nominee.builder().build(), Nominee.builder().build(),
                Nominee.builder().build(), Nominee.builder().build()));
        Map<String, LocalizedMessage> errors = new NomineeSpecification()
                .validate(fullContext(aggregate, null, null, null, null, null, null));
        assertEquals("NOMINEE_LIMIT_EXCEEDED", errors.get("nominees").getKey());

        aggregate.setNominees(java.util.List.of(Nominee.builder().build()));
        assertTrue(new NomineeSpecification()
                .validate(fullContext(aggregate, null, null, null, null, null, null)).isEmpty());
    }

    @Test
    void specialSavingsRulesFail() {
        LoanProposal aggregate = new LoanProposal();
        aggregate.setSpecialSavingsAccountIds(java.util.List.of("SSA-1"));
        aggregate.setSpecialSavingsAccountNumbers(java.util.List.of("ACC-9"));
        Member member = Member.builder()
                .specialSavingsAccounts(java.util.List.of("ACC-1"))
                .accountsWithActiveLoans(java.util.List.of())
                .build();
        LoanProduct nonLien = LoanProduct.builder().loanProductType("GENERAL").build();
        Map<String, LocalizedMessage> errors = new SpecialSavingsLienSpecification()
                .validate(fullContext(aggregate, member, nonLien, null, null, null, null));
        assertEquals("SPECIAL_SAVINGS_ACCOUNT_MISMATCH", errors.get("specialSavings").getKey());
    }

    @Test
    void prottashaParallelLimitExceededFails() {
        LoanProposal aggregate = new LoanProposal();
        aggregate.setProposedLoanAmount(new BigDecimal("60000"));
        Member member = Member.builder()
                .totalDisbursedProttashaAmount(new BigDecimal("50000"))
                .build();
        LoanProduct lien = LoanProduct.builder().loanProductType("LIEN").build();
        ProjectPolicy projectPolicy = ProjectPolicy.builder()
                .maxProttashaParallelAmount(new BigDecimal("100000"))
                .build();
        Map<String, LocalizedMessage> errors = new SpecialSavingsLienSpecification()
                .validate(fullContext(aggregate, member, lien, null, projectPolicy, null, null));
        assertEquals("PROTTASHA_PARALLEL_LIMIT_EXCEEDED", errors.get("proposedLoanAmount").getKey());
    }

    @Test
    void gotiLoanParallelRulesForProgotiProjectFail() {
        LoanProposal aggregate = new LoanProposal();
        Member member = Member.builder()
                .hasActiveRemittanceOrMigrationOrGeneralLoan(true)
                .hasGeneralLoanNotCurrentOrClosedOrWithOverdue(true)
                .build();
        LoanProduct goti = LoanProduct.builder().loanProductType("GOTI").build();
        Project progoti = Project.builder().code("PROGOTI-01").build();
        Map<String, LocalizedMessage> errors = new ProjectSpecificRulesSpecification()
                .validate(fullContext(aggregate, member, goti, progoti, null, null, null));
        assertEquals("PARALLEL_LOAN_NOT_ALLOWED", errors.get("parallelLoan").getKey());
        assertEquals("MEMBER_CANNOT_AVAIL_LOAN", errors.get("memberEligibility").getKey());
    }

    @Test
    void progotiChecklistOnNonProgotiProjectFails() {
        LoanProposal aggregate = new LoanProposal();
        aggregate.setProgotiDocumentChecklist(new com.bits.loanproposal.domain.value.ProgotiDocumentChecklist(
                true, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null));
        Project general = Project.builder().code("DABI").name("Dabi").build();
        Map<String, LocalizedMessage> errors = new ProjectSpecificRulesSpecification()
                .validate(fullContext(aggregate, activeMember(), null, general, null, null, null));
        assertEquals("PROGOTI_CHECKLIST_NOT_APPLICABLE", errors.get("progotiChecklist").getKey());
    }

    @Test
    void coBorrowerNotApplicableFails() {
        LoanProposal aggregate = new LoanProposal();
        aggregate.setCoBorrower(CoBorrower.builder().name("X").build());
        LoanProduct product = LoanProduct.builder().requiresCoBorrower(false).build();
        Map<String, LocalizedMessage> errors = new CoBorrowerSpecification()
                .validate(fullContext(aggregate, null, product, null, null, null, null));
        assertEquals("CO_BORROWER_NOT_APPLICABLE", errors.get("coBorrower").getKey());

        LoanProduct requiring = LoanProduct.builder().requiresCoBorrower(true).build();
        errors = new CoBorrowerSpecification()
                .validate(fullContext(aggregate, null, requiring, null, null, null, null));
        assertTrue(errors.isEmpty());
    }

    @Test
    void parallelLoanRulesFail() {
        LoanProposal aggregate = new LoanProposal();
        aggregate.setLoanProposalType(LoanProposalType.UPG);
        aggregate.setLoanProductId(7L);
        Member member = Member.builder()
                .hasExistingLoan(true)
                .hasPriorTUPLoan(false)
                .activeLoanProductIds(java.util.List.of(7L))
                .build();
        LoanProduct noParallel = LoanProduct.builder().allowsParallelLoans(false).build();
        Map<String, LocalizedMessage> errors = new ParallelCoExistingLoanSpecification()
                .validate(fullContext(aggregate, member, noParallel, null, null, null, null));
        assertEquals("PARALLEL_LOAN_NOT_ALLOWED", errors.get("parallelLoan").getKey());
        assertEquals("UPG_MUST_TAKE_GENERAL_LOAN_FIRST", errors.get("upgFirstLoan").getKey());
    }

    @Test
    void missingLoanProductDetailsFailsInstallmentConfig() {
        Map<String, LocalizedMessage> errors = new com.bits.loanproposal.domain.specification.rules
                .InstallmentConfigurationSpecification()
                .validate(fullContext(new LoanProposal(), null, null, null, null, null, null));
        assertEquals("INSTALLMENT_CALC_DETAILS_MISSING", errors.get("installmentDetails").getKey());
    }

    @Test
    void rocketWalletMismatchOnDisbursementAndCollectionFails() {
        LoanProposal aggregate = new LoanProposal();
        aggregate.setModeOfPayment(new OtcModeOfPayment(1L, ModeOfPaymentSubType.ROCKET,
                null, null, null, null, null, null, null, "01711111111", null, null));
        aggregate.setAutoDebitCollection(new com.bits.loanproposal.domain.value.AutoDebitCollection(
                com.bits.loanproposal.domain.enums.AutoDebitCollectionSubType.ROCKET,
                null, null, null, "01722222222"));
        Member member = Member.builder().rocketWalletNumber("01700000000").build();
        Map<String, LocalizedMessage> errors = new ModeOfPaymentRocketWalletSpecification()
                .validate(fullContext(aggregate, member, null, null, null, null, null));
        assertEquals("ROCKET_WALLET_MISMATCH", errors.get("modeOfPaymentDisbursement").getKey());
        assertEquals("ROCKET_WALLET_MISMATCH", errors.get("modeOfPaymentCollection").getKey());
    }

    @Test
    void digitalDisbursementWithoutModeOfPaymentFails() {
        LoanProposal aggregate = new LoanProposal();
        aggregate.setIsDigitalDisbursement(true);
        aggregate.setPremiumAmount(new BigDecimal("-1"));
        Map<String, LocalizedMessage> errors = new DigitalDisbursementSpecification()
                .validate(fullContext(aggregate, null, null, null, null, null, null));
        assertEquals("MODE_OF_PAYMENT_REQUIRED_FOR_DIGITAL", errors.get("modeOfPayment").getKey());
        assertEquals("PREMIUM_AMOUNT_NEGATIVE", errors.get("premiumAmount").getKey());
    }

    @Test
    void bankAccountOwnershipCheckedForBankPayments() {
        LoanProposal aggregate = new LoanProposal();
        aggregate.setMemberId(1L);
        aggregate.setModeOfPayment(new OtcModeOfPayment(1L, ModeOfPaymentSubType.BEFTN,
                "AC-1", null, 9L, null, null, null, null, null, null, null));
        Bank otherMembersBank = Bank.builder().bankId(9L).memberId(2L).build();
        LoanProposalValidationContext ctx = new LoanProposalValidationContext(
                null, null, null, null, null, null, null, null, null, null, null, otherMembersBank, aggregate);
        Map<String, LocalizedMessage> errors = new DigitalDisbursementSpecification().validate(ctx);
        assertEquals("BANK_DOES_NOT_MATCH_MEMBER", errors.get("bankAccount").getKey());
    }

    @Test
    void migrationLoanCountryRulesFail() {
        LoanProposal aggregate = new LoanProposal();
        LoanProduct migration = LoanProduct.builder().loanProductType("MIGRATION").build();
        Map<String, LocalizedMessage> errors = new MigrationCountrySpecification()
                .validate(fullContext(aggregate, null, migration, null, null, null, null));
        assertEquals("MIGRATION_COUNTRY_MANDATORY", errors.get("country").getKey());

        aggregate.setCountryId(3L);
        Country notConfigured = Country.builder().countryId(3L).isMigrationConfigured(false).build();
        LoanProposalValidationContext ctx = new LoanProposalValidationContext(
                null, migration, null, null, null, null, null, null, null, null, notConfigured, null, aggregate);
        errors = new MigrationCountrySpecification().validate(ctx);
        assertEquals("MIGRATION_COUNTRY_NOT_CONFIGURED", errors.get("country").getKey());
    }

    @Test
    void fireInsuranceRequestedValidatesDetails() {
        LoanProposal aggregate = new LoanProposal();
        aggregate.setWantsFireInsurance(true);
        aggregate.setProposedLoanAmount(new BigDecimal("50000"));
        aggregate.setFireInsuranceDetails(new com.bits.loanproposal.domain.value.FireInsuranceDetails(
                null, null, "01700", null, null, null, null, null, null,
                null, new BigDecimal("40000"), null, null, null, null));
        Map<String, LocalizedMessage> errors = new FireInsuranceSpecification()
                .validate(fullContext(aggregate, null, null, null, null, null, null));
        assertEquals("FIRE_INSURANCE_ID_NOT_FOUND", errors.get("fireInsuranceProductId").getKey());
        assertEquals("FIRE_INSURANCE_PHONE_INVALID", errors.get("businessPhone").getKey());
        assertEquals("FIRE_INSURANCE_INSURED_AMOUNT_MISMATCH",
                errors.get("fireInsuranceInsuredAmount").getKey());
    }

    @Test
    void fireNomineeWithoutFireInsuranceFails() {
        LoanProposal aggregate = new LoanProposal();
        aggregate.setWantsFireInsurance(false);
        aggregate.setNominees(java.util.List.of(
                Nominee.builder().insuranceTypes(java.util.List.of("FIRE")).build()));
        Map<String, LocalizedMessage> errors = new FireInsuranceSpecification()
                .validate(fullContext(aggregate, null, null, null, null, null, null));
        assertEquals("FIRE_INSURANCE_NOT_APPLICABLE_FOR_NOMINEE", errors.get("nominee").getKey());
    }

    @Test
    void memberOverSeventyFailsAgeLimit() {
        LoanProposal aggregate = new LoanProposal();
        aggregate.setApplicationDate(LocalDate.of(2026, 7, 6));
        Member member = Member.builder()
                .dateOfBirth(LocalDate.of(1950, 1, 1))
                .isGolden(false)
                .build();
        Map<String, LocalizedMessage> errors = new AgeLimitSpecification()
                .validate(fullContext(aggregate, member, null, null, null, null, null));
        assertEquals("MEMBER_AGE_INELIGIBLE", errors.get("memberAge").getKey());

        // golden member on lien product is exempt
        Member golden = Member.builder()
                .dateOfBirth(LocalDate.of(1950, 1, 1))
                .isGolden(true)
                .build();
        LoanProduct lien = LoanProduct.builder().loanProductType("LIEN").build();
        Map<String, LocalizedMessage> goldenErrors = new AgeLimitSpecification()
                .validate(fullContext(aggregate, golden, lien, null, null, null, null));
        assertTrue(goldenErrors.isEmpty(), () -> "expected exemption but got: " + goldenErrors);
    }

    @Test
    void moneyPlantMissingDataFails() {
        LoanProposal aggregate = new LoanProposal();
        aggregate.setApplicationDate(LocalDate.of(2026, 7, 6));
        LoanProduct moneyPlant = LoanProduct.builder().loanProductType("MONEY_PLANT").build();
        Member member = Member.builder().dateOfBirth(LocalDate.of(1990, 1, 1)).build();
        Map<String, LocalizedMessage> errors = new MoneyPlantSpecification()
                .validate(fullContext(aggregate, member, moneyPlant, null, null, null, null));
        assertEquals("MONEY_PLANT_DURATION_NULL", errors.get("duration").getKey());
        assertEquals("MONEY_PLANT_AMOUNT_NULL", errors.get("proposedLoanAmount").getKey());
        assertEquals("SPECIAL_SAVINGS_NOT_FOUND", errors.get("specialSavings").getKey());

        // non money-plant product: spec is silent
        assertTrue(new MoneyPlantSpecification()
                .validate(fullContext(aggregate, member, null, null, null, null, null)).isEmpty());
    }

    @Test
    void schemeSectorMappingFails() {
        LoanProposal aggregate = new LoanProposal();
        aggregate.setSectorId(4L);
        LoanProduct product = LoanProduct.builder().loanProductId(7L).build();
        Scheme scheme = Scheme.builder()
                .loanProductMappings(java.util.List.of(99L))
                .sectorMappings(java.util.List.of(1L))
                .build();
        LoanProposalValidationContext ctx = new LoanProposalValidationContext(
                null, product, null, null, scheme, null, null, null, null, null, null, null, aggregate);
        Map<String, LocalizedMessage> errors = new SchemeSectorMappingSpecification().validate(ctx);
        assertEquals("SCHEME_NOT_MAPPED_TO_LOAN_PRODUCT", errors.get("scheme").getKey());
        assertEquals("SECTOR_NOT_MAPPED_TO_LOAN_PRODUCT", errors.get("sector").getKey());
    }

    @Test
    void bankModeOfPaymentRulesFail() {
        LoanProposal aggregate = new LoanProposal();
        aggregate.setBranchId(10L);
        aggregate.setProposedLoanAmount(new BigDecimal("100000"));
        aggregate.setModeOfPayment(new OtcModeOfPayment(1L, ModeOfPaymentSubType.BEFTN,
                "AC-1", null, 9L, null, null, null, null, null, null, null));
        Bank bank = Bank.builder()
                .bankId(9L).memberId(1L)
                .balance(new BigDecimal("500"))
                .isOverdraftAccount(false)
                .build();
        com.bits.loanproposal.application.dto.sourcedata.Branch branch =
                com.bits.loanproposal.application.dto.sourcedata.Branch.builder()
                        .branchId(10L).bankId(9L).build();
        LoanProposalValidationContext ctx = new LoanProposalValidationContext(
                null, null, null, null, null, null, null, branch, null, null, null, bank, aggregate);
        Map<String, LocalizedMessage> errors = new BankModeOfPaymentSpecification().validate(ctx);
        assertEquals("BANK_INSUFFICIENT_BALANCE", errors.get("bankBalance").getKey());
        assertEquals("BEFTN_NOT_ALLOWED_SAME_BANK", errors.get("modeOfPayment").getKey());
    }

    @Test
    void chequePaymentRequiresDocumentNumberAndDate() {
        LoanProposal aggregate = new LoanProposal();
        aggregate.setModeOfPayment(new OtcModeOfPayment(1L, ModeOfPaymentSubType.CHEQUE,
                "AC-1", null, 9L, null, null, null, null, null, null, null));
        Bank bank = Bank.builder().bankId(9L).build();
        LoanProposalValidationContext ctx = new LoanProposalValidationContext(
                null, null, null, null, null, null, null, null, null, null, null, bank, aggregate);
        Map<String, LocalizedMessage> errors = new BankModeOfPaymentSpecification().validate(ctx);
        assertEquals("BANK_PAYMENT_DOCUMENT_NUMBER_INVALID", errors.get("paymentSubTypeNumber").getKey());
        assertEquals("BANK_PAYMENT_DOCUMENT_DATE_INVALID", errors.get("paymentSubTypeDate").getKey());
    }
}
