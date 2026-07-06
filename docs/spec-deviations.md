# Deviation Report — Validation Specifications vs. DDD-EARS Doc

Covers all 21 specification categories chained in `LoanProposal.validate()` (19 written in
this effort plus the earlier `MemberEligibilitySpecification` and `LoanAmountSpecification`),
documenting every place the code deviates from `LoanProposal-Command-DDD-EARS.md`, why, and
what decision was taken. Every deviation is also marked in code with a `ponytail:` or
explanatory comment.

## The root problem behind almost every deviation

Two facts drive most of these decisions:

1. **The doc's pseudocode assumes happy-path data.** It writes `ctx.member().branchId()`
   without a null check. But in our chain, the library's `Specification.and()` runs *every*
   spec and merges the error maps — there is no short-circuit between specs. If `member` is
   null, `MemberEligibilitySpecification` correctly reports `MEMBER_NOT_FOUND`, but spec #2
   would then NPE on `member.getBranchId()` and turn a clean validation error into a 500.
   So **every spec null-guards fields the doc assumes present**. This is a systematic
   deviation, applied everywhere, deliberately.

2. **The doc names helper functions it never defines** (`isRecognisedFrequency`,
   `isLienProduct`, `isProgotiOrAdpProject`, `activeMappingExistsBetweenBranchAndProject`, …).
   The source EARS file it cites (`LoanProposalOTC-EARS-review-2-resolved.md`) is **not in
   this repo**, so the real business definitions are unverifiable. For each I either invented
   a reasonable implementation (marked as invented) or skipped the rule (marked as skipped).
   Decision rule: *invent when the data exists and a wrong guess fails loudly; skip when the
   data doesn't exist at all* — a check running against data we don't have can only produce
   false rejections.

---

## Batch 1

### 1. BranchProjectVoConsistencySpecification (DDD-REQ-010)

**Deviation A — VO not-found check scoped to group projects.**
- *Doc says:* unconditionally, `IF villageOrganisation is null or code is null THEN error
  VO_CODE_NOT_FOUND`.
- *Code does:* that check fires only when the project policy's `associationType` is `GROUP`.
- *Why:* VO source data is **conditionally fetched** — `LoanProposalSourceDataProvider` only
  registers the `villageOrganisation` lookup when the command carries a voCode. Followed
  literally, the doc would reject *every individual-lending proposal* (no VO by design) with
  `VO_CODE_NOT_FOUND`. That's an internal contradiction in the doc: its own Gate section
  makes VO optional, then its pseudocode requires it.
- *Decision:* the doc's own preceding rule ("Group-based project but no VO code") reveals the
  intent — VO is mandatory *for group lending*. The check is scoped to that. Mismatch
  (`MEMBER_VO_MISMATCH`) still fires for any project type when both codes are present.
- *Risk:* if the business actually requires VO on some non-GROUP association types, those
  slip through. Verify against the legacy validator.

**Deviation B — branch↔project active-mapping helper invented.**
- *Doc says:* `activeMappingExistsBetweenBranchAndProject(branch, project)` — never defined.
- *Code does:* `project.getBranchMappings().contains(branch.getBranchId())`, and **passes
  when either side or the mapping list is null**.
- *Why:* `branchMappings` is the only mapping-shaped data the `Project` DTO carries.
  Lenient-when-null matches the existing convention in this codebase
  (`LoanProduct.isMappedWith` already treats null mappings as "no restriction").
- *Risk:* "active" mapping — we have no active/inactive flag on the mapping, only membership
  in the list. If the snapshot includes inactive mappings, this under-rejects.

### 2. LoanProductPolicySpecification (DDD-REQ-011)

**Deviation A — `officeId` replaced with `branchId`.**
- *Doc says:* `isMappedWith(project, ctx.aggregate().getOfficeId(), ...)`.
- *Why/how:* the aggregate has **no `officeId` field** — the doc's own DDD-REQ-002 schema
  never defines one either; in BRAC's structure the branch *is* the office for lending
  purposes. Passing `aggregate.getBranchId()` was the only coherent reading.
- *Risk:* near zero — but if office ≠ branch at some org levels, the mapping check is
  checking the wrong id.

