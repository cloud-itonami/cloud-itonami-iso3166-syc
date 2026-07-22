# ADR-0001: Architecture — Seychelles market-entry compliance actor (`marketentry`)

**Status**: accepted
**Date**: 2026-07-23

## Context

`cloud-itonami-iso3166-syc` was published as a `:blueprint` (docs +
`blueprint.edn` + `deps.edn`, then a country-level `culture.facts`
catalog in a separate Wave 1 batch) but carried ZERO `src/marketentry`
or `src/statute` content -- its `:public-sector/market-entry-
compliance` domain, declared in `blueprint.edn`, was unimplemented.
This ADR closes that gap, following the pattern established by
`cloud-itonami-iso3166-jpn` (origin) and this family's small-island-
nation siblings `cloud-itonami-iso3166-grd` (Grenada) and
`cloud-itonami-iso3166-fsm` (Federated States of Micronesia) -- the
simpler, no-`goyoukiki` shape this blueprint also uses (`blueprint.edn`'s
`:required-technologies` does not list `:ontology`).

## Decision

Build the full governed-actor architecture for `marketentry`, mirroring
JPN/GRD/FSM's harness verbatim (StateGraph node names, governor
hard/escalate contract, phase 0-3 rollout, `Store` protocol with
MemStore + DatomicStore parity) and researching Seychelles' own real
market-entry rules from scratch for the country-specific content.

- **Store**: `marketentry.store`, MemStore + DatomicStore, proven parity
  via contract test.
- **Registry**: `marketentry.registry`, pure DRAFT-certificate
  construction via `unsigned-certificate`, jurisdiction-scoped sequence
  numbering (`SYC-DFT-000000`, `SYC-SUB-000000`), plus the flagship
  director-conviction-disqualification date recompute (see below).
- **Governor**: `:market-entry-compliance-governor` (family keyword from
  `blueprint.edn`).
- **Entity shape**: `engagement`, sequential draft -> submit on the same
  record. `high-stakes` = `#{:actuation/draft-filing
  :actuation/submit-filing}`.
- **Phase**: 0->3; `:filing/draft` and `:filing/submit` NEVER auto-
  commit at any phase.

### Which body registers companies -- investigated, not conflated

