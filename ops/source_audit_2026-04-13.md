# Source Audit - 2026-04-13

## Source hierarchy
Use this order:

1. current DOB LL152 page
2. current DOB LL152 FAQs
3. current DOB service notices and Follow Up #7
4. NYC311 certification guidance
5. rule text and secondary plumber pages only for nuance or SERP checks

## Official anchors

### Periodic Gas Piping System Inspections
- URL: `https://www.nyc.gov/site/buildings/property-or-business-owner/gas-piping-inspections.page`
- Why it matters:
  - confirms scope exclusions
  - gives the cycle schedule
  - confirms sub-cycle C for `2026`
  - confirms no-gas and extension paths
- Product consequence:
  - checker and due-cycle logic are justified directly from DOB

### LL152 FAQs
- URL: `https://www.nyc.gov/site/buildings/property-or-business-owner/ll152-faqs.page`
- Why it matters:
  - clarifies tenant-space scope
  - clarifies unsafe condition reporting
  - clarifies GPS1 and GPS2 timing
  - clarifies access limits and incomplete inspection rules
- Product consequence:
  - after-inspection and scope pages can be more useful than plumber marketing pages

### NYC311 certification article
- URL: `https://portal.311.nyc.gov/article/?kanumber=KA-03353`
- Why it matters:
  - confirms DOB NOW submission path
  - confirms base penalty framing
- Product consequence:
  - filing-next-step routes can show concrete submission logic

### Follow Up #7
- URL: `https://www.nyc.gov/assets/buildings/pdf/ll152follup7-su.pdf`
- Why it matters:
  - adds or clarifies:
    - no active gas service documentation every cycle
    - no gas piping certification only once
    - utility company can provide no-gas certification
    - 2-Day Notification before inspection
    - violation resolution and waiver path
    - filing fees
- Product consequence:
  - the product is incomplete without this notice
  - this is what sharpens the wedge beyond older LL152 content

### Cycle 2 service notice
- URL: `https://www.nyc.gov/assets/buildings/pdf/gps-cycle2-sn.pdf`
- Why it matters:
  - confirms penalty nuance:
    - `$1,500` for 3-family residential building
    - `$5,000` for all other buildings
  - confirms 2026 sub-cycle C timing
  - confirms exempt-building notification note for owners incorrectly treated as covered
- Product consequence:
  - penalty routes must not flatten everything to `$5,000`

### Rule text
- URL: `https://www.nyc.gov/assets/buildings/rules/1_RCNY_103-10_prom_details_date.pdf`
- Why it matters:
  - rule basis for timing and penalty logic
- Product consequence:
  - use mainly for legal nuance, not as primary UX text

## Product consequence summary
- build around checker and filing-next-step ambiguity
- make no-gas versus no-active-gas-service a first-class branch
- make extension, waiver, and fee nuance visible without becoming a legal treatise
