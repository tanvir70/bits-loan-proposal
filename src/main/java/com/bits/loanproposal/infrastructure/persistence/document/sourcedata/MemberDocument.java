package com.bits.loanproposal.infrastructure.persistence.document.sourcedata;

import com.bits.ddd.annotation.MongoSourceData;
import com.bits.ddd.dto.SourceData;
import com.bits.loanproposal.application.dto.sourcedata.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Document(collection = "member_snapshot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MongoSourceData
public class MemberDocument extends SourceData<Long> {

    @Id
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

    @Override
    public Long id() {
        return memberId;
    }

    public Member toModel() {
        return Member.builder()
                .id(memberId != null ? String.valueOf(memberId) : null)
                .memberId(memberId)
                .memberClassificationId(memberClassificationId)
                .branchId(branchId)
                .projectId(projectId)
                .voId(voId)
                .voCode(voCode)
                .status(status)
                .isScreened(isScreened)
                .dateOfBirth(dateOfBirth)
                .rocketWalletNumber(rocketWalletNumber)
                .isGolden(isGolden)
                .nationalId(nationalId)
                .hasActiveRemittanceOrMigrationOrGeneralLoan(hasActiveRemittanceOrMigrationOrGeneralLoan)
                .hasGeneralLoanNotCurrentOrClosedOrWithOverdue(hasGeneralLoanNotCurrentOrClosedOrWithOverdue)
                .isFirstGotiLoan(isFirstGotiLoan)
                .isGotiTopUp(isGotiTopUp)
                .daysSincePreviousGotiLoan(daysSincePreviousGotiLoan)
                .hasActiveGotiLoan(hasActiveGotiLoan)
                .hasExistingLoan(hasExistingLoan)
                .hasActiveGeneralLoan(hasActiveGeneralLoan)
                .hasActiveRemittanceLoan(hasActiveRemittanceLoan)
                .hasPriorTUPLoan(hasPriorTUPLoan)
                .activeLoanProductIds(activeLoanProductIds)
                .specialSavingsAccounts(specialSavingsAccounts)
                .accountsWithActiveLoans(accountsWithActiveLoans)
                .totalDisbursedProttashaAmount(totalDisbursedProttashaAmount)
                .hasAmarHishabAccount(hasAmarHishabAccount)
                .amarHishabBalance(amarHishabBalance)
                .build();
    }
}
