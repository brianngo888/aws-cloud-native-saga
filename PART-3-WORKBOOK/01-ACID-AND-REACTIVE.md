# Lab 01 — ACID, WebFlux, and Reactor (5–8 hours)

## A. Baseline local exercise before AWS
1. `docker compose up -d` (inspect compose.yaml and environment first).
2. `cd services/order-service && gradle test`.
3. Start app with DB environment variables from `application.yml`; `curl -i http://localhost:8080/actuator/health`.
4. Submit a valid order and query it; save response and DB rows.

## B. Atomicity
Add `order_lines` and `outbox` tables, with a `CHECK (quantity > 0)` constraint. Use `TransactionalOperator` around a *single composed reactive chain* containing all inserts. Force a negative quantity on line 2. Expected: order, lines, outbox all absent. A remote Step Functions call must not be inside a DB transaction.

## C. Consistency
Attempt an invalid quantity and an invalid foreign key. Expect database rejection and no committed invalid rows. Explain what application invariants DB constraints cannot express.

## D. Isolation
Open two SQL sessions, both attempt reservation of last unit. First test naïve SELECT then UPDATE, then fix with `UPDATE stock SET available=available-1 WHERE sku=$1 AND available >= 1 RETURNING available`. Expected exactly one winner; no negative stock. Explain READ COMMITTED vs SERIALIZABLE and retry of serialization failures.

## E. Durability
Commit an order, restart PostgreSQL container without removing volumes, query it again. Then separately restore a backup into a fresh database. These are different proofs.

## F. Reactor
Write a `StepVerifier` test for success, `Mono.error` and retry with backoff. Explain why `block()` on event-loop threads and `subscribe()` in a service are dangerous. AWS SDK async future may be wrapped using `Mono.fromFuture`, with cancellation and timeouts considered.

## Exit questions
What does @Transactional guarantee in R2DBC? What if you call Step Functions after commit and the process dies? Can Saga reverse a shipment already delivered? Why is isolation not the same as eventual consistency?
