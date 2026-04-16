# Context Tracker

## Current status
- Spring Boot plus jte scaffold created under `owner.nycll152`.
- File-backed checker, core phase-1 routes, lead capture, and admin summary are implemented.
- Official DOB, NYC311, and rule-text source packs are normalized into `data/normalized/sources`.
- The checker now reads official cycle, exemption, timeline, penalty, and gas-status rules from normalized JSON instead of using only launch-safe heuristics.
- Pre-deploy locks are in place: `/admin` basic auth, global noindex by default, `robots.txt` disallow-all by default, request rate limiting, CSP and browser security headers, and deployment guards for public indexing.
- A second persona pass now pushed the product toward blocker-first entry, explicit confidence, scenario checklists, and visible source anchors.

## Latest decisions
- Canonical page unit is `checker or filing-next-step route`, not a borough blog.
- Phase 1 public focus is `due now + what to file next + who to contact`.
- Follow Up #7 changes are part of the base product, not a later update.
- Initial monetization should prioritize LMP and certification-help routing, not broad directories.
- District overlays should only ship when route evidence proves they add value beyond the checker.
- The first checker supports address plus optional BIN or BBL, instead of forcing full address only.
- The first checker now also supports optional DOF class input because the official exemption logic depends on property classification.
- Default CTA should stay scenario-aware: `Get LMP Help` for active gas service, broader filing or certification help for the other scenarios.
- Public indexing stays off until deploy-time configuration sets a real `https` base URL and a non-default admin password.
- Admin metrics should not be public; the route now requires HTTP Basic authentication.
- The home page should lead with blocker selection before the wider route library.
- Checker and route pages should expose preparation checklists and official source anchors next to the decision.
- The product should show confidence whenever core inputs such as district, property classification, or gas-status branch are incomplete.

## What changed this session
- Captured and normalized the official source pack for the DOB LL152 inspections page, LL152 FAQs, Follow Up #7, Cycle 2 service notice, NYC311 certification article, and 1 RCNY 103-10 rule text.
- Expanded raw LL152 data files for cycle schedule, exempt DOF classes, fees and penalties, and waiver grounds.
- Added `checker-rules.json` and updated the checker so due-cycle, exemption, penalty, and gas-status outcomes read from official rules.
- Updated route content to reflect one-time no-gas-piping certification, every-cycle no-active-gas-service documentation, Cycle 2 Sub-cycle C districts, and the current penalty split.
- Extended integration coverage to verify the official district verdict and exempt DOF classification path.
- Added Spring Security, request validation tightening, CSV formula-injection hardening, request rate limiting, global robots locking, and deployment-time startup guards.
- Added `ops/genius_persona_council_2026-04-14.md` and translated that council into product changes: blocker-first home navigation, checker confidence plus preparation lists, and route-level source anchors.

## Next recommended tasks
- Add richer admin reporting for stale source records and route-family breakdowns.
- Add source-to-route traceability in the admin so each public claim can expose the current supporting source ids without opening page HTML.
- Replace broad DOF-class heuristics for `M3` and `M4` with occupancy-unit detail if that data becomes available.
- Decide whether the first public CTA copy should bias harder toward filing help or LMP help on the non-checker routes.
- Before public deploy, set a real `app.base-url`, replace the default `app.admin.password`, and flip `app.public-indexing-enabled=true`.
- Run browser QA and content review before any public deployment.

## Open questions
- Should the default non-checker CTA emphasize `Get Filing Help` or `Get LMP Help` once real route data arrives?
- Should district pages start only after traffic appears, or should sub-cycle C get a minimal indexed district layer on day one?
- Should the checker add a dedicated occupancy or unit-count question so `M3` and `M4` exemptions can be handled more precisely?