**Deviation B — member classification read from the aggregate, not `ctx.member()`.**
- *Doc says:* `ctx.member().classificationId()`.
- *Code does:* `aggregate.getMemberClassificationId()`.
- *Why:* null-safety (member may be null, per the root problem above) and the aggregate
  already carries the classification copied from the command. Same value on the happy path,
  no NPE on the sad path.

**Deviation C — error-key restructuring.**
- *Doc says:* three different failures (`NOT_FOUND`, `EXPIRED`, `MAPPING_INVALID`) all stored
  under the **same map key** `"loanProduct"`, and details/policy not-found + expired under
  their shared keys.
- *Code does:* not-found/expired merged into `if / else if` per entity (they're mutually
  exclusive anyway — a null product can't be expired), and the member-category error moved
  to its own key `"loanProductMemberCategory"`.
- *Why:* the error container is a `HashMap<String, LocalizedMessage>` — the doc's version
  silently **overwrites earlier errors with later ones on the same key**, so a product that
  is both expired and mis-mapped would report only one problem. Restructuring preserves
  every distinct failure. This is a doc bug fixed rather than replicated.

### 3. RepaymentFrequencyModeOfPaymentSpecification (DDD-REQ-012)

**Deviation A — `isRecognisedFrequency` invented as range 1–10.**
- *Doc says:* `IF freq > 10 OR NOT isRecognisedFrequency(freq)` — the helper undefined.
- *Code does:* `frequencyId == null || < 1 || > 10`.
- *Why:* the doc's only concrete constraint is ">10 is invalid"; there is no frequency lookup
  table in source data to check against. A 1–10 range is the minimal faithful reading.
- *Risk:* if valid frequency ids are a sparse set (say 1,2,4,12 style), ids like 7 pass
  wrongly. Needs the legacy frequency table.

**Deviation B — subtype validity = enum non-null.**
- *Doc says:* `NOT isValidModeOfPaymentSubType(mop.subType())`.
- *Code does:* flags only `subType() == null`.
- *Why/decision:* `subType` is typed as the `ModeOfPaymentSubType` **enum** — the type system
  already makes an unrecognized value unrepresentable; an invalid inbound string surfaces as
  null after mapping. The enum's 11 constants *are* the valid set, so a non-null value is by
  construction valid. No lookup needed.

**Deviation C — Rocket wallet error keyed `"rocketWallet"`, not `"modeOfPayment"`.**
- *Why:* same HashMap-collision reason as 2C — an invalid subtype *and* a missing wallet
  would otherwise collapse into one error.

### 4. LoanExposureLimitSpecification (DDD-REQ-014)

**Deviation — the limit is read from `LoanProductPolicy`, the enforce-flag from
`ProjectPolicy`.**
- *Doc says:* both `enforcesLoanExposureLimit()` **and** `officeAndProjectExposureLimit()`
  live on `ctx.projectPolicy()`.
- *Code does:* flag from `ProjectPolicy.isEnforcesLoanExposureLimit()`, limit amount from
  `LoanProductPolicy.getOfficeAndProjectExposureLimit()`.
- *Why:* our DTOs simply don't match the doc — `ProjectPolicy` has the flag but no limit
  field; `LoanProductPolicy` has `officeAndProjectExposureLimit`. Someone (the earlier DTO
  author) already split this data. Rather than restructure DTOs (and the RabbitMQ snapshot
  contracts behind them) for one check, the two were bridged.
- *Decision on absent data:* if the limit is null while the flag is on → **pass**, not fail.
  Failing would block all proposals on a data-sync gap; the doc gives no guidance, so
  availability won over strictness.
- *Risk:* medium. This is the one deviation where the doc and DTOs genuinely disagree about
  data ownership. Verify which policy document actually carries the limit in production data.

### 5. CoBorrowerSpecification (DDD-REQ-015)

**Deviation — null-guard on `loanProduct` only.** The doc's one rule is implemented as
written; if the product is missing the spec stays silent because
`LoanProductPolicySpecification` already reports `LOAN_PRODUCT_NOT_FOUND` — repeating it
here would be noise. No business logic deviation.

---

## Batch 2

### 6. InsurancePolicyTypeSecondInsurerSpecification (DDD-REQ-016)

The doc lists 16 sub-rules; 12 implemented, 3 skipped, 1 modified.

**Deviation A — SINGLE/DOUBLE as constants 1L/2L.** Not really invented — the doc's own
field table states "SINGLE=1, DOUBLE=2", and its rejection message says "Allowed Policy type
is: 1 and 2." Just noting where the magic numbers come from.

**Deviation B — marital-status and gender-relationship combo checks SKIPPED.**
- *Doc says:* `isValidRelationshipForMaritalStatus(insurer, member)` and
  `isValidGenderRelationshipCombo(insurer, member)`.
- *Why skipped:* the `Member` source-data DTO has **no gender and no marital status**, and
  both helper definitions are absent. There is literally no data to evaluate against; any
  implementation would be pure fiction that rejects real customers.
- *Upgrade path:* add `genderId`/`maritalStatusId` to the Member snapshot + a relationship
  rules table, then implement. Cleanest example of the skip-vs-invent rule: no data → skip.

**Deviation C — spouse exemption on engagement checks DROPPED (checks run unconditionally).**
- *Doc says:* only a second insurer who is *not* the spouse is checked for
  other-loan/other-insurance engagement.
- *Why:* determining "is spouse" requires a `relationshipId → SPOUSE` mapping we don't have.
  Two options: skip the engagement checks entirely, or run them for everyone. Running them
  is currently **inert and safe**: `LoanProposalDataMapper.mapSecondInsurer` hardcodes
  `isEngagedWithOtherLoans`/`isEngagedWithOtherInsurance` to `false` (they'd come from an
  external verification not yet integrated), so the checks cannot false-reject today, but
  the wiring is in place for when real flags arrive.
- *Risk:* the day real engagement flags land, spouses will start being rejected — the
  ponytail comment at that exact spot says to add the spouse exemption then.

**Deviation D — coverage check guarded for null amount.** `coversAmount(null)` returns
`false`, which would emit a misleading `LOAN_AMOUNT_NOT_ALLOWED_FOR_INSURANCE` when the real
problem is a missing amount (already reported by `LoanAmountSpecification` as
`LOAN_AMOUNT_INVALID`). Guarded to avoid the duplicate/misleading error.

### 7. NomineeSpecification (DDD-REQ-017)

**Deviation — `DCS_MAX_NOMINEES = 3`, a number the doc never states.**
- *Doc says:* `size() > DCS_MAX_NOMINEES` — the constant is referenced, never defined,
  anywhere in the 3000-line doc.
- *How 3 was chosen:* the doc's FireInsurance section says "CSI nominees should not exceed
  the limit of three" and "Fire Insurance nominees should not exceed the limit of three" —
  the only nominee ceiling the document ever names. Borrowed it.
- *Risk:* the DCS system limit could genuinely differ (could be 2, could be 5).
  One-character fix once someone reads the DCS config. Invented and flagged as such in code.

### 8. SpecialSavingsLienSpecification (DDD-REQ-018)

**Deviation A — approver-role checks SKIPPED (2 of 6 sub-rules).**
- *Doc says:* lien on individual-lending project → approver must be AAM or AM;
  group-lending → ABM or BM.
- *Why:* the aggregate carries only `loanApproverId` (a `Long`). AAM/AM/ABM/BM are *role
  designations* — resolving an id to a role requires an employee/designation source-data
  entity that **doesn't exist among the 12 registered lookups**. No data → skip.
- *Upgrade path:* add an approver/employee snapshot entity, then implement both this and the
  REQ-019 approver rules in one go.

**Deviation B — `isLienProduct` / `isLienOrMoneyPlantProduct` invented as string matches.**
- *Code does:* `loanProductType` equalsIgnoreCase `"LIEN"` / `"MONEY_PLANT"`.
- *Why:* `loanProductType` is a free string on the `LoanProduct` DTO; no enum, no doc
  definition. These are the obvious names but there is zero confirmation the legacy system
  uses these exact strings (could be `"PROTTASHA"`, could be numeric type codes).
- *Risk:* **high-consequence if wrong** — if the string never matches, the lien limit check
  never fires and the "special savings not applicable" check fires for actual lien products.
  Top verification priority of the batch.

**Deviation C — `policyMaximum()` mapped to `maxProttashaParallelAmount`.** The doc's helper
is undefined; `ProjectPolicy.getMaxProttashaParallelAmount()` is the only field shaped like
it and its name matches the rejection message ("maximum limit together of {amount} taka").
Confidence high.

**Deviation D — loop errors share one map key.** Multiple bad accounts overwrite each other
under `"specialSavings"` — kept as the doc has it, because fixing it means keyed-per-account
errors and the first failing account is enough to bounce the proposal. Noted, not fixed.

### 9. ProjectSpecificRulesSpecification (DDD-REQ-019)

The doc bundles 10 sub-rules; 5 implemented, 5 skipped.

**Deviation A — DCS branch-recommender rules SKIPPED (2 sub-rules) — sanctioned by the doc
itself.** The pseudocode's own comment says "OTC proposals skip DCS recommender checks", and
this service is the OTC channel. Not really a deviation, but recorded since 2 of the 10
listed sub-rules intentionally have no code.

**Deviation B — Goti approver-role rules SKIPPED (3 sub-rules: first-Goti AM/RM/AAM/ARM,
top-up ≤30 days SM-only, top-up >30 days DM-only).** Same reason as 8A — no approver-role
data exists. Note the member-side inputs *are* already there (`isFirstGotiLoan`,
`isGotiTopUp`, `daysSincePreviousGotiLoan` all sit on the Member DTO waiting), so when a
role lookup lands, these three rules are mechanical to add.

**Deviation C — `isProgotiOrAdpProject` invented as substring match.**
- *Code does:* project `code` or `name` contains `"PROGOTI"` or `"ADP"` (case-insensitive).
- *Why:* the doc never defines it and `Project` has no project-type field. Substring on
  code/name is the laziest thing that can work with current data.
- *Risk:* same class as 8B — legacy likely identifies these by numeric `projectId` or a code
  convention. A project named "Adarsha ADP Phase 2" would false-match today. Verify and
  replace with an id set or a proper type field on the snapshot.

**Deviation D — Goti/Shondhi/General product detection via `loanProductType` strings.** Same
invented-string situation as 8B, same verification need. Also note `SHONDHI` and the doc's
"agent member" qualifier: the doc restricts the Shondhi rule to *agent* members, but Member
has no agent flag — the rule currently applies to all members, which over-rejects in the
(presumably rare) non-agent-Shondhi case.

---

## Batch 3

A shared package-private helper `ProductTypes.is(product, type)` was introduced for this
batch: seven specs now discriminate on `LoanProduct.loanProductType`, a free-text field, and
none of the doc's product-type helpers (`isLienProduct`, `isMigrationLoan`,
`isMoneyPlantLoan`, `isRemittanceLoan`, `isGeneralLoan`, …) are defined anywhere available.
Every type string below (`LIEN`, `MONEY_PLANT`, `MIGRATION`, `REMITTANCE`, `GENERAL`,
`GOTI`, `SHONDHI`) is **invented** and must be verified against legacy product data — if
one string never matches, its whole rule group silently never fires.

