package com.bits.loanproposal.presentation.constant;

public final class RouteConstant {

    public static final String LOAN_PROPOSALS = "/api/loan-proposals";
    public static final String LOAN_PROPOSALS_DELETE = "/{branchId}/{id}";
    public static final String LOAN_PROPOSALS_BULK_APPROVAL = "/bulk-approval";

    private RouteConstant() {
    }
}