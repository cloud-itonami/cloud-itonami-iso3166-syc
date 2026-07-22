# Operator Guide

## First Deployment

1. Confirm the client's incorporation/legal-entity status is complete
   (route to `cloud-itonami-M6910` or local counsel first if not).
2. Register the client's intake: business type, target public function,
   prior filing history in Seychelles if any.
3. Run the advisor in read-only mode against `pou.gov.sc`, governed by
   the Public Procurement Act, 2008 (Cap. 305, Act 33 of 2008, as
   amended by Act 37 of 2021, Act 33 of 2022 and Act 15 of 2025).
4. Compare the checklist against the client's current documentation:
   - Certificate of Incorporation from the Registration Division
     (Companies Act Chapter 40) for a domestic company, OR an
     International Business Companies Act (Act 15 of 2016, Cap. 274)
     certificate from the Financial Services Authority (FSA) for an
     offshore IBC -- these are DISTINCT regimes, never conflated
   - Taxpayer Identification Number (TIN) record from the Seychelles
     Revenue Commission (SRC)
   - confirmation no director or officer carries a conviction still
     within the s.90(1)(d) three-year disqualification lookback
5. Enable gated filing-draft assistance once the Market-Entry Compliance
   Governor contract is trusted; actual submission always requires human
   sign-off.

## Minimum Production Controls

- client-owned data store for business/tax registration documents
- clear provenance (official portal/regulation citation) for every
  requirement surfaced
- approval workflow for any portal registration or filing submission
- independent re-verification that no director/officer conviction falls
  within the s.90(1)(d) three-year disqualification lookback before any
  `:filing/submit` -- never trust a self-reported "qualified" claim
- named referral relationship with Seychelles-licensed counsel or a
  registered agent for anything beyond checklist/draft assistance
- monthly audit export
- disputes/appeals route to the Procurement Oversight Unit's own review
  process (ss.94-97, Public Procurement Act 2008), not this actor -- it
  has no standing to file a complaint on the client's behalf

## Certification

Certified operators must prove data provenance, audit traceability, that
automated actions cannot bypass the Market-Entry Compliance Governor, and
a working referral relationship with Seychelles-licensed counsel or a
registered agent for whatever licensed representation the law of
Seychelles requires for actual public-procurement filings.
