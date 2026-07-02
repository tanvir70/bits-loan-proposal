package com.bits.loanproposal.presentation.controller.dto;

public record ProgotiDocumentChecklistRequestDto(
    Boolean commitmentLetter,
    Boolean collateralBond,
    Boolean bankStatement,
    Boolean securityCheck,
    Boolean originalDeed,
    Boolean bayaDeed,
    Boolean pittDeed,
    Boolean positionDeed,
    Boolean duplicateDocumentWithWithdrawalReceipt,
    Boolean dcr,
    Boolean dismissalForm,
    Boolean saOriginalPapers,
    Boolean rsOriginalPapers,
    Boolean taxReceipt,
    Boolean heirCertificate,
    Boolean stopRentOrAdvanceAgreement,
    Boolean seizedPropertyInvestigativeReport,
    String other
) {}
