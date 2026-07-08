# Create Flow — Live Run Report (2026-07-08)

First end-to-end execution of the create API against real infrastructure (Spring Boot app +
MongoDB 7 and RabbitMQ 3.13 in Docker). The run surfaced **two aggregate-initialization bugs**
that no unit test had caught, plus **one architectural finding about the bits-ddd library**
(non-atomic persist/publish). Both bugs are fixed; the library finding needs a decision.

---

## 1. Setup

| Piece | Value |
|---|---|
| App | `./gradlew bootRun`, port 8081 |
| Mongo | Docker `mongo:7`, db `loan_proposal_command` |
| RabbitMQ | Docker `rabbitmq:3.13`, port 5672 |
| Endpoint | `POST /api/loan-proposals` |
| Seed data | member 1001, branch 77 (`B001`), loan product 10, details 100, policy 200, scheme 5, project 1, project policy 1, VO 55 |

Two seeding traps worth remembering (cost most of the setup time):

1. **The business id IS the Mongo `_id`.** Snapshot documents annotate the business id
   (`memberId`, `branchId`, …) with `@Id`, so lookups are `findById` on `_id`. Documents
   seeded with an auto ObjectId and the business id as a plain field are invisible to the app.
2. **Every query is tenant-filtered.** `GenericMongoRepositoryImpl` extends
   `AbstractTenantAwareRepository`, which appends `tenantId` (default `"1"`) to every query.
   Seed documents without `tenantId: "1"` are silently filtered out. The resulting
   422 `SOURCE_DATA_ERROR` does not say which lookup failed or why — this had to be
   found by decompiling the library jar.

## 2. Timeline of requests

Each row is one `POST /api/loan-proposals` with the same body (id `test-proposal-0001`,
member 1001, branch 77, 50 000 @ 24%, 24 installments / 12 months).

| # | DB state | Response | Sequence consumed? |
|---|---|---|---|
| 1 | empty DB | 422 `SOURCE_DATA_ERROR` | no |
| 2 | seeded, wrong `_id`s | 422 `SOURCE_DATA_ERROR` | no |
| 3 | `_id` fixed, no `tenantId` | 422 `SOURCE_DATA_ERROR` | no |
| 4 | tenantId fixed, branch has **no business date** | 400 `BUSINESS_DATE_NOT_AVAILABLE` ✅ new guard works | no (guard sits before the counter) |
| 5 | business date set, member `isScreened: true` | 400 `MEMBER_SCREENED` | **yes → seq 1 burned** |
| 6 | member unscreened | **500** `getVersion()` NPE — **Bug A** | yes → seq 2 burned |
| 7 | retry same id | 400 `ALREADY_EXISTS` — **document from #6 was persisted!** | no (duplicate gate is first) |
| 8 | doc cleared, Bug A fixed | **500** `getStatus()` NPE — **Bug B** | yes → seq 3 burned |
| 9 | doc cleared, Bug B fixed | **202 ACCEPTED** ✅ | yes → seq 4 → `202607-00004` |

