package com.bits.loanproposal.infrastructure.config;

import com.bits.ddd.shared.domain.value.DomainStatus;
import org.bson.Document;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

@Configuration
public class DomainStatusMongoConverters {

    @Bean
    public Converter<Document, DomainStatus> documentToDomainStatusConverter() {
        return new DocumentToDomainStatusConverter();
    }

    @Bean
    public Converter<String, DomainStatus> stringToDomainStatusConverter() {
        return new StringToDomainStatusConverter();
    }

    @Bean
    public Converter<DomainStatus, String> domainStatusToStringConverter() {
        return new DomainStatusToStringConverter();
    }

    @ReadingConverter
    private static class DocumentToDomainStatusConverter implements Converter<Document, DomainStatus> {
        @Override
        public DomainStatus convert(Document source) {
            return source == null ? null : DomainStatus.of(source.getString("code"));
        }
    }

    @ReadingConverter
    private static class StringToDomainStatusConverter implements Converter<String, DomainStatus> {
        @Override
        public DomainStatus convert(String source) {
            return source == null ? null : DomainStatus.of(source);
        }
    }

    @WritingConverter
    private static class DomainStatusToStringConverter implements Converter<DomainStatus, String> {
        @Override
        public String convert(DomainStatus source) {
            return source == null ? null : source.code();
        }
    }
}