### 10. ParallelCoExistingLoanSpecification (DDD-REQ-020)

**Deviation A — UPG detection via `LoanProposalType.UPG`, not the project.**
- *Doc says:* `isUPGProject(ctx.project())`.
- *Code does:* `aggregate.getLoanProposalType() == LoanProposalType.UPG`.
- *Why:* `Project` has no type field, so a project-side check would need another invented
  substring heuristic. The aggregate's `LoanProposalType` enum *already has a `UPG`
  constant* — typed data beats a string guess, even though it moves the discriminator from
  the project to the proposal.
- *Risk:* low — but if a UPG project can carry a non-UPG proposal type, the two gates differ.

**Deviation B — Remittance/General detection via invented product-type strings** (see the
`ProductTypes` note above).

**Deviation C — member null-guard:** the spec returns silently when member is null
(`MemberEligibilitySpecification` already reports `MEMBER_NOT_FOUND`). All member flags the
doc needs (`hasExistingLoan`, `hasActiveGeneralLoan`, `hasActiveRemittanceLoan`,
`hasPriorTUPLoan`, `hasActiveNonClosedLoanOfSameProduct`) exist on the Member snapshot, so
the rules themselves are implemented as written.

### 11. InstallmentConfigurationSpecification (DDD-REQ-021)

