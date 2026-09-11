# cloud-itonami-iso3166-syc

Open ISO 3166 Blueprint for **SYC**: Seychelles -- **`:implemented`**.

This repository designs **and implements** a forkable OSS business for
an independent public-sector market-entry consultant: an already-
incorporated operator (e.g. a `cloud-itonami-cofog-{code}`,
`cloud-itonami-isco-{code}`, `cloud-itonami-unspsc-{segment}` or
`cloud-itonami-{ISIC}` blueprint fork) gets a Compliance Advisor +
independent **Market-Entry Compliance Governor** to navigate public-
procurement registration, local business/tax registration, and
regulatory-compliance rules in Seychelles, so the operator can win and
service a government contract without hiring a full in-house
compliance department.

## Official surface (curl/pdftotext/tesseract-verified 2026-07-22/23 -- every `.gov.sc` / `fsaseychelles.sc` / `investinseychelles.com` host this iteration fetched returned HTTP 200 directly, no bot-detection blocker was hit)

- **Procurement**: `pou.gov.sc`, the Procurement Oversight Unit's (POU)
  own live e-procurement/tender portal, established by the Public
  Procurement Act, 2008 (Cap. 305, Act 33 of 2008, amended by Act 37 of
  2021, Act 33 of 2022 and Act 15 of 2025 -- none of which touch Part
  VIII's bidder-eligibility provisions). The National Tender Board
  approves procurement above the Public Procurement Regulations 2014's
  own First Schedule thresholds.
- **Company registration -- TWO genuinely distinct regimes, verified
  separately rather than conflated**: the Financial Services Authority
  (FSA, `fsaseychelles.sc`) regulates International Business Companies
  (IBCs) under the International Business Companies Act, 2016 (Act 15
  of 2016, Cap. 274); the Registration Division (`registrationdivision.
  gov.sc`, under the Department of Legal Affairs, President's Office)
  registers DOMESTIC companies under the Companies Act, Chapter 40
  (Companies Ordinance, 1972) and business names under the Registration
  of Business Names Act, Chapter 202. The Beneficial Ownership Act, 2020
  (Act 4 of 2020) is the one regime this iteration found that applies
  UNIFORMLY across both the domestic and IBC/offshore regimes.
- **Foreign investment**: the Seychelles Investment Board (SIB,
  `investinseychelles.com`, established July 2004) operates under the
  Seychelles Investment Act, 2010 (Act 31 of 2010) -- confirmed via
  `tesseract` OCR of a scanned-image-only primary-text PDF.
- **Tax**: the Seychelles Revenue Commission (SRC, `src.gov.sc`) issues
  a Taxpayer Identification Number (TIN) within 24 hours of successful
  business registration, under the Business Tax Act, 2009 (Cap 20)
  s.81.

## Implementation (R0)

| Piece | Location |
|---|---|
| Actor namespaces | `src/marketentry/*` |
| Governor | `:market-entry-compliance-governor` |
| Ops | `:engagement/intake` · `:jurisdiction/assess` · `:filing/draft` · `:filing/submit` |
| Flagship HARD check | `director-conviction-disqualifying` (Public Procurement Act, 2008 s.90(1)(d): a bidder, or any of its directors/officers, convicted of a criminal offence related to professional conduct or false statements within a THREE-YEAR lookback of the procurement proceedings' commencement is disqualified -- independently recomputed from the engagement's own declared conviction/submission dates, see `docs/adr/0001-architecture.md`) |
| Compliance catalog | `src/statute/facts.cljk` -- Companies Act (Cap. 40), Employment Act (Cap. 69), Beneficial Ownership Act 2020 |
| Tests | `clojure -M:dev:test` |
| Demo | `clojure -M:dev:run` |
| Architecture ADR | [`docs/adr/0001-architecture.md`](docs/adr/0001-architecture.md) |

`:filing/submit` is never in any phase's `:auto` set -- human sign-off
is structural, not a rollout milestone.

## No robotics premise -- digital/data service exemption

Market-entry and procurement-compliance navigation is a pure data/software
service with no physical-domain work (portal registration, document
checklists, regulatory-change monitoring) -- the same exemption class as
`cloud-itonami-6310` (HR SaaS replacement) and `cloud-itonami-gtin-*`.
`blueprint.edn` sets `:itonami.blueprint/robotics false` and
`:required-technologies` lists only real capabilities (`:identity`,
`:forms`, `:dmn`, `:bpmn`, `:audit-ledger`), no `:robotics`.

## Core Contract

```text
operator intake + prior filing history
        |
        v
Compliance Advisor -> Market-Entry Compliance Governor -> filing draft, or human sign-off
        |
        v
gated portal registration / filing submission + audit ledger
```

No automated proposal can submit a portal registration or filing the
governor refuses, suppress a compliance record, or claim a legal/tax
conclusion the governor has not cleared. `:filing/submit` is never in any
phase's `:auto` set -- it always requires human sign-off.

## What this is NOT

- **Not the government of Seychelles.** This blueprint is an
  independent operator the government contracts with or that bids into
  its procurement -- never the government itself, and never an
  official channel.
- **Not legal or tax advice.** Every regulatory claim must cite the
  official source and route final filings to Seychelles-licensed
  counsel or a registered agent where the law requires licensed
  representation.

## Capability layer

Required capabilities (`blueprint.edn`):

- :identity
- :forms
- :dmn
- :bpmn
- :audit-ledger

See [`docs/business-model.md`](docs/business-model.md) and
[`docs/operator-guide.md`](docs/operator-guide.md).

## License

AGPL-3.0-or-later.

## Culture catalog

Alongside the market-entry / statute catalogs, this repo carries a
**country-level regional-culture catalog** (ADR-2607171400 addendum 2,
`cloud-itonami-municipality-culture-catalog` Wave 1, in
`com-junkawasaki/root`) — national dishes, protected products, beverages,
crafts, festivals and heritage sites for Seychelles:

- `src/culture/facts.cljk` — the catalog, source of truth (keyed by
  uppercase ISO3, mirroring `statute.facts`).
- `schema/culture.edn` — DataScript schema.
- `data/culture-tx.edn` — derived DataScript tx-data (regenerated from
  the catalog, never hand-edited).

City-level counterparts live in the `cloud-itonami-municipality-*` repos.
Same provenance discipline as the compliance catalogs: every entry cites a
source URL that was actually fetched and read on `:culture/retrieved-at`;
summaries state only what the cited source confirms. An item not in
`culture.facts/catalog` has no spec-basis — never fabricate one.
