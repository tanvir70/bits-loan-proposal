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
public class Country {
    private String id;
    private Long countryId;
    private String code;
    private String name;
    private Boolean isMigrationConfigured;

    public boolean isConfiguredForMigration() {
        return Boolean.TRUE.equals(isMigrationConfigured);
    }
}
