package com.bits.loanproposal.infrastructure.persistence.document.sourcedata;

import com.bits.ddd.annotation.MongoSourceData;
import com.bits.ddd.dto.SourceData;
import com.bits.loanproposal.application.dto.sourcedata.Scheme;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Document(collection = "scheme_snapshot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MongoSourceData
public class SchemeDocument extends SourceData<Long> {

    @Id
    private Long schemeId;
    private String name;
    private List<Long> loanProductMappings;
    private List<Long> sectorMappings;
    private Map<String, BigDecimal> assetGrantPercentageByVoCategory;

    @Override
    public Long id() {
        return schemeId;
    }

    public Scheme toModel() {
        return Scheme.builder()
                .id(schemeId != null ? String.valueOf(schemeId) : null)
                .schemeId(schemeId)
                .name(name)
                .loanProductMappings(loanProductMappings)
                .sectorMappings(sectorMappings)
                .assetGrantPercentageByVoCategory(assetGrantPercentageByVoCategory)
                .build();
    }
}
