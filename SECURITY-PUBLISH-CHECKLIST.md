# Before making this repository PUBLIC
- [ ] Run secret scan on entire Git history, not only current files.
- [ ] Confirm no AWS keys, tokens, passwords, internal endpoints, account identifiers, customer data, or sensitive architecture.
- [ ] Replace all real credentials with placeholders and examples.
- [ ] Inspect Terraform state, tfvars, `.env`, generated build outputs and CI artifacts; do not commit them.
- [ ] Confirm Lambda simulated payment does not touch real cards.
- [ ] Add repository secret scanning and branch protection.
- [ ] Never publish a production incident runbook containing internal access paths.
