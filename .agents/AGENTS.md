# Workspace Coding Rules and Constraints

Always apply the following rules when working on the `bits-loan-proposal-command` project:

## Architecture and Library Compliance
* **Prioritize `bits-ddd-lib` 1.2.1 Patterns**: Ensure all DDD layers comply with the library's version 1.2.1 API:
  * **Source Data Provider**: Implement `SourceDataProvider<Command>` and use `SourceDataCoordinator` to concurrently fetch lookup snapshots. Do not use legacy sequential service helper classes.
  * **Mutable Source Data snapshots**: Ensure snapshot classes extend `com.bits.ddd.domain.sourcedata.SourceData` and are annotated with `@MongoSourceData`. Avoid using immutable Java records for snapshots as they fail to compile with the library's mutable tenant setters.
  * **Persistence Wiring**: Use field injection with `@PersistDomain` on the handler class to automatically generate the transactional `DomainPersistenceService` bean.

## Distributed Systems and Resiliency
* **Distributed System Constraints**: Always keep in mind for distribution system, multiple instane and DR:
  * Use distributed locks (e.g., Redis/Redisson) rather than in-memory concurrency locks (`ConcurrentHashMap`) to support scaling to multiple instances safely.
  * Ensure idempotent command handling and message consumption to prevent duplicate operations in a multi-instance topology.
