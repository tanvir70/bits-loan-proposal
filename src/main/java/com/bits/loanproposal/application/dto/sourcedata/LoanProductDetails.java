package com.bits.loanproposal.application.dto.sourcedata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanProductDetails {
    private String id;
    private Long loanProductDetailsId;
    private Long loanProductId;
    private Long frequencyId;
    private Integer durationMonths;
    private Integer installmentCount;
    private BigDecimal interestRate;
    private LocalDate activePeriodStart;
    private LocalDate activePeriodEnd;

    public boolean isActiveOn(LocalDate date) {
        if (date == null) return false;
        boolean afterStart = (activePeriodStart == null || !date.isBefore(activePeriodStart));
        boolean beforeEnd = (activePeriodEnd == null || !date.isAfter(activePeriodEnd));
        return afterStart && beforeEnd;
    }
}
