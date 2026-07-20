package com.bits.loanproposal.infrastructure.persistence.converter;

import com.bits.ddd.shared.domain.value.DomainStatus;
import com.bits.loanproposal.domain.constant.LoanProposalTestStatuses;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

@Component
@WritingConverter
public final class DomainStatusDocumentWriteConverter implements Converter<DomainStatus, Document> {

    @Override
    public Document convert(DomainStatus source) {
        DomainStatus supportedStatus = LoanProposalTestStatuses.requireSupported(source);
        return new Document("code", supportedStatus.code());
    }
}