Seychelles has TWO genuinely distinct company-registration regimes,
confirmed by downloading and reading each body's own primary text
directly (not assumed by analogy): the Financial Services Authority
(FSA, `fsaseychelles.sc`) regulates International Business Companies
(IBCs) under the International Business Companies Act, 2016 (Act 15 of
2016, own consolidated-to-11-July-2025 PDF read via `pdftotext`; its
own chapter number, Cap. 274, is independently confirmed by the
Beneficial Ownership Act 2020's own First Schedule reference); the
Registration Division (`registrationdivision.gov.sc`, confirmed via its
own "about-us" page to fall under "the Department of Legal Affairs,
under the President's Office") registers DOMESTIC companies under the
Companies Act, Chapter 40 (Companies Ordinance, 1972, consolidated to 1
December 2014, downloaded directly and read via `pdftotext`) -- s.13
"Conclusiveness of certificate of incorporation", issued by the
"Registrar of Companies" (own primary text) -- and business NAMES under
the Registration of Business Names Act, Chapter 202 (Act 6 of 1972,
also downloaded and read directly). This ADR does NOT conflate the two,
per the task's own explicit instruction to verify which body handles
which.

The one regime this iteration found that is UNIFIED rather than split:
the Beneficial Ownership Act, 2020 (Act 4 of 2020, downloaded from
`investinseychelles.com` and read via `pdftotext`) applies, per its own
s.2(1)(a), to BOTH domestic Companies Act (Cap. 40) entities AND
International Business Companies Act (Cap. 274) entities alike -- a
precise, citable confirmation that Seychelles' post-2018/2019 OECD/EU-
pressure transparency reform was deliberately built to apply uniformly
across the domestic and offshore/IBC regimes, even though incorporation
itself remains administratively split. This finding is documented as
catalog content (`marketentry.facts` namespace docstring,
`statute.facts` for the Beneficial Ownership Act entry) rather than
forced into the governor's flagship check, because a more directly
on-topic mechanism was found in the Public Procurement Act itself (see
below).

### Flagship HARD check: `director-conviction-disqualifying` -- independently grounded in Seychelles' own s.90(1)(d)

The Public Procurement Act, 2008 (Cap. 305, Act 33 of 2008) -- downloaded
independently from BOTH the Procurement Oversight Unit's own site
(`pou.gov.sc/legislations/the-act/`) AND the Ministry of Finance,
National Planning and Trade's own site (`finance.gov.sc/resources/
legislations/`), both read in full via `pdftotext` -- establishes a
Procurement Oversight Unit (Part II) and a National Tender Board (Part
III). Its own Part VIII "BIDDERS AND SUPPLIERS" s.90(1)(d) (read
directly in the primary PDF text) bars eligibility to a bidder who, or
whose directors or officers, "have been convicted of a criminal offence
related to their professional conduct or making of false statements or
mis-representations as to their qualifications to enter into a contract
within a period of THREE YEARS preceding the commencement of the
procurement proceedings". `marketentry.registry/compute-
disqualification-expiry` independently recomputes the date on which a
declared conviction stops disqualifying (conviction date + 3 calendar
years), and `director-conviction-disqualifying?` HARD-holds
`:filing/submit` if the engagement's own declared `:submission-date`
falls on or before that expiry date.

This iteration independently confirmed s.90 (and the whole of Part
VIII) is UNTOUCHED by any of the three amendment Acts enacted since
2008, each downloaded directly from `pou.gov.sc` and read in full: the
Public Procurement (Amendment) Act, 2021 (Act 37 of 2021) amends only
ss.14/99 (Board/Review Panel composition); the Public Procurement
(Amendment) Act, 2022 (Act 33 of 2022) amends only ss.2/37/42/49/60A/
80/82/98/100/Schedule 1; the Public Procurement (Amendment) Act, 2025
(Act 15 of 2025) amends only ss.23/24/26 (retitling "Director" to
"Chief Executive Officer"). Three independently-enacted amendments to a
named Act, none of which touch the operative provision, plus a live,
actively-updated tender portal (`pou.gov.sc`, real dated tender
notices, contract awards, an appeals process), is strong, directly-
observed evidence the Act -- and specifically s.90(1)(d) -- is
genuinely in force today.

This check is the SAME mechanical technique and the SAME backward-
looking DISQUALIFICATION polarity as this family's Grenada (GRD)
sibling's own flagship check, but INDEPENDENTLY grounded in Seychelles'
own primary text with its own distinct numeric constant: THREE years,
not GRD's two. This ADR does not force an artificially "different"
check shape onto a mechanism that is, honestly, structurally similar to
a sibling's -- the Beneficial Ownership Act unification finding above
is a genuinely distinct structural finding this iteration chose NOT to
force into the governor instead, specifically to avoid manufacturing
false novelty where an honest, well-grounded, structurally-similar
mechanism was the actual best fit for a procurement-market-entry
actor's flagship check.

### Operating thresholds -- the current figures, not a stale reading of the 2008 Act alone

The 2008 Act's own Schedule 1 (read directly) sets goods/services
thresholds at SR 100,000/500,000, works at SR 150,000/750,000,
consultancy at SR 50,000/150,000. The Public Procurement Regulations
2014 (also downloaded from `finance.gov.sc` and read via `pdftotext`)
own First Schedule (Regulations 9/11) instead sets goods/services at
SCR 150,000/750,000 (RAISED from the Act's original figures) while
leaving works and consultancy unchanged. `marketentry.facts` reports
the 2014 Regulations' current figures as authoritative while disclosing
the Act's own now-superseded goods/services figures, rather than
silently picking one or asserting an unverified figure (an earlier
`pou.gov.sc` homepage summary this iteration could not re-confirm by
directly reading the raw page text cited a "SCR 850,000" National
Tender Board threshold -- this iteration explicitly did NOT include
that unverified number in the catalog, since it could not independently
confirm it by reading the primary source itself).

### Tax registration -- verified as the CURRENT regime, not an outdated "tax haven" assumption

The Seychelles Revenue Commission (SRC, `src.gov.sc`, fetched directly)
states it has operated "a self-assessment tax regime since 2010" and
allocates a Taxpayer Identification Number (TIN) "within 24 hours"
of successful business registration. The underlying Business Tax Act,
2009 (Cap 20) s.81's regulation-making power is confirmed live and
exercised as recently as 2021 via the Business Tax (Amendment of
Schedules) Regulations, 2021 (S.I. 108 of 2021, `investinseychelles.
com`, own primary text read directly: "In exercise of the powers
conferred by section 81 of the Business Tax Act 2009 ..."). This
iteration did NOT independently fetch the Business Tax Act's own
consolidated primary text (the only government-hosted link found was a
broken internal network-share path leaked into a public page, not
independently fetchable) -- this is an honest, disclosed gap, not a
fabricated section-by-section summary.

### Foreign investment

The Seychelles Investment Board (SIB, `investinseychelles.com`,
established July 2004 per its own "about-sib" page) operates under the
Seychelles Investment Act, 2010 (Act 31 of 2010). This Act's own PDF
turned out to be a SCANNED image-only document (`Creator: HP Digital
Sending Device` per its own metadata, no text layer) -- this iteration
used `tesseract` OCR directly on the rendered page images (not a
secondary summary) to confirm its own "ARRANGEMENT OF SECTIONS": Part V
s.14 "Establishment of the Seychelles Investment Board", s.15
"Objectives", s.16 "Functions".

### `statute.facts` (second, orthogonal catalog)

Three Seychelles statutes: the Companies Act, Chapter 40 (Companies
Ordinance, 1972, downloaded from `registrationdivision.gov.sc` and read
via `pdftotext`, confirming s.13's certificate-of-incorporation text
and the Registrar of Companies), the Employment Act, Chapter 69 (Act 2
of 1995, "2010 edition", downloaded from `employment.gov.sc` and read
via `pdftotext`, confirming Part VIII "TERMINATION OF CONTRACTS" and an
Employment Tribunal at s.73A), and the Beneficial Ownership Act, 2020
(Act 4 of 2020, downloaded from `investinseychelles.com` and read via
`pdftotext`, confirming its own unification of the domestic/IBC company
regimes under one beneficial-ownership register).

## Consequences

- `src/` now genuinely exists with real, tested, curl/pdftotext/
  tesseract-cited content for this blueprint's declared domain
  (`:public-sector/market-entry-compliance`) -- moves this repo's
  `manifest/itonami-fleet-audit.edn` `:prod-ready?` signal from `:stub`
  to `:active`.
- The existing `culture.facts` catalog (Wave 1, unrelated batch) is
  untouched.
- The Business Tax Act, 2009's own consolidated primary text (this
  iteration only confirmed it via a 2021 amending regulation), the
  SLA's own governing "Licenses Act, 2010" primary text, and whether
  the Economic Needs Test (ENT) has any statutory (versus purely
  policy-level, REAP 2020) basis, are genuine, disclosed,
  NOT-yet-independently-verified extension points for a future
  iteration.
- Sibling country blueprints can continue forking JPN/GRD/FSM/SYC and
  swapping in their own genuinely-researched `marketentry.facts` /
  `statute.facts` content and whichever flagship check their own law
  actually supports -- this ADR is itself further evidence that a
  DATE-shaped check family (Barbados, Grenada, Seychelles) can grow a
  third member sharing an existing polarity honestly, rather than every
  new member being required to invent an artificially distinct shape.
