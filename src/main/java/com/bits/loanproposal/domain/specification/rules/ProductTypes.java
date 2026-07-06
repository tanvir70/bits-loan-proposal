package com.bits.loanproposal.domain.specification.rules;

import com.bits.loanproposal.application.dto.sourcedata.LoanProduct;

// ponytail: the doc never defines its product-type helpers (isLienProduct, isMigrationLoan, ...);
// all of them are matched on the free-text loanProductType field, verify strings against legacy data
final class ProductTypes {

    private ProductTypes() {}

    static boolean is(LoanProduct product, String type) {
        return product != null && type.equalsIgnoreCase(product.getLoanProductType());
    }
}
