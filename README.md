# NYC LL152 Verdict

Working internal project: `NYCLL152Verdict`  
Suggested package root: `owner.nycll152`

**Date:** 2026-04-14 (Asia/Seoul)  
**Purpose:** This folder is a self-contained design packet for building an NYC-focused **LL152 checker + filing next-step + LMP routing** site.

## What you are building
An NYC-only compliance decision site for building owners, landlords, supers, and managers who need an answer now:

- is this building covered?
- is this building due in the current cycle?
- what exactly must be filed next?
- what happens if there is no gas piping?
- what happens if there is no active gas service?
- what is the penalty or waiver path if nothing was filed?
- do I need an LMP or certification help now?

The product should tell the user:

- likely applicability
- likely due cycle and deadline
- exact next filing or correction path
- penalty and waiver exposure
- who needs to act next
- whether LMP help is the right next step

## Phase 1 launch wedge
Phase 1 is narrower than the full LL152 category.

The launch wedge is:

- current-cycle or near-term due owner intent
- address or building-profile verdict
- filing next-step uncertainty
- LMP or compliance-help routing

This means the first public build should behave more like:

- `LL152 checker + filing next-step + LMP routing`

and less like:

- `NYC gas safety blog`
- `generic plumber directory`

## Why this concept is attractive
- The trigger is deadline and penalty driven.
- Official guidance exists, but it is fragmented across pages, FAQs, notices, and rule updates.
- Follow Up #7 introduced new nuance around no-active-gas-service, no-gas-piping, 2-day notifications, fees, and violation resolution.
- Lead value is meaningful because the paid next step often involves an LMP or filing support.
- The flow can stay form-first and email-first without requiring phone-heavy brokerage.

## Product thesis
Do not build `What is Local Law 152?`

Build a **LL152 checker + filing next-step + LMP routing** engine for users trying to answer:

- Am I due now?
- What exactly must I file next?
- What changes if there is no gas piping versus no active gas service?
- What do the penalty and waiver paths actually mean?
- Do I need an LMP right now?

## File map
- `AGENT_START_HERE.md` - read order and handoff rules for any future agent
- `ops/context_tracker.md` - current status, decisions, and next tasks
- `ops/wedge_focus_2026-04-13.md` - current primary wedge and narrow operating loop for the first build phase
- `ops/source_audit_2026-04-13.md` - official-source anchor map and how each source should shape the product
- `ops/persona_council_2026-04-13.md` - forced debate across demand, SERP, funnel, risk, sponsor, and portfolio perspectives
- `ops/genius_persona_council_2026-04-14.md` - product-thinking council that tightened blocker-first UX, confidence, and evidence display
- `ops/promotion_review_system_2026-04-13.md` - how future agents should review metrics and recommend route promotion
- `ops/route_promotion_board.md` - current held-route board and recommendation status
- `spec/00_strategy.md` - market thesis, positioning, wedge, and rollout philosophy
- `spec/01_query_and_user_map.md` - jobs-to-be-done, trigger states, query families, and first user map
- `spec/02_site_architecture.md` - canonical entities, URL graph, route families, and internal linking
- `spec/03_data_and_operations.md` - data model, source hierarchy, verification workflow, and refresh cadence
- `spec/04_commercial_model.md` - CTA logic, partner types, lead intake, and sponsor packaging
- `spec/05_editorial_rules_and_execution.md` - writing rules, trust guardrails, and page-family ship criteria
- `spec/06_indexing_quality_and_analytics.md` - indexing gates, route quality rules, and measurement plan
- `spec/07_technical_architecture.md` - system boundaries, package map, rendering model, and services
- `spec/08_delivery_and_handoff.md` - workstreams, milestones, and implementation order
- `spec/09_launch_surface_and_route_inventory.md` - first launch-surface page inventory
- `spec/10_acceptance_test_matrix.md` - launch-critical tests and definition of done

## Recommended build stack
- `Spring Boot` + `jte`
- Server-rendered checker and compliance pages with file-backed content
- File-based pipeline using raw `CSV` plus normalized and derived `JSON`
- No runtime database in phase 1
- Java runtime baseline: `21`

