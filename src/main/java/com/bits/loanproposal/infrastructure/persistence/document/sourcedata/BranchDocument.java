package com.bits.loanproposal.infrastructure.persistence.document.sourcedata;

import com.bits.ddd.annotation.MongoSourceData;
import com.bits.ddd.dto.SourceData;
import com.bits.loanproposal.application.dto.sourcedata.Branch;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "branch_snapshot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MongoSourceData
public class BranchDocument extends SourceData<Long> {

    @Id
    private Long branchId;
    private String code;
    private String name;
    private Long bankId;
    private LocalDate lastAccountingBusinessDate;

    @Override
    public Long id() {
        return branchId;
    }

    public Branch toModel() {
        return Branch.builder()
                .id(branchId != null ? String.valueOf(branchId) : null)
                .branchId(branchId)
                .code(code)
                .name(name)
                .bankId(bankId)
                .lastAccountingBusinessDate(lastAccountingBusinessDate)
                .build();
    }
}
