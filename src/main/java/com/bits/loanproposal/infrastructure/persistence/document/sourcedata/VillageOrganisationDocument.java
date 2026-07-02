package com.bits.loanproposal.infrastructure.persistence.document.sourcedata;

import com.bits.ddd.annotation.MongoSourceData;
import com.bits.ddd.domain.sourcedata.SourceData;
import com.bits.loanproposal.application.dto.sourcedata.VillageOrganisation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "village_organisation_snapshot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MongoSourceData
public class VillageOrganisationDocument extends SourceData<Long> {

    @Id
    private Long voId;
    private String code;
    private String name;
    private String category;
    private Long projectId;
    private Long branchId;
    private Boolean isActive;

    @Override
    public Long id() {
        return voId;
    }

    public VillageOrganisation toModel() {
        return VillageOrganisation.builder()
                .id(voId != null ? String.valueOf(voId) : null)
                .voId(voId)
                .code(code)
                .name(name)
                .category(category)
                .projectId(projectId)
                .branchId(branchId)
                .isActive(isActive)
                .build();
    }
}
