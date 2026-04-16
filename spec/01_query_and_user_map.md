# 01 Query And User Map

## Primary user segments

### Owner or manager checking current applicability
Questions:

- Is this building due now?
- Is this building covered?
- What if this is a one-, two-, or three-family building?

Commercial value:

- high

### Owner with filing-next-step confusion
Questions:

- What exactly do I file next?
- Is it GPS2, no-gas, no-active-gas-service, correction, or extension?
- Who submits what?

Commercial value:

- very high

### Owner with penalty or waiver concern
Questions:

- What is the penalty?
- Can I still file?
- Can I request a waiver?

Commercial value:

- high

### Owner needing LMP help
Questions:

- Do I need an LMP right now?
- Can I get certification help without phone-heavy consulting?

Commercial value:

- very high

## Trigger states
1. `need_applicability_verdict`
2. `need_due_cycle_verdict`
3. `need_filing_next_step`
4. `need_no_gas_split`
5. `need_penalty_or_waiver`
6. `need_lmp_help`

## Query families

### Applicability and deadline
- ll152 checker
- ll152 2026 deadline
- is my building subject to ll152
- ll152 sub cycle c

### Filing next step
- what do i file for ll152
- gps2 ll152 what next
- ll152 filing next step

### No gas split
- ll152 no gas piping
- ll152 no active gas service
- no gas piping gps2

### Penalty and waiver
- ll152 penalty
- ll152 extension
- ll152 waiver

### Help intent
- ll152 lmp
- ll152 inspection plumber
- ll152 certification help

## Priority page families

### Tier 1
- checker
- filing-next-step
- no-gas split
- extension-penalty-waiver
- after-inspection timing

### Tier 2
- 2026 due-cycle page
- district overlays
- corrected-certification page

### Tier 3
- unsafe-condition page
- exempt-building challenge page
- broad LMP finder page
