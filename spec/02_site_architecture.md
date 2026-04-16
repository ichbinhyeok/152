# 02 Site Architecture

## Canonical entities

### Building verdict input
Fields:

- address
- BIN or BBL if available
- DOF class if available
- community district if available
- gas piping yes or no
- active gas service yes or no

### Route family
Core families:

- checker
- filing-next-step
- no-gas-split
- extension-penalty-waiver
- after-inspection
- due-cycle
- district-overlay

### Partner type
- LMP inspection help
- filing or certification help

## URL graph

### Core public routes
- `/`
- `/ll152-checker/`
- `/filing-next-step/`
- `/no-gas-vs-no-active-gas-service/`
- `/extension-penalty-waiver/`
- `/after-inspection-gps1-gps2/`
- `/2026-deadline/`

### Held routes
- `/districts/{district}/`
- `/corrected-certification/`
- `/unsafe-condition/`
- `/exempt-building-notification/`
- `/lmp-finder/`

### Trust routes
- `/about/`
- `/methodology/`
- `/contact/`
- `/not-government-affiliated/`

## Page modules

### Checker page
- address or building-profile input
- applicability answer
- due-cycle answer
- next-step summary
- CTA

### Filing-next-step page
- quick answer
- next filing path by scenario
- timing summary
- CTA

### No-gas split page
- no gas piping versus no active gas service
- cycle frequency difference
- submission difference
- CTA

### Penalty page
- penalty nuance
- extension and waiver summary
- violation-resolution summary
- CTA

## Internal linking rules
- Checker links to all phase-1 public routes.
- Filing-next-step links to no-gas split, after-inspection, and penalty pages.
- Held routes can be linked contextually, but should not displace the phase-1 navigation.
