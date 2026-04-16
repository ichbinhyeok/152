# 03 Data And Operations

## Data philosophy
The moat is not generic prose. The moat is a current, source-backed checker and decision dataset.

## Recommended raw data files
- `data/raw/ll152/properties.csv`
- `data/raw/ll152/cycle_schedule.csv`
- `data/raw/ll152/rule_notes.csv`
- `data/raw/partners/partner_types.csv`
- `data/raw/sources/*.json`

## Recommended normalized outputs
- `data/normalized/ll152/current.json`
- `data/normalized/routes/{slug}.json`
- `data/normalized/sources/{sourceId}.json`
- `data/derived/routes.json`

## Recommended ops files
- `data/ops/route-status.csv`
- `data/ops/promotion-review.json`
- `data/ops/admin-metrics-snapshot.json`

## Route-status schema
- route id
- route path
- route family
- phase
- index status
- source freshness status
- checker starts
- checker completions
- cta clicks
- lead submissions
- promotion recommendation
- recommendation reason

## Source workflow
1. Capture official source URLs and notes
2. Normalize into structured source records
3. Link source ids to route and checker rules
4. Render only from normalized records
5. Block promotion when source freshness is stale

## Review cadence
- core routes: every `30` days
- tactical notices and service updates: every `14` days during active cycle season
- partner roster: every `30` days once launched

## Ops review needs
The eventual admin page should expose:

- stale source records
- blocked routes
- checker completions
- CTA clicks by route family
- leads by scenario
