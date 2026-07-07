# Create Behavior — Missing Implementation Report

Line-by-line diff of the DDD-EARS doc's `create()` pseudocode (DDD-REQ-002) and its
create-path requirements against the current implementation. Field population and the
21-specification chain are complete; the gaps below remain. Ranked by impact.

Companion doc: [spec-deviations.md](spec-deviations.md) covers deviations *inside* the 21
specifications; this file covers what the create behavior itself still misses.

---

## 1. `LoanAmountSpecification` is a slim stand-in for REQ-013 (biggest gap)

The doc's spec #5 is `LoanAmountGrantInstallmentSpecification` with 7 sub-rules. Ours only
does policy range + negative-amount/grant checks. Missing — and notably **three of these
are implementable today with data we already have**:

| Missing sub-rule | Data status |
|---|---|
| `installmentAmount` vs `approvedInstallmentAmount` (recalculated) → "Your provided installment amount is wrong" | **Implementable now** — both fields sit on the aggregate |
| Installment count / duration / interest rate vs `LoanProductDetails.installmentCount/durationMonths/interestRate` → INSTALLMENT_CONFIG_MISMATCH | **Implementable now** — DTO fields exist, unused |
| Grant match via `computeGrantAmount(policy, scheme, agg)` → "Your provide grant amount is wrong" | Partially — `LoanProductPolicy.grantPercentage` and `Scheme.assetGrantPercentageByVoCategory` exist, but the doc's formula is undefined |
| Variable installments selected but no variable-installment config | Not implementable — no config lookup exists |
| `isValidInterestRate(amount, rate, businessDate)` → "Interest rate is not valid." | Not implementable — helper undefined, no rate table |

## 2. Aggregate field-population helpers — all four skipped silently

The doc's `create()` calls four transformers that were replaced with plain copies. Unlike
the spec deviations, these were **not** flagged with `ponytail:` comments in code — they
are undocumented misses:

| Doc helper | What it should do | Current behavior |
|---|---|---|
| `defaultFireInsuranceDetails(creationData)` | Default insured amount → `proposedLoanAmount` if absent; default duration → `max(proposalDurationInMonths, 12)` if absent | Copied as-is. Compounds with `FireInsuranceSpecification`: its insured-amount mismatch check skips when the field is null, so an un-defaulted proposal is never checked |
| `assignNomineeIds(nominees)` | Assign nominee IDs + share percentages ("Shares total 100%") | Copied as-is — no IDs, no shares |
| `linkGuardianToFirstNominee(guardian, nominees)` | Link the guardian entity to the first nominee | Copied as-is |
| `assignCoBorrowerId(coBorrower)` | Assign the co-borrower ID | Copied as-is |

## 3. Validation failure path doesn't match the doc

- *Doc says:* `THROW LoanProposalValidationException(LoanProposalFailedEvent.validationError(traceId, errors))`
  — a **failed event** carrying the traceId and the structured error map.
- *Code does:* `throw new DomainValidationException("LOAN_PROPOSAL_VALIDATION_FAILED", joinedString)`.
- *Consequences:* no `LoanProposalFailedEvent` is produced, the traceId is lost from the
  error, and the `LocalizedMessage` keys/args are flattened into one string — which also
  defeats the library's MessageSource translation machinery downstream.

## 4. Proposal number: random, not sequenced

- *Doc says:* `generateProposalNumber(creationData.businessDate, creationData.sequence)` →
  format `{YYYY}{MM}-{seq:5}`.
- *Code does:* `ThreadLocalRandom.nextInt(100_000)` (known `ponytail:` shortcut).
- *Gaps:* collisions possible under load; `LoanProposalCreationData` has no `sequence`
  field at all. Needs a Mongo counter collection (e.g. `findAndModify` with `$inc`) keyed
  by year-month.

## 5. Mapper hardcodes that contradict the doc

In `LoanProposalDataMapper`:

| Field | Doc | Current | Impact |
|---|---|---|---|
| `premiumAmount` | copied from creation data | `@Mapping(ignore = true)` → **always null** | Two specs read it (negative-premium check in DigitalDisbursement; second-insurer premium flow) — both permanently see null |
| `guarantors` | "attached from member record" | hardcoded `emptyList()` | Guarantor data never reaches the aggregate |
| `applicationDate` | business date (likely `Branch.lastAccountingBusinessDate` — the field exists on the Branch snapshot, unused) | `LocalDate.now()` | Product/policy active-on checks and age calculations run against the server date, not the accounting business date |

## 6. Derivations live in the wrong layer (known, already commented)

`derivedDigitalDisbursementFlag` and `deriveCustomerReference` are implemented in the
MapStruct mapper (application layer), not inside `create()` as the doc places them — and
their internals are invented (the doc never defines them; source EARS file not in repo).
Both carry "not defined in ears" comments in code. Known side-bug: a null voCode produces
a literal `"null"` in the transaction description.

## 7. Cosmetic only

`domainStatus = CREATED` is assigned mid-field-population instead of after validation (doc
line ~438). No observable difference — validation throws before persist either way.

---

## Verified present (not missing)

- id-null guard in `create()`
- duplicate-id check in the handler (Gate 5b idempotency)
- all ~100 field copies, including `approvedLoanAmount = proposedLoanAmount`,
  `approvedNumberOfInstallments = numberOfInstallments`,
  `approvedInstallmentAmount = recalculatedInstallmentAmount`,
  `approvedDurationInMonths = proposalDurationInMonths`
- `loanProposalStatus = PENDING`, `loanProposalType` default `NORMAL_LOAN`,
  `dataSource = OTC`, `loanSecurity* ?? 0` defaults
- all 21 specifications chained in doc order inside `validate()`
- `LoanProposalCreatedEvent` emission via `LoanProposalEventMapper`
- persist + outbox publish via the library's `DomainPersistenceService` / `MessageProcessor`

## Suggested fix order (value per line of code)

1. Add the two implementable REQ-013 sub-rules (installment-amount match, details match).
2. Stop ignoring `premiumAmount` in the mapper.
3. Implement the four field-population helpers (#2) — small, pure functions on the aggregate.
4. Proposal-number Mongo counter (#4).
5. Failed-event error path (#3) — needs a `LoanProposalFailedEvent` type decision first.
6. Business-date sourcing (#5, applicationDate) — confirm with legacy whether
   `Branch.lastAccountingBusinessDate` is the intended business date.
