# 08 Delivery And Handoff

## Delivery objective
Ship a narrow but commercially credible first version that can:

- render the checker and core routes
- route users by filing-next-step scenario
- capture leads
- log enough analytics to learn quickly

## Implementation order
1. scaffold app and route inventory
2. implement home and checker
3. implement filing-next-step routes
4. implement lead capture
5. implement admin review
6. run end-to-end QA

## Handoff checklist
- update `ops/context_tracker.md`
- update source audit when official anchor sources change
- note any route families intentionally held `noindex`
- review `ops/route_promotion_board.md` if metrics exist
