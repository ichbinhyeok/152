# 10 Acceptance Test Matrix

## Goal
This matrix defines what must work before the first public release can be considered credible.

## Rendering tests
- home renders
- checker renders
- all phase-1 routes render

## Metadata tests
- every indexable route emits a canonical URL
- every held route emits `noindex`

## Lead-flow tests
- lead form opens from checker and core routes
- submit success writes storage

## Analytics tests
- checker completions log
- CTA clicks log
- admin summary shows lead and click counts
