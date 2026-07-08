# Open Questions for the Legacy System

Everything the DDD-EARS doc left undefined and we implemented with a guess (marked
`ponytail:` / "not defined in ears" in code), plus rules that cannot be implemented at all
until the legacy system answers. Each row names the current stand-in so the answer can be
diffed against it. Companion docs: [create-flow-gaps.md](create-flow-gaps.md),
[spec-deviations.md](spec-deviations.md).

Priority: **H** = wrong guess produces wrong accept/reject decisions today,
**M** = missing check (too permissive), **L** = cosmetic or unlikely to differ.

---

## 1. Undefined formulas — blocked, not implemented at all

| # | Question for legacy | Current behavior | Code | Prio |
|---|---|---|---|---|
| 1.1 | What is the `computeGrantAmount(policy, scheme, agg)` formula? (`LoanProductPolicy.grantPercentage` and `Scheme.assetGrantPercentageByVoCategory` exist — how do they combine?) | Grant-match check skipped | `LoanAmountSpecification.java:54` | M |
| 1.2 | How is `isValidInterestRate(amount, rate, businessDate)` evaluated? Is there a rate table, and where does it live? | Interest-rate validity check skipped | `LoanAmountSpecification.java:54` | M |
| 1.3 | Where is the variable-installment configuration stored, and what does "no config" look like? | Variable-installment check skipped | `LoanAmountSpecification.java:54` | M |
| 1.4 | What are the fire-insurance premium and duration recalculation formulas (`calculateFireInsurancePremium` / `calculateFireInsuranceDuration`)? What is the trading-sector check? | Premium/duration recalculation and trading-sector check skipped | `FireInsuranceSpecification.java:51` | M |

## 2. Undefined helpers — implemented with a guessed reading

| # | Question for legacy | Our guess | Code | Prio |
|---|---|---|---|---|
| 2.1 | What exactly makes a disbursement digital (`derivedDigitalDisbursementFlag(modeOfPayment)`)? | Non-null digital mode ID or a bKash/Rocket wallet number | `LoanProposalDataMapper.java:105` | H |
| 2.2 | What is the real transaction-description format for digital disbursement? | Invented `OTC-{branchCode}-{voCode}-{memberId}` (also: null voCode currently prints literal `"null"`) | `LoanProposalDataMapper.java:113` | H |
| 2.3 | What are the actual product-type values behind `isLienProduct`, `isMigrationLoan`, `isLienOrMoneyPlantProduct`, etc.? | String match on free-text `loanProductType` (`LIEN`, `MONEY_PLANT`, …) | `ProductTypes.java:5`, `SpecialSavingsLienSpecification.java:65` | H |
| 2.4 | How is `isProgotiOrAdpProject` decided — project code list, type field, flag? | Project code/name containing `PROGOTI` or `ADP` | `ProjectSpecificRulesSpecification.java:52` | H |
| 2.5 | How is `isUPGProject(project)` decided? `Project` has no type field in our snapshot | Gate skipped | `ParallelCoExistingLoanSpecification.java:34` | M |
| 2.6 | What counts as `isCentralDisbursementMode(mop)`? | Non-null digital-disbursement mode ID | `DigitalDisbursementSpecification.java:20` | M |
| 2.7 | What defines `isBankMode(mop)` and `isValidDocumentNumber(doc)`? | "Carries bank coordinates" / non-blank string | `BankModeOfPaymentSpecification.java:30,75` | M |
| 2.8 | What is `isRecognisedFrequency(freq)`? | Range 1..10 (from the ">10 invalid" rule) | `RepaymentFrequencyModeOfPaymentSpecification.java:19` | L |
| 2.9 | How does `schemeExistsForLoanProductAndSector(loanProduct, sectorId)` resolve? Where is the product↔sector↔scheme mapping? | Absent scheme in snapshot = not mapped; no product-side sector check (LoanProduct carries no sectors) | `SchemeSectorMappingSpecification.java:16,26` | M |
| 2.10 | What is `DCS_MAX_NOMINEES`? | 3 (from the CSI/fire-insurance nominee limit) | `NomineeSpecification.java:13` | L |
| 2.11 | Which limit field drives loan exposure — doc says `projectPolicy`, our snapshot has it elsewhere? | Read from the only field shaped like it | `LoanExposureLimitSpecification.java:20` | M |
| 2.12 | What triggers `isAmarHishabhPremiumCollection(agg)`? | Only plausible field used | `spec-deviations.md` (line ~316) | M |