The final proposal number `202607-00004` is itself evidence of the documented trade-off
working as designed: rows 5, 6, 8 each burned a number ("a validation/persist failure after
the counter increments leaves gaps; gaps are acceptable").

## 3. Bug A — `AggregateRoot.version` never initialized

**Symptom (row 6):**

```
500 DOMAIN_EXCEPTION
Cannot invoke "java.lang.Long.longValue()" because the return value of
"com.bits.ddd.aggregate.AggregateRoot.getVersion()" is null
```

**Root cause.** The bits-ddd base class declares the persistence bookkeeping fields but
never gives them values, and provides no setters:

```java
public abstract class AggregateRoot<ID> implements BaseEntity<ID> {
    protected Long version;          // no default, no setter
    protected DomainStatus status;   // no default, no setter
    protected String tracerId;
    ...
    public AggregateRoot() { this.events = new ArrayList<>(); }  // only initializes events
}
```

`LoanProposal.create()` populated ~100 of its own fields but none of the inherited ones.
The library's persistence path then calls `getVersion().longValue()` → NPE. Because the
fields are `protected` with no setter, the library's implicit contract is *"the subclass
must assign these in its factory"* — a contract that is nowhere enforced or documented,
and that unit tests never exercise because they don't run the persistence service.

**Fix** (`LoanProposal.create()`):

```java
// library AggregateRoot fields: persistence NPEs on a null version
proposal.version = 0L;
proposal.tracerId = creationData.traceId();
```

## 4. Bug B — `domainStatus` shadowed the library's `status`

**Symptom (row 8, after Bug A was fixed):**

```
500 DOMAIN_EXCEPTION
Cannot invoke "com.bits.ddd.shared.domain.value.DomainStatus.toString()" because the
return value of "com.bits.ddd.aggregate.AggregateRoot.getStatus()" is null
```

**Root cause.** `AggregateRoot` already has a `DomainStatus status` field, but the aggregate
had declared **its own** `private DomainStatus domainStatus` and set that one
(`CREATED` on create, `UPDATED` on update). Two fields for the same concept: the domain
code read/wrote the shadow copy, the library read the inherited one — which stayed null
until the persistence/event path called `getStatus().toString()`.

**Fix.** Deleted the duplicate field; one field, one truth:

- `proposal.domainStatus = DomainStatus.CREATED` → `proposal.status = DomainStatus.CREATED`
- `this.domainStatus = DomainStatus.UPDATED` → `this.status = DomainStatus.UPDATED`
- `getDomainStatus()` callers (`LoanProposalEventMapper`, update test) → inherited `getStatus()`
- Test's `setDomainStatus(CREATED)` line dropped (no setter exists on the library field;
  `update()` assigns it anyway, which is what the test asserts).

## 5. Library finding — persist and event publishing are not atomic

Row 7 is the important one: after the 500 in row 6, **the proposal document was already in
Mongo** — the retry hit the duplicate-id gate. So the library's `DomainPersistenceService`
flow is effectively:

```
insert document → (version/event bookkeeping → NPE here) → publish events
```

Consequences of a failure in the middle:

- The client gets a 500 and reasonably assumes nothing happened — but durable state exists.
- No `LoanProposalCreatedEvent` was published for that document — downstream consumers
  never learn about it.
- A retry with the same id gets `ALREADY_EXISTS` — the "idempotency" gate turns into a
  dead end for a request that never observably succeeded.

Both NPEs are fixed so this particular trigger is gone, but the window is structural: any
exception between insert and publish leaves the same half-state. Worth raising with the
bits-ddd owners (see §7).

## 6. Verification (row 9)

```json
POST /api/loan-proposals → HTTP 202 {"message": "ACCEPTED", "success": true}
```

Persisted document (trimmed):

```json
{
  "_id": "test-proposal-0001",
  "proposalNumber": "202607-00004",
  "applicationDate": "2026-07-07",       // from branch lastAccountingBusinessDate, not server clock
  "status": "CREATED",                    // inherited field, now set
  "version": 0,                           // now set
  "loanProposalStatus": "PENDING",
  "branchCode": "B001",
  "proposedLoanAmount": {"$numberDecimal": "50000"},
  "tracerId": "938dfbb9-..."
}
```

Sequence counter: `{_id: "202607", seq: 4}` — month-keyed, atomic `$inc` confirmed live.

Also exercised on the way: 422 on missing source data, 400 `BUSINESS_DATE_NOT_AVAILABLE`
(this session's guard, firing before the counter — no number burned), 400 `MEMBER_SCREENED`,
400 `ALREADY_EXISTS`. Full unit test suite green after both fixes.

## 7. Follow-ups

| # | Item | Owner |
|---|---|---|
| 1 | Commit the two fixes (`version`/`tracerId` init, `domainStatus` → `status`) | us — pending |
| 2 | Ask bits-ddd owners: should `AggregateRoot` default `version = 0L` and require `status` in a constructor instead of leaving both null? Every consumer of the library can hit Bug A/B | library team |
| 3 | Ask bits-ddd owners: is persist-then-publish meant to be atomic (outbox)? Row 7 shows a 500 can leave a persisted-but-unannounced aggregate | library team |
| 4 | 422 `SOURCE_DATA_ERROR` hides which lookup failed; the errors map exists in `SourceDataFetchBuilder` — surfacing it would have saved most of this run's setup time | library team / us |
| 5 | Seeding recipe (business id as `_id`, `tenantId: "1"`) should go into a project run skill or test fixture | us |