## Current implementation state
- Spring Boot plus jte scaffold is in place
- Phase-1 home, checker, trust routes, core route pages, and admin summary are implemented
- File-backed route inventory, route-status tracking, and lead capture storage are seeded
- Official-source normalization is in place for the core DOB page, LL152 FAQs, Follow Up #7, the Cycle 2 service notice, NYC311 GPS2 guidance, and the current rule text
- The checker reads official cycle, exemption, penalty, filing-timeline, and gas-status rules from `data/normalized/ll152/checker-rules.json`
- Home, checker, and route surfaces now reflect the latest persona-council decisions: blocker-first entry, confidence labels, preparation checklists, and official source anchors
- Pre-deploy locks are active by default: admin authentication, global noindex, disallow-all `robots.txt`, request rate limiting, CSP and browser security headers, and deployment guards for public indexing

## Pre-deploy locks
- `app.public-indexing-enabled=true` is now enabled for the live domain, but the deployment guards still block startup if the public-indexing requirements are not met
- `/admin` requires HTTP Basic auth using `app.admin.username` and `app.admin.password`
- public indexing cannot be enabled with a localhost or non-HTTPS `app.base-url`
- public indexing cannot be enabled while the admin password is still the default placeholder
- public indexing cannot be enabled if any indexable route in `data/ops/route-status.csv` is not marked `current`

## Domain and production base URL
- canonical production domain is `https://ll152guide.com`
- `app.base-url` is hardcoded to `https://ll152guide.com` in `application.properties`
- requests that arrive on the wrong public host or over plain `http` are redirected to the configured canonical base URL
- `app.public-indexing-enabled=true` is now hardcoded for the live deploy on `ll152guide.com`
- only the admin credentials should come from runtime environment variables: `APP_ADMIN_USERNAME` and `APP_ADMIN_PASSWORD`
- use `.env.production.example` as the deployment variable checklist

## OCI deploy baseline
- GitHub Actions builds an ARM64 image and pushes `shinhyeok22/152`
- OCI runs `docker compose` from `~/deploy/ll152guide`
- nginx on the OCI host already routes `ll152guide.com` and `www.ll152guide.com` to `127.0.0.1:8099`
- required GitHub Actions secrets: `DOCKERHUB_USERNAME`, `DOCKERHUB_TOKEN`, `OCI_HOST`, `OCI_USERNAME`, `OCI_KEY`, `APP_ADMIN_PASSWORD`
- optional GitHub Actions secret: `APP_ADMIN_USERNAME` (defaults to `admin`)
- optional GitHub Actions variable: `OCI_APP_PORT` (defaults to `8099`)

## Recommended launch geography
Start with `NYC` only.

Reason:

- the law and filing system are city-specific
- the official sources are concentrated and structured
- the 2026 commercial wedge is tied to NYC timing, not broad national content

## Core route families
- checker
- filing next-step
- no-gas versus no-active-gas-service
- extension, penalty, and waiver
- after-inspection timing
- due-cycle and deadline guidance
- held district overlays later

## Phase 1 launch families
- checker
- filing next-step
- no-gas versus no-active-gas-service
- extension-penalty-waiver
- after-inspection timing
- 2026 sub-cycle C guide

Everything else should start as support-layer or `noindex` inventory until the first wedge proves traction.

## Recommended monetization order
1. LMP inspection or filing-help leads
2. no-gas or no-active-gas-service certification help
3. later district-level or owner-portfolio sponsorship if route demand is proven

## Portfolio position
This is a strong `priority-2` tactical build candidate.

Why:

- first cash can be faster than many broader home-service ideas
- but market ceiling is narrower and more cycle-driven than the best evergreen assets

## Build principles
- This is a compliance verdict product, not a local plumbing blog.
- The winning moment is `am I due, what do I file next, and who do I need`.
- Always separate official filing guidance from partner routing.
- Always separate `no gas piping` from `no active gas service`.
- Show uncertainty when critical inputs are missing, and show source anchors where the decision is made.
- If a page does not reduce filing ambiguity or route to the next action, it probably should not ship.
