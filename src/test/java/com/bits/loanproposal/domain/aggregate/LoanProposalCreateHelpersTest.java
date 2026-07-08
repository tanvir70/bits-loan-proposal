package com.bits.loanproposal.domain.aggregate;

import com.bits.loanproposal.domain.entity.CoBorrower;
import com.bits.loanproposal.domain.entity.Guardian;
import com.bits.loanproposal.domain.entity.Nominee;
import com.bits.loanproposal.domain.value.FireInsuranceDetails;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class LoanProposalCreateHelpersTest {

    private FireInsuranceDetails details(BigDecimal insuredAmount, Integer duration) {
        return new FireInsuranceDetails("Shop", null, "01700000000", null, null, null, null,
                null, null, null, insuredAmount, duration, null, null, null);
    }

    @Test
    void fireInsuranceDefaultsAppliedWhenAbsent() {
        FireInsuranceDetails defaulted = LoanProposal.defaultFireInsuranceDetails(
                details(null, null), new BigDecimal("50000"), 6);
        assertEquals(new BigDecimal("50000"), defaulted.fireInsuranceInsuredAmount());
        assertEquals(12, defaulted.durationOfFireInsurance()); // max(6, 12)

        FireInsuranceDetails longLoan = LoanProposal.defaultFireInsuranceDetails(
                details(null, null), new BigDecimal("50000"), 24);
        assertEquals(24, longLoan.durationOfFireInsurance()); // max(24, 12)

        FireInsuranceDetails explicit = LoanProposal.defaultFireInsuranceDetails(
                details(new BigDecimal("40000"), 18), new BigDecimal("50000"), 6);
        assertEquals(new BigDecimal("40000"), explicit.fireInsuranceInsuredAmount());
        assertEquals(18, explicit.durationOfFireInsurance());

        assertNull(LoanProposal.defaultFireInsuranceDetails(null, new BigDecimal("50000"), 6));
    }

    @Test
    void nomineeIdsAndEqualSharesAssigned() {
        List<Nominee> nominees = List.of(Nominee.builder().name("A").build(),
                Nominee.builder().name("B").build());
        LoanProposal.assignNomineeIds(nominees);
        assertNotNull(nominees.get(0).getId());
        assertNotNull(nominees.get(1).getId());
        assertEquals(50.0, nominees.get(0).getSharePercentage());
        assertEquals(50.0, nominees.get(1).getSharePercentage());
    }

    @Test
    void explicitSharePercentagesKept() {
        List<Nominee> nominees = List.of(
                Nominee.builder().name("A").sharePercentage(70.0).build(),
                Nominee.builder().name("B").sharePercentage(30.0).build());
        LoanProposal.assignNomineeIds(nominees);
        assertEquals(70.0, nominees.get(0).getSharePercentage());
        assertEquals(30.0, nominees.get(1).getSharePercentage());
    }

    @Test
    void proposalNumberUsesYearMonthAndSequence() {
        assertEquals("202607-00042",
                LoanProposal.generateProposalNumber(java.time.LocalDate.of(2026, 7, 7), 42L));
        assertEquals("202612-99999",
                LoanProposal.generateProposalNumber(java.time.LocalDate.of(2026, 12, 1), 99999L));
    }

    @Test
    void guardianLinkedToFirstNomineeAndCoBorrowerIdAssigned() {
        List<Nominee> nominees = LoanProposal.assignNomineeIds(
                List.of(Nominee.builder().name("A").build()));
        Guardian guardian = LoanProposal.linkGuardianToFirstNominee(
                Guardian.builder().name("G").build(), nominees);
        assertEquals(nominees.get(0).getId(), guardian.getId());

        assertNull(LoanProposal.linkGuardianToFirstNominee(null, nominees));

        CoBorrower coBorrower = LoanProposal.assignCoBorrowerId(CoBorrower.builder().name("C").build());
        assertNotNull(coBorrower.getId());
    }
}
