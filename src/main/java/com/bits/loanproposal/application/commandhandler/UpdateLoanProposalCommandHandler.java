package com.bits.loanproposal.application.commandhandler;

import com.bits.ddd.annotation.RegisterCommandHandler;
import com.bits.ddd.handler.CommandHandler;
import com.bits.ddd.service.AggregateService;
import com.bits.ddd.service.SourceDataContext;
import com.bits.loanproposal.application.command.UpdateLoanProposalCommand;
import com.bits.loanproposal.application.dto.LoanProposalSourceData;
import com.bits.loanproposal.application.mapper.LoanProposalDataMapper;
import com.bits.loanproposal.application.mapper.LoanProposalSourceDataMapper;
import com.bits.loanproposal.application.service.LoanProposalQueryService;
import com.bits.loanproposal.application.service.UpdateLoanProposalSourceDataProvider;
import com.bits.loanproposal.domain.aggregate.LoanProposal;
import com.bits.loanproposal.domain.param.LoanProposalUpdateData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RegisterCommandHandler
@RequiredArgsConstructor
public class UpdateLoanProposalCommandHandler implements CommandHandler<UpdateLoanProposalCommand> {

    private final AggregateService<LoanProposal, String> aggregateService;
    private final UpdateLoanProposalSourceDataProvider sourceDataProvider;
    private final LoanProposalQueryService queryService;
    private final LoanProposalDataMapper dataMapper;

    @Override
    @Transactional
    public void handle(UpdateLoanProposalCommand command) {
        LoanProposal loanProposal = queryService.fetchByIdOrHandleFailure(command.getId(), command.getTracerId());
        SourceDataContext sourceDataContext = sourceDataProvider.provide(command, loanProposal);
        LoanProposalSourceData sourceData = LoanProposalSourceDataMapper.toSourceData(sourceDataContext);
        LoanProposalUpdateData updateData = dataMapper.toUpdateData(command, sourceData);

        loanProposal.update(updateData);

        aggregateService.save(loanProposal);
    }
}