## 3. Proposal number & business date

| # | Question for legacy | Our guess | Code | Prio |
|---|---|---|---|---|
| 3.1 | Is the proposal-number sequence **per branch** per month or global per month? (Are a branch's numbers contiguous?) | Global per month; switch counter key to `{branchCode}-{yearMonth}` if per branch | `ProposalNumberSequenceService.java:14` | H |
| 3.2 | Is `Branch.lastAccountingBusinessDate` the intended application/business date for proposals? | Yes, with null when absent (no fallback to today) | `LoanProposalDataMapper.deriveApplicationDate` | H |
| 3.3 | What signals "business day is open"? (Rule: no proposal creation when the branch day is not open — no such flag exists in our source data) | Not implemented; no data | — | H |

## 4. Missing source data / entity links

| # | Question for legacy | Current behavior | Code | Prio |
|---|---|---|---|---|
| 4.1 | Where does guarantor data come from ("attached from member record")? | `guarantors` hardcoded to empty list | `LoanProposalDataMapper.java:33` | H |
| 4.2 | How are nominee IDs assigned and shares split beyond the "totals 100%" rule? What ID scheme? | UUIDs; equal split only where the doc's update rule implies it | `LoanProposal.java:472` | M |
| 4.3 | How is the guardian linked to the first nominee — is there a real link field? | Guardian shares the first nominee's ID (only representable link) | `LoanProposal.java:488` | M |
| 4.4 | How is a spouse identified? No spouse link exists in the member snapshot (needed for spouse-scoped insurer checks and the spouse engagement exemption) | Checks run unscoped / skipped | `InstallmentConfigurationSpecification.java:26`, `InsurancePolicyTypeSecondInsurerSpecification.java:77` | M |
| 4.5 | How are money-plant accounts distinguished from other member savings accounts? | Not distinguishable in member snapshot | `MoneyPlantSpecification.java:50` | M |
| 4.6 | Where does the project installment configuration live (`projectInstallmentConfigExists`)? | Check skipped — no source | `InstallmentConfigurationSpecification.java:17` | M |
| 4.7 | Where do approver roles come from (AAM/AM vs ABM/BM checks)? We only get `loanApproverId` | Role checks skipped | `SpecialSavingsLienSpecification.java:60` | M |
| 4.8 | Where is the auto-debit / payment-channel ↔ branch/project mapping? | Mapping checks skipped | `DigitalDisbursementSpecification.java:31` | M |
| 4.9 | What are the DCS branch-recommender checks, and do they ever apply to OTC? | Skipped (doc says OTC skips them) | `ProjectSpecificRulesSpecification.java:47` | L |

## 5. Business-rule semantics to confirm

| # | Question for legacy | Our reading | Code | Prio |
|---|---|---|---|---|
| 5.1 | Age at loan end: doc says invalid when EXACTLY 70y0m — surely 70y1m+ is also invalid? | Treated as ≥ 70 | `AgeLimitSpecification.java:48` | M |
| 5.2 | What is the golden-member valid age range? Doc's rule is undefined; its 70–80 branch is dead code | Both skipped | `AgeLimitSpecification.java:38` | M |
| 5.3 | Missing VO: doc pseudocode rejects unconditionally, which would reject every proposal — when should it actually fail? | Fails only when VO is genuinely required | `BranchProjectVoConsistencySpecification.java:52` | L |
| 5.4 | Doc's `officeId` — is branch the office, or is there a separate office entity? | Branch used as office | `LoanProductPolicySpecification.java:43` | L |
| 5.5 | Is the second failed-event factory `sourceDataError(traceId, errorCode, errors)` ever produced on the create path? | Omitted | `LoanProposalFailedEvent.java:36` | L |

---

## Answer log

Record answers here as they come in; move the item's row out once code and comment are updated.

| # | Answered by / date | Answer | Code updated? |
|---|---|---|---|
| | | | |
