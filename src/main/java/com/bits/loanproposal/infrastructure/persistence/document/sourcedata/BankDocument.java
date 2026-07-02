package com.bits.loanproposal.infrastructure.persistence.document.sourcedata;

import com.bits.ddd.annotation.MongoSourceData;
import com.bits.ddd.dto.SourceData;
import com.bits.loanproposal.application.dto.sourcedata.Bank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "bank_snapshot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MongoSourceData
public class BankDocument extends SourceData<Long> {

    @Id
    private Long bankId;
    private String accountNumber;
    private String routingNumber;
    private BigDecimal balance;
    private Boolean isOverdraftAccount;
    private Long memberId;
    private Long branchBankId;

    @Override
    public Long id() {
        return bankId;
    }

    public Bank toModel() {
        return Bank.builder()
                .id(bankId != null ? String.valueOf(bankId) : null)
                .bankId(bankId)
                .accountNumber(accountNumber)
                .routingNumber(routingNumber)
                .balance(balance)
                .isOverdraftAccount(isOverdraftAccount)
                .memberId(memberId)
                .branchBankId(branchBankId)
                .build();
    }
}
