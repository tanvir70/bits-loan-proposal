package com.bits.loanproposal.infrastructure.persistence.converter;

import com.bits.ddd.shared.domain.value.DomainStatus;
import com.bits.loanproposal.domain.constant.LoanProposalTestStatuses;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

@Component
@ReadingConverter
public final class DomainStatusDocumentReadConverter implements Converter<Document, DomainStatus> {

    @Override
    public DomainStatus convert(Document source) {
        Object code = source.get("code");
        if (!(code instanceof String stringCode)) {
            throw new IllegalArgumentException(
                    "Domain status document must contain a String 'code' field");
        }
        return LoanProposalTestStatuses.fromCode(stringCode);
    }
}
