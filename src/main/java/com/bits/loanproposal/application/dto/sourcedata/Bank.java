package com.bits.loanproposal.application.dto.sourcedata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bank {
    private String id;
    private Long bankId;
    private String accountNumber;
    private String routingNumber;
    private BigDecimal balance;
    private Boolean isOverdraftAccount;
    private Long memberId;
    private Long branchBankId;

    public boolean accountBelongsToMember(Long memberId) {
        return memberId != null && memberId.equals(this.memberId);
    }

    public boolean isOverdraftAccount() {
        return Boolean.TRUE.equals(isOverdraftAccount);
    }
}