**Deviation A — project installment-config check SKIPPED (1 of 3 sub-rules).**
- *Doc says:* `projectInstallmentConfigExists(project, loanProduct)`.
- *Why:* there is no project installment-configuration entity among the source-data
  lookups — nothing to check against. No data → skip.

**Deviation B — details-missing error re-keyed to `"installmentDetails"`.**
- *Why:* `LoanProductPolicySpecification` already emits under `"loanProductDetails"`, and
  the chain merges all spec maps into one — the doc's key would collide **across specs**
  and one of the two messages would be silently lost. Same HashMap discipline as batch 1,
  now applied cross-spec.

**Deviation C — spouse scoping dropped on the loan-accounts check.** Same situation as
REQ-016's spouse exemption: no `relationshipId → SPOUSE` mapping exists, so the
`hasOtherLoanAccounts` check runs for *any* second insurer. Inert today — the mapper
hardcodes the flag to `false`.

### 12. ModeOfPaymentRocketWalletSpecification (DDD-REQ-022)

**No business deviation.** Both rules (disbursement wallet match, collection wallet match)
implemented as written using `Objects.equals` — which also handles the doc's unstated case
where the proposal's wallet number is null (null ≠ member's number → mismatch, matching the
doc's `!=` literally). Member null-guarded as everywhere.

