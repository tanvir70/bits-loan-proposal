package com.bits.loanproposal.infrastructure.persistence.document;

import com.bits.ddd.annotation.MongoSourceData;
import com.bits.ddd.domain.sourcedata.SourceData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "client_info_snapshots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MongoSourceData
public class ClientInfoDocument extends SourceData<String> {

    @Id
    private String id;
    private String clientName;
    private String status;
    private LocalDateTime lastEventTimestamp;

    @Override
    public String id() {
        return id;
    }
}
