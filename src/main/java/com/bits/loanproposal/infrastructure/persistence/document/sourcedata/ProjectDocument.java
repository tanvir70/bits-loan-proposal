package com.bits.loanproposal.infrastructure.persistence.document.sourcedata;

import com.bits.ddd.annotation.MongoSourceData;
import com.bits.ddd.dto.SourceData;
import com.bits.loanproposal.application.dto.sourcedata.Project;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "project_snapshot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MongoSourceData
public class ProjectDocument extends SourceData<Long> {

    @Id
    private Long projectId;
    private String code;
    private String name;
    private String associationType;
    private String landingType;
    private List<Long> branchMappings;

    @Override
    public Long id() {
        return projectId;
    }

    public Project toModel() {
        return Project.builder()
                .id(projectId != null ? String.valueOf(projectId) : null)
                .projectId(projectId)
                .code(code)
                .name(name)
                .associationType(associationType)
                .landingType(landingType)
                .branchMappings(branchMappings)
                .build();
    }
}