### 13. DigitalDisbursementSpecification (DDD-REQ-023)

The doc bundles 9 sub-rules; 4 implemented, 5 skipped or adapted.

**Deviation A — "central disbursement mode" invented as `digitalDisbursementModeId != null`.**
- *Doc says:* `isCentralDisbursementMode(mop)` — undefined.
- *Why:* `digitalDisbursementModeId` on `OtcModeOfPayment` is the only field that smells
  like central/digital disbursement routing. Marked invented.
- *Risk:* medium — if central disbursement is a *specific* mode id rather than any non-null
  id, the money-plant exclusion over-fires.

**Deviation B — auto-debit and payment-channel branch/project mapping checks SKIPPED
(2 sub-rules).** No mapping source data exists for either. No data → skip.

**Deviation C — the whole Amar Hishab premium-collection block SKIPPED (3 sub-rules).**
- *Why:* the trigger `isAmarHishabhPremiumCollection(agg)` is undefined and the only
  candidate field (`premiumModeOfPaymentId`) has no documented id→meaning mapping — we
  cannot even tell *when* the rules should fire. Notably the Member snapshot already
  carries `hasAmarHishabAccount` and `amarHishabBalance`, so the *data* side is ready; only
  the trigger id is missing. Skipped rather than guessing a magic number.

**Deviation D — bank-ownership check gated on `bankAccountNumber != null`.** The doc's
`hasBankAccount(mop)` is undefined; a present account number is the natural reading. The
doc's order (check ownership, then check bank-null) was also flipped to the sane
null-first order.

### 14. MigrationCountrySpecification (DDD-REQ-024)

**Deviation — `isMigrationLoan` via invented `MIGRATION` product-type string** (see
`ProductTypes` note). Everything else is as written: `Country.isConfiguredForMigration()`
existed on the DTO, matching the doc exactly.

### 15. FireInsuranceSpecification (DDD-REQ-025)

The doc bundles 15 sub-rules; 8 implemented, 7 skipped. The biggest gap in the batch.

**Deviation A — no fire-insurance product source data exists at all.**
- *Doc says:* `ctx.fireInsuranceProduct()` — a lookup fetched by `fireInsuranceProductId`.
- *Reality:* the 12 registered source-data entities include `insuranceProduct` (fetched by
  `insuranceProductId`, the micro-insurance product) but **nothing keyed by
  `fireInsuranceProductId`**. Four sub-rules die with it: product availability/premium
  presence, premium recalculation, branch mapping, project mapping.
