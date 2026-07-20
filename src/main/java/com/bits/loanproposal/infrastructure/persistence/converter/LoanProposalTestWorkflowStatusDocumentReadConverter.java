package com.bits.loanproposal.infrastructure.persistence.converter;

import com.bits.loanproposal.domain.value.LoanProposalTestWorkflowStatus;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

@Component
@ReadingConverter
public final class LoanProposalTestWorkflowStatusDocumentReadConverter
        implements Converter<Document, LoanProposalTestWorkflowStatus> {

    @Override
    public LoanProposalTestWorkflowStatus convert(Document source) {
        Object code = source.get("code");
        if (!(code instanceof String stringCode)) {
            throw new IllegalArgumentException(
                    "Workflow status document must contain a String 'code' field");
        }
        return LoanProposalTestWorkflowStatus.fromCode(stringCode);
    }
}
