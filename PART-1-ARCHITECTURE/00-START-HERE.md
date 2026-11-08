# Part 1 — Architecture and learning documentation

**Status: instructional reference, NOT production-certified.** Read this before running Terraform. A previously generated 39-file starter is included and has known missing integrations. The purpose of this edition is to make those gaps explicit and provide reproducible acceptance tests.

## Learning objectives
1. Define measurable functional and non-functional requirements.
2. Explain ACID at the boundary of one PostgreSQL database, and why Saga does not provide global ACID.
3. Explain synchronous API acceptance vs asynchronous order completion.
4. Identify IAM, networking, secrets, encryption, and supply-chain trust boundaries.
5. Defend EKS vs ECS/Lambda, RDS vs DynamoDB, orchestration vs choreography, and operational cost tradeoffs.
6. Distinguish a demonstrated result from an aspirational SLO.

## Proposed system context
Client -> ingress/API -> Java 25 WebFlux Order API (EKS) -> PostgreSQL (RDS)
Order API -> durable transactional outbox -> dispatcher -> Step Functions Standard
Step Functions -> Lambda Inventory -> Lambda Payment -> Lambda Shipping
Compensations: void payment, release inventory; failed compensation -> human reconciliation
GitLab -> OIDC -> AWS scoped role -> ECR digest -> Helm -> EKS
Telemetry -> CloudWatch / OpenTelemetry; vulnerability scanning in CI.

## Important architecture correction
The starter starts Step Functions after the order DB commit, with no durable outbox. That leaves a crash window (order saved but workflow never started). Do NOT deploy this version for real money or real customers. Implement the outbox before claiming delivery guarantees.

## Definition of done
A requirement is DONE only with (1) code, (2) automated test, (3) deployment evidence, (4) operational ownership, and (5) security review. A generated file alone is not evidence.
