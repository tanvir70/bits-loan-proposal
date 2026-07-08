# Create Behavior — Missing Implementation Report

Line-by-line diff of the DDD-EARS doc's `create()` pseudocode (DDD-REQ-002) and its
create-path requirements against the current implementation. Refreshed 2026-07-08 —
field population, the 21-specification chain, the field-population helpers, the
failed-event path, and the sequenced proposal number are complete; the gaps below remain.
Ranked by impact.

Companion docs: [spec-deviations.md](spec-deviations.md) covers deviations *inside* the 21
specifications; [legacy-questions.md](legacy-questions.md) tracks every open question for
the legacy system referenced below.

---

## Still open

### 1. Three REQ-013 sub-rules — blocked on undefined specs

`LoanAmountSpecification` now covers policy range, negative amounts, installment-amount
match, and product-details config match. Still missing, all blocked on legacy answers
(legacy-questions 1.1–1.3):

| Missing sub-rule | Blocker |
|---|---|
| Grant match via `computeGrantAmount(policy, scheme, agg)` | Formula undefined |
| Variable installments selected but no variable-installment config | No config source exists |
| `isValidInterestRate(amount, rate, businessDate)` | Helper undefined, no rate table |

### 2. `guarantors` hardcoded to `emptyList()`

The doc says "attached from member record", but the member source-data snapshot carries no
guarantor data. Needs the snapshot extended first (legacy-questions 4.1). Until then the
mapper's `emptyList()` stands.

### 3. Full day-open validation

Business rule (not in EARS): a proposal cannot be created when the branch business day is
not open. A partial version is live — the handler rejects creation with
`BUSINESS_DATE_NOT_AVAILABLE` when the branch has no `lastAccountingBusinessDate` — but the
real "day is open" signal doesn't exist in our source data (legacy-questions 3.3).

### 4. Sequence counter scope

`ProposalNumberSequenceService` counts globally per month; the unique index
(`proposalNumber + branchId`) hints legacy may number per branch. One-line key change
(`{branchCode}-{yearMonth}`) once confirmed (legacy-questions 3.1).

### 5. Derivations live in the wrong layer, with invented internals (known, commented)

`deriveIsDigital` and `deriveTransactionDescription` sit in the MapStruct mapper
(application layer), not inside `create()` as the doc places them, and their internals are
guesses — the doc never defines them (legacy-questions 2.1–2.2). The former side-bug
(null voCode → literal `"null"` in the description) is fixed: missing parts now yield a
null description. `premiumAmount` sourcing is likewise a guess: the create request carries
no premium, so it's taken from the insurance-product snapshot.

### 6. Cosmetic only

`domainStatus = CREATED` is assigned mid-field-population instead of after validation (doc
line ~438). No observable difference — validation throws before persist either way.

---

## Resolved since the original report

| Original gap | Resolution |
|---|---|
| #1 (partial) two implementable REQ-013 sub-rules | Installment-amount match + product-details config match in `LoanAmountSpecification` (commit `8ed6984`) |
| #2 four field-population helpers skipped | `defaultFireInsuranceDetails`, `assignNomineeIds`, `linkGuardianToFirstNominee`, `assignCoBorrowerId` implemented on the aggregate and called from `create()` (commit `cff634b`) |
| #3 validation failure path | `LoanProposalValidationException` carries `LoanProposalFailedEvent.validationError(traceId, errors)`; handler publishes the failed event (commit `6efd984`) |
| #4 random proposal number | Mongo `findAndModify` counter in `ProposalNumberSequenceService`, keyed by year-month; `sequence` field added to creation data |
| #5 `premiumAmount` always null | Mapped from `sourceData.insuranceProduct.premiumAmount` (guess — flagged in code) |
| #5 `applicationDate = LocalDate.now()` | Derived from `Branch.lastAccountingBusinessDate`; null when absent (no fallback to today), which the handler rejects before consuming a sequence number |
| #6 side-bug: null voCode → literal `"null"` | Missing description parts now yield a null transaction description |

## Verified present (not missing)

- id-null guard in `create()`
- duplicate-id check in the handler (Gate 5b idempotency)
- missing-business-date rejection before the sequence is consumed (partial day-open rule)
- all ~100 field copies, including `approvedLoanAmount = proposedLoanAmount`,
  `approvedNumberOfInstallments = numberOfInstallments`,
  `approvedInstallmentAmount = recalculatedInstallmentAmount`,
  `approvedDurationInMonths = proposalDurationInMonths`
- `loanProposalStatus = PENDING`, `loanProposalType` default `NORMAL_LOAN`,
  `dataSource = OTC`, `loanSecurity* ?? 0` defaults
- all 21 specifications chained in doc order inside `validate()`
- `LoanProposalCreatedEvent` emission via `LoanProposalEventMapper`
- failed-event publication on validation failure (fire-and-forget, no outbox row)
- persist + outbox publish via the library's `DomainPersistenceService` / `MessageProcessor`

## Suggested next steps (value per line of code)

1. Get legacy answers for the high-priority rows in [legacy-questions.md](legacy-questions.md)
   — 3.1 (sequence scope) and 3.3 (day-open signal) are one-line and small fixes respectively.
2. Extend the member snapshot with guarantor data, then drop the `emptyList()` hardcode.
3. Implement the three REQ-013 sub-rules as their definitions arrive.
