# Loan Proposal Command Service Documentation

This folder contains documentation, domain specifications, and architectural constraints for the `bits-loan-proposal-command` microservice.

## Scaffolding Architecture

This service is built using Clean Domain-Driven Design (DDD) principles:

1. **Domain Layer**:
   * Encapsulates aggregates, entities, value objects, and domain events.
   * Business validations are enforced using specifications.
2. **Application Layer**:
   * Mediates interactions between presentation and domain layers.
   * Handles commands and routes lookups concurrently using the snapshot coordinator.
3. **Infrastructure Layer**:
   * Implements database mapping, outbox patterns, and event publishing brokers.
4. **Presentation Layer**:
   * Rest controller endpoints translating request DTOs into application commands.