- *Decision:* skipped rather than misusing the micro-insurance product lookup — the
  `InsuranceProduct` DTO's `isMappedWithBranch`/`isMappedWithProject` helpers look built
  for this, but pointing them at the wrong product id would validate the wrong thing.
- *Upgrade path:* register a `fireInsuranceProduct` lookup in the provider + context, then
  the four rules are mechanical.

**Deviation B — premium/duration recalculation SKIPPED.** `calculateFireInsurancePremium`
and `calculateFireInsuranceDuration` are undefined formulas. Guessing a premium formula on
a money path is exactly the kind of inventing not worth doing.

**Deviation C — trading-sector check SKIPPED.** `isTradingSector(sectorId)` — no sector
entity, no documented trading-sector id.

**Deviation D — DCS business-details rule SKIPPED** — doc-sanctioned, this is the OTC
channel.

**Deviation E — phone rule tightened to digits.** Doc pseudocode checks only
`length() != 11`, but its own rejection message says "exactly 11 digits" — implemented as
`matches("\\d{11}")`, following the message over the pseudocode.

**Deviation F — fire-nominee limit re-keyed to `"fireNominees"`.** The doc puts both the
CSI-limit and fire-limit errors under `"nominees"` — same-key overwrite again.

**What did survive:** product-id presence, details presence, phone, insured-amount ==
proposed-amount, CSI ≤ 3, fire nominees ≤ 3, fire-nominee-without-fire-insurance, and the
OTC policy-type-not-applicable rule (implemented per the sub-rule table, which adds the
"no micro-insurance" condition the pseudocode forgot).

### 16. AgeLimitSpecification (DDD-REQ-026)

**Deviation A — the doc's individual-project 70–80/80+ branch is dead code, not
implemented.** It sits in an `ELSE IF` behind `IF memberAge >= 70`, so no age can ever
reach it (70–80 and 80+ are both ≥ 70). Rather than copy unreachable code, only the ≥ 70
rule is implemented. If the *intent* was a lower threshold for individual-lending projects,
the doc doesn't say what it is.

**Deviation B — "exactly 70 at loan end" implemented as "turns 70 by loan end".**
- *Doc says:* `ageAtLoanEnd.totalMonths() == 70 * 12` — equality, meaning a member who is
  70 years **1 month** at loan end would pass while 70 years 0 months fails.
- *Code does:* `ageAtEnd >= 70 && ageAtApplication < 70` (the second clause avoids
  double-reporting members already rejected by the ≥ 70-at-application rule).
- *Why:* the equality reads as a pseudocode artifact, not a business rule; an
  insurance-age ceiling that only catches one exact month is nonsensical.
- *Risk:* if the business genuinely wanted the exact-boundary semantics, this over-rejects
  the 70y1m+ band. Flagged in code.

**Deviation C — golden-member valid-range rule SKIPPED.** `isAgeInvalidForGoldenMember` is
undefined ("age in 65+ or 80+ group making it invalid" is not a specification). The golden
exemption *on the ≥ 70 rule* (lien/money-plant products) is implemented.

**Deviation D — `calculateLoanEndDate` invented as
`applicationDate.plusMonths(proposalDurationInMonths)`.** Obvious, but the doc never
defines it; if duration is null the end date falls back to the application date, which
disables the loan-end age rules rather than NPEing.

**Deviation E — second-insurer individual-project 70–80 sub-rule not implemented** — same
dead-code shape as Deviation A (insurer ≥ 70 is already rejected by the preceding rule).

### 17. MoneyPlantSpecification (DDD-REQ-027)

**Deviation A — age ineligibility implemented from the sub-rule table, not the pseudocode.**
The pseudocode delegates to undefined `isAgeIneligibleForMoneyPlant`; the table spells it
out: under 18, 80+, or 70–80 non-golden. That table version is implemented directly.

**Deviation B — money-plant account distinction collapsed into ownership (1 check instead
of 2).**
- *Doc says:* `hasMoneyPlantAccount(acc)` (→ MONEY_PLANT_ACCOUNT_INVALID) *and*
  `ownsAccount(acc)` (→ SAVINGS_ACCOUNT_NOT_BELONG_TO_MEMBER) as separate checks.
