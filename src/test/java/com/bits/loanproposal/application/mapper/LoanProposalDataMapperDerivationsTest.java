package com.bits.loanproposal.application.mapper;

import com.bits.loanproposal.application.command.CreateLoanProposalCommand;
import com.bits.loanproposal.application.dto.LoanProposalSourceData;
import com.bits.loanproposal.application.dto.sourcedata.Branch;
import com.bits.loanproposal.application.dto.sourcedata.VillageOrganisation;
import com.bits.loanproposal.presentation.controller.dto.OtcModeOfPaymentRequestDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LoanProposalDataMapperDerivationsTest {

    private final LoanProposalDataMapper mapper = Mappers.getMapper(LoanProposalDataMapper.class);

    @Test
    void applicationDateComesFromBranchBusinessDateOrIsNull() {
        LocalDate businessDate = LocalDate.of(2026, 7, 7);
        LoanProposalSourceData withDate = LoanProposalSourceData.builder()
                .branch(Branch.builder().lastAccountingBusinessDate(businessDate).build())
                .build();
        assertEquals(businessDate, mapper.deriveApplicationDate(withDate));

        assertNull(mapper.deriveApplicationDate(null));
        assertNull(mapper.deriveApplicationDate(LoanProposalSourceData.builder().build()));
        assertNull(mapper.deriveApplicationDate(LoanProposalSourceData.builder()
                .branch(Branch.builder().build()).build()));
    }

    @Test
    void transactionDescriptionNeverContainsLiteralNull() {
        CreateLoanProposalCommand command = mock(CreateLoanProposalCommand.class);
        when(command.getModeOfPayment()).thenReturn(digitalMode());
        when(command.getMemberId()).thenReturn(123L);

        LoanProposalSourceData complete = LoanProposalSourceData.builder()
                .branch(Branch.builder().code("B001").build())
                .villageOrganisation(VillageOrganisation.builder().code("VO9").build())
                .build();
        assertEquals("OTC-B001-VO9-123", mapper.deriveTransactionDescription(command, complete));

        LoanProposalSourceData noVo = LoanProposalSourceData.builder()
                .branch(Branch.builder().code("B001").build())
                .build();
        assertNull(mapper.deriveTransactionDescription(command, noVo));
    }

    // digital because digitalDisbursementModeId (last arg) is set
    private static OtcModeOfPaymentRequestDto digitalMode() {
        return new OtcModeOfPaymentRequestDto(null, null, null, null, null, null,
                null, null, null, null, null, 1L);
    }
}
