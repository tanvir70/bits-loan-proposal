package com.bits.loanproposal.application.dto.sourcedata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VillageOrganisation {
    private String id;
    private Long voId;
    private String code;
    private String name;
    private String category;
    private Long projectId;
    private Long branchId;
    private Boolean isActive;
}
