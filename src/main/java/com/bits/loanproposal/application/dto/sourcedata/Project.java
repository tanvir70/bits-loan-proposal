package com.bits.loanproposal.application.dto.sourcedata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    private String id;
    private Long projectId;
    private String code;
    private String name;
    private String associationType;
    private String landingType;
    private List<Long> branchMappings;
}
