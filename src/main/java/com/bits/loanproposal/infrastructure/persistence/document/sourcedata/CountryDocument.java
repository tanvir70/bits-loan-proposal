package com.bits.loanproposal.infrastructure.persistence.document.sourcedata;

import com.bits.ddd.annotation.MongoSourceData;
import com.bits.ddd.domain.sourcedata.SourceData;
import com.bits.loanproposal.application.dto.sourcedata.Country;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "country_snapshot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MongoSourceData
public class CountryDocument extends SourceData<Long> {

    @Id
    private Long countryId;
    private String code;
    private String name;
    private Boolean isMigrationConfigured;

    @Override
    public Long id() {
        return countryId;
    }

    public Country toModel() {
        return Country.builder()
                .id(countryId != null ? String.valueOf(countryId) : null)
                .countryId(countryId)
                .code(code)
                .name(name)
                .isMigrationConfigured(isMigrationConfigured)
                .build();
    }
}
