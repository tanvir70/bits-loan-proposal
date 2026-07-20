package com.bits.loanproposal.infrastructure.persistence.converter;

import com.bits.loanproposal.domain.value.LoanProposalTestWorkflowStatus;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

@Component
@WritingConverter
public final class LoanProposalTestWorkflowStatusDocumentWriteConverter
        implements Converter<LoanProposalTestWorkflowStatus, Document> {

    @Override
    public Document convert(LoanProposalTestWorkflowStatus source) {
        return new Document("code", source.code());
    }
}
