# Promotion Review System - 2026-04-13

## Why this exists
The founder should not have to remember when a held route is ready.

The future agent should:

1. read the wedge rules
2. read the current route board
3. inspect metrics
4. tell the user whether any held route deserves promotion

This is a recommendation system, not an auto-publish system.

## What the agent must review
- route status
- source freshness
- checker completions
- CTA behavior
- lead submissions by scenario
- partner availability

## Required review outputs
Every review should produce one of these for each held route family:

- `hold`
- `recommend_promote`
- `recommend_build`
- `recommend_demote`

And every decision must include a reason.

## Default held route families
- district overlays
- corrected-certification routes
- unsafe-condition routes
- exempt-building challenge routes
- broad LMP finder pages

## Review cadence
- at the start of any later strategy or implementation session once metrics exist
- every `14` days after launch if no one has reviewed recently
