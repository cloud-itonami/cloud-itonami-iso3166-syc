# Business Model: Independent Public-Sector Market-Entry & Procurement Compliance Service — Seychelles

## Classification

- Repository: `cloud-itonami-iso3166-syc`
- ISO 3166: `SYC` (Seychelles)
- Activity: public-procurement market-entry and ongoing regulatory-
  compliance navigation for an already-incorporated operator

## Customer

- an already-incorporated `cloud-itonami-cofog-{code}` /
  `cloud-itonami-isco-{code}` / `cloud-itonami-unspsc-{segment}` /
  `cloud-itonami-{ISIC}` operator wanting to bid on a Seychelles public
  contract
- a foreign SME or civic-tech vendor entering the public sector in
  Seychelles for the first time
- a `cloud-itonami-M6910` client that has just completed incorporation
  and now needs public-sector market access

## Offer

- registration walkthrough for `pou.gov.sc` (the Procurement Oversight
  Unit's own live e-procurement/tender portal, governed by the Public
  Procurement Act, 2008, Cap. 305, Act 33 of 2008), including bid
  eligibility screening against Part VIII
- business/tax registration checklist that correctly ROUTES the
  operator to the right body: the Registration Division (Companies Act
  Chapter 40) for a domestic company, OR the Financial Services
  Authority (International Business Companies Act, 2016, Cap. 274) for
  an offshore IBC -- these are DISTINCT regimes this actor never
  conflates -- followed by Taxpayer Identification Number (TIN)
  registration with the Seychelles Revenue Commission (Business Tax
  Act 2009, Cap 20, s.81)
- director/officer qualification screening: independent verification
  that no director or officer of the operator has a conviction still
  within the Public Procurement Act, 2008 s.90(1)(d) three-year
  disqualification lookback, before any filing submission
- ongoing regulatory-change monitoring subscription
- compliance-audit export package for the client's own records

## Revenue

- per-engagement market-entry fee (one-time registration + checklist
  completion)
- recurring regulatory-change monitoring subscription
- compliance-audit export package

## Trust Controls

- any actual portal registration or filing submission requires
  Market-Entry Compliance Governor clearance and always escalates to
  human sign-off (`:filing/submit` is never automated at any phase)
- a false or fabricated regulatory-requirement claim is a HARD hold that
  cannot be overridden by human approval alone -- it must be corrected
  against a cited official source first
- a director/officer conviction still within the s.90(1)(d) three-year
  disqualification lookback window, independently recomputed from the
  engagement's own declared conviction and submission dates, is a HARD
  hold on `:filing/submit` -- never trusted from a self-reported
  "qualified" claim
- this service does **not** provide legal or tax advice; characterization
  and filing on the client's behalf beyond checklist/draft assistance
  routes to Seychelles-licensed counsel or a registered agent

## Boundary with adjacent actors (read before forking)

- **`cloud-itonami-M6910`**: helps a client BECOME a legal entity
  (incorporation, ISIC 6910) -- a prior, different regulatory phase
  (company law). This blueprint assumes incorporation is already done and
  handles public-procurement market entry (a different regulatory domain).
- **`cloud-itonami-cofog-{code}`**: a jurisdiction-agnostic operator
  template for ONE public function. This blueprint is the orthogonal
  jurisdiction-specific axis -- the two compose (fork a COFOG-function
  blueprint AND this one to operate in Seychelles).
