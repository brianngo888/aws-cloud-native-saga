# AWS Cloud-Native Saga — Architecture, Source, Workbook

**Learning reference; NOT production-certified or fully integrated.** This package builds upon the original 39-file starter and adds architecture, term explanations, a 52-hour workbook, failure matrix, assessment and a public-repository security checklist.

## Read in this order
1. `PART-1-ARCHITECTURE/00-START-HERE.md` then requirements, terms and ADRs.
2. `PART-2-SOURCE/00-SOURCE-MAP.md` — distinguish included source from missing production implementation.
3. `PART-3-WORKBOOK/00-ROADMAP.md` then each lab.
4. `SECURITY-PUBLISH-CHECKLIST.md` before pushing to public GitHub.

## Included source
`services/order-service`, `lambdas`, `workflows`, `infrastructure/terraform`, `deploy/helm`, `.gitlab-ci.yml`.

## Known blockers to full production deployment
Transactional outbox, business idempotency, actual RDS resources, full CI publish/deploy, scoped AWS identities, secrets, production authentication, full compensation/reconciliation and automated integration tests. See source map for exact status. **Do not use with real payments or customers.**

## Quick preflight
`bash scripts/preflight.sh` (requires locally installed Gradle/Terraform/Helm for full checks). Configure a budget before provisioning AWS resources.
