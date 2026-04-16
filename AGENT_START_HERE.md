# Agent Start Here

## Project
NYCLL152Verdict

## Current implementation direction
- Preferred stack: `Spring Boot` + `jte`
- Preferred storage: raw `CSV` and `JSON` source files plus normalized and derived `JSON`
- No runtime database in phase 1
- This folder is currently a design packet, not a scaffolded app

## What this folder contains
- A self-contained product and implementation packet for an NYC-only Local Law 152 compliance verdict site
- Enough context for a new agent to start implementation without chat history

## Read order
1. `ops/context_tracker.md`
2. `ops/wedge_focus_2026-04-13.md`
3. `ops/source_audit_2026-04-13.md`
4. `ops/persona_council_2026-04-13.md`
5. `ops/promotion_review_system_2026-04-13.md`
6. `ops/route_promotion_board.md`
7. `README.md`
8. `spec/00_strategy.md`
9. `spec/01_query_and_user_map.md`
10. `spec/02_site_architecture.md`
11. `spec/03_data_and_operations.md`
12. `spec/04_commercial_model.md`
13. `spec/05_editorial_rules_and_execution.md`
14. `spec/06_indexing_quality_and_analytics.md`
15. `spec/07_technical_architecture.md`
16. `spec/08_delivery_and_handoff.md`
17. `spec/09_launch_surface_and_route_inventory.md`
18. `spec/10_acceptance_test_matrix.md`

## Rules for any future agent
- The canonical SEO unit is `LL152 checker + filing next-step + LMP routing`, not a generic NYC plumbing article.
- This is not a broad local plumber site.
- This is not a generic `What is LL152?` explainer site.
- The primary wedge is:
  - building owner or manager needs to know if the property is due now
  - the user needs the exact filing next step
  - the user may need LMP or certification help
- Phase 1 public wedge is narrower:
  - address or building-profile checker
  - filing next-step guidance
  - no-gas versus no-active-gas-service split
  - extension, penalty, and waiver clarity
  - after-inspection timing and GPS1/GPS2 flow
- Always distinguish `no gas piping` from `no active gas service`.
- Always include the 2026 Follow Up #7 changes when they affect next-step guidance.
- District pages, unsafe-condition pages, corrected-certification pages, and broad LMP finder pages are support-layer until the first wedge proves traction.
- If analytics or admin metrics exist, start every review session by checking `ops/route_promotion_board.md`.
- Future agents must produce a user-facing `promotion recommendation` summary when any held route has enough evidence.
- Future agents may recommend promotion, but should not silently widen the public index surface without surfacing the recommendation first.
- Official DOB pages, FAQs, service notices, and rule updates outrank secondary plumber pages.
- If strategy changes, update the relevant spec file and `ops/context_tracker.md`.

## Minimum handoff standard
- Update `Current status`
- Update `Latest decisions`
- Update `What changed this session`
- Update `Next recommended tasks`
- Update `Open questions`
- Update `ops/route_promotion_board.md` if any route recommendation status changes
