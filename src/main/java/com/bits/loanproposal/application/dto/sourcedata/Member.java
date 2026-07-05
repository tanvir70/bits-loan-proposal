package com.bits.loanproposal.application.dto.sourcedata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    private String id;
    private Long memberId;
    private Long memberClassificationId;
    private Long branchId;
    private Long projectId;
    private Long voId;
    private String voCode;
    private String status;
    private Boolean isScreened;
    private LocalDate dateOfBirth;
    private String rocketWalletNumber;
    private Boolean isGolden;
    private String nationalId;
    private boolean hasActiveRemittanceOrMigrationOrGeneralLoan;
    private boolean hasGeneralLoanNotCurrentOrClosedOrWithOverdue;
    private boolean isFirstGotiLoan;
    private boolean isGotiTopUp;
    private int daysSincePreviousGotiLoan;
    private boolean hasActiveGotiLoan;
    private boolean hasExistingLoan;
    private boolean hasActiveGeneralLoan;
    private boolean hasActiveRemittanceLoan;
    private boolean hasPriorTUPLoan;
    private List<Long> activeLoanProductIds;
    private List<String> specialSavingsAccounts;
    private List<String> accountsWithActiveLoans;
    private BigDecimal totalDisbursedProttashaAmount;
    private boolean hasAmarHishabAccount;
    private BigDecimal amarHishabBalance;

    public boolean ownsSpecialSavingsAccount(String accountNumber) {
        return specialSavingsAccounts != null && specialSavingsAccounts.contains(accountNumber);
    }

    public boolean ownsAccount(String accountNumber) {
        return ownsSpecialSavingsAccount(accountNumber);
    }

    public boolean existingLoanForAccount(String accountNumber) {
        return accountsWithActiveLoans != null && accountsWithActiveLoans.contains(accountNumber);
    }

    public boolean hasActiveNonClosedLoanOfSameProduct(Long productId) {
        return activeLoanProductIds != null && activeLoanProductIds.contains(productId);
    }
}
