package com.bits.loanproposal.application.mapper;

import com.bits.ddd.application.service.SourceDataContext;
import com.bits.loanproposal.application.dto.LoanProposalSourceData;
import com.bits.loanproposal.application.dto.sourcedata.ClientInfo;
import com.bits.loanproposal.infrastructure.persistence.document.ClientInfoDocument;

public final class LoanProposalSourceDataMapper {

    private LoanProposalSourceDataMapper() {}

    public static LoanProposalSourceData toSourceData(SourceDataContext context) {
        ClientInfoDocument document = context.get("clientInfo", ClientInfoDocument.class);
        
        ClientInfo clientInfo = null;
        if (document != null) {
            clientInfo = ClientInfo.builder()
                    .id(document.id())
                    .clientName(document.getClientName())
                    .status(document.getStatus())
                    .build();
        }
        
        return LoanProposalSourceData.builder()
                .clientInfo(clientInfo)
                .build();
    }
}