- *Reality:* the Member snapshot has one `specialSavingsAccounts` list with no product-type
  breakdown — both checks would run the same `contains()`. One ownership check is done,
  emitting `MONEY_PLANT_ACCOUNT_INVALID`.

**Deviation C — loan-portion amount match SKIPPED.** `moneyPlantLoanPortion(account)` needs
per-account balance data the snapshot doesn't carry. No data → skip.

### 18. SchemeSectorMappingSpecification (DDD-REQ-028)

**Deviation A — "scheme list empty" mapped to scheme-lookup-absent.**
- *Doc says:* `schemeExistsForLoanProductAndSector(loanProduct, sectorId)` — undefined,
  and no scheme-list source data exists (we fetch exactly one scheme by `schemeId`).
- *Code does:* `ctx.scheme() == null` → `SCHEME_LIST_EMPTY`. Note this is near-unreachable
  in the create flow — scheme is a *required* lookup, so a missing scheme normally fails
  earlier in `provide()` as a source-data error. Kept as a guard.

**Deviation B — sector checked against the scheme only.** The doc wants "sector mapped to
loan product **or** scheme"; `LoanProduct` carries no sector mappings, so only
`Scheme.isMappedToSector()` is checkable. If sector-to-product mappings exist in legacy,
this under-rejects.

### 19. BankModeOfPaymentSpecification (DDD-REQ-029)

**Deviation A — `isBankMode` invented as "carries bank coordinates"
(`bankId != null || bankAccountNumber != null`).** The doc gates the whole spec on this
undefined helper. Defining it by subtype would be circular (the doc checks subtype-null
*after* the gate). Field presence is the only non-circular reading.

**Deviation B — recognised bank subtype = any non-wallet enum constant.** Same type-system
substitution as REQ-012: the enum makes unknown values unrepresentable, so "unsupported"
can only mean a *wallet* subtype (ROCKET/BKASH) arriving through the bank path.

**Deviation C — "same bank as branch" = `bank.bankId == branch.bankId`.**
`isSameBankAsBranch` is undefined; `Branch.bankId` exists on the snapshot and is the
natural anchor. (`Bank.branchBankId` also exists and may be the intended comparand — if
so, it's a one-line change.) If either id is null the BEFTN/fund-transfer rules are
skipped rather than guessed.

**Deviation D — document-number validity = non-blank.** `isValidDocumentNumber` undefined;
no format spec exists for cheque/online/cash-deposit references. The `{document label}` /
`{date label}` message args use the subtype enum name — the doc's label lookup tables
don't exist.

---

## Summary table

| # | Spec | Deviation | Type | Risk |
|---|------|-----------|------|------|
| 1 | BranchProjectVo | VO check scoped to GROUP projects | Doc self-contradiction fixed | Low |
| 1 | BranchProjectVo | Branch↔project mapping via `branchMappings`, lenient on null | Invented helper | Low-med |
| 2 | LoanProductPolicy | `officeId` → `branchId` | Missing field bridged | Low |
| 2 | LoanProductPolicy | Error keys de-collided | Doc HashMap bug fixed | None |
| 3 | RepaymentFrequency | Frequency valid = 1–10 | Invented helper | Med |
| 3 | RepaymentFrequency | Subtype validity = enum non-null | Type-system substitution | Low |
| 4 | LoanExposureLimit | Limit from LoanProductPolicy, flag from ProjectPolicy; null limit = pass | DTO/doc mismatch bridged | Med |
| 6 | InsuranceSecondInsurer | Marital/gender-relationship combos skipped | No data | Med (missing checks) |
| 6 | InsuranceSecondInsurer | Spouse exemption dropped, checks inert today | No data, safe default | Low now, real later |
| 7 | Nominee | Limit = 3, borrowed from CSI/fire limit | Invented constant | Med |
| 8 | SpecialSavingsLien | Approver roles skipped | No data | Med (missing checks) |
| 8 | SpecialSavingsLien | LIEN/MONEY_PLANT string matching | Invented strings | **High** |
| 9 | ProjectSpecificRules | DCS recommender skipped | Doc-sanctioned (OTC) | None |
| 9 | ProjectSpecificRules | Goti approver roles skipped | No data | Med (missing checks) |
| 9 | ProjectSpecificRules | PROGOTI/ADP substring heuristic; GOTI/SHONDHI/GENERAL strings; agent flag ignored | Invented | **High** |
| 10 | ParallelCoExistingLoan | UPG via LoanProposalType enum, not project | Better data substituted | Low |
| 10 | ParallelCoExistingLoan | REMITTANCE/GENERAL type strings | Invented strings | **High** |
| 11 | InstallmentConfiguration | Project installment-config check skipped | No data | Med (missing check) |
| 11 | InstallmentConfiguration | Details error re-keyed (cross-spec collision) | HashMap discipline | None |
| 11 | InstallmentConfiguration | Spouse scoping dropped, check inert today | No data, safe default | Low now, real later |
| 12 | ModeOfPaymentRocketWallet | None (Objects.equals null handling) | Faithful | None |
| 13 | DigitalDisbursement | Central mode = digitalDisbursementModeId != null | Invented | Med |
| 13 | DigitalDisbursement | Auto-debit/payment-channel mapping skipped | No data | Med (missing checks) |
| 13 | DigitalDisbursement | Amar Hishab premium block skipped (trigger id unknown) | No data | Med (missing checks) |
| 14 | MigrationCountry | MIGRATION type string | Invented string | **High** |
| 15 | FireInsurance | No fireInsuranceProduct source-data entity — 4 sub-rules skipped | Missing lookup | **High** (missing checks) |
| 15 | FireInsurance | Premium/duration formulas skipped | Undefined formulas | Med (missing checks) |
| 15 | FireInsurance | Trading-sector check skipped | No sector data | Med (missing check) |
| 15 | FireInsurance | Phone rule tightened to \d{11} | Message over pseudocode | None |
| 16 | AgeLimit | Dead individual-project 70–80/80+ branch not copied | Doc dead code | Low |
| 16 | AgeLimit | "Exactly 70 at loan end" → "turns 70 by loan end" | Doc bug fixed | Low-med |
| 16 | AgeLimit | Golden valid-range rule skipped | Undefined | Med (missing check) |
| 16 | AgeLimit | Loan end = applicationDate + durationMonths | Invented (obvious) | Low |
| 17 | MoneyPlant | Age rule from sub-rule table, not undefined helper | Table over pseudocode | Low |
| 17 | MoneyPlant | Money-plant account distinction collapsed to ownership | Snapshot lacks breakdown | Med |
| 17 | MoneyPlant | Loan-portion amount match skipped | No data | Med (missing check) |
| 18 | SchemeSectorMapping | Scheme-list-empty = scheme lookup absent | Invented mapping | Low |
| 18 | SchemeSectorMapping | Sector checked against scheme only | Product has no sector mappings | Med |
| 19 | BankModeOfPayment | isBankMode = has bankId/account number | Invented gate | Med |
| 19 | BankModeOfPayment | Same-bank = bank.bankId == branch.bankId | Invented anchor | Med |
| 19 | BankModeOfPayment | Document number = non-blank; labels = enum names | Invented | Low |

## Verification priority

1. **Product-type strings** (8B, 9D, 10B, 14, and every `ProductTypes.is` call site) and
   project identification (9C) against legacy data — one wrong string silently disables a
   whole rule group. `ProductTypes.java` is the single place to fix the strings.
2. **The missing `fireInsuranceProduct` source-data lookup** (15A) — registering it
   unblocks 4 skipped fire-insurance sub-rules.
3. Exposure-limit field ownership (4); the central-disbursement marker (13A); the
   same-bank anchor (19C); the Amar Hishab trigger id (13C).
4. The DCS nominee limit (7).
5. Plan the approver-role source-data entity — unblocks 5 skipped sub-rules across
   REQ-018/019 in one change.
6. Confirm the age-boundary semantics with the business: dead 70–80 branch intent (16A) and
   the exactly-70-at-loan-end reading (16B).
