(ns marketentry.registry
  "Pure-function market-entry filing-draft + filing-submit record
  construction -- an append-only market-entry book-of-record draft.

  Like every sibling actor's registry, there is no single international
  reference-number standard for a public-procurement market-entry
  filing -- every jurisdiction assigns its own format. This namespace
  does NOT invent one; it builds a jurisdiction-scoped sequence number
  and validates the record's required fields, the same honest,
  non-fabricating discipline `marketentry.facts` uses.

  `engagement-fee-matches-claim?` is an HONEST reapplication of the
  SAME ground-truth-recompute DISCIPLINE sibling actors use (verify a
  claimed monetary total against the entity's own recorded quantity x
  unit fields), reapplied to a market-entry engagement fee line.

  `compute-disqualification-expiry` / `director-conviction-
  disqualifying?` are THIS vertical's own ground-truth recompute,
  grounding SYC's flagship governor check
  (`marketentry.governor/director-conviction-disqualifying-
  violations`): the Public Procurement Act, 2008 (Cap. 305, Act 33 of
  2008) s.90(1)(d) (own primary text, see `marketentry.facts`)
  disqualifies a bidder, or any of its directors or officers, convicted
  of a criminal offence related to professional conduct or the making
  of false statements or misrepresentations as to their qualifications
  'within a period of THREE YEARS preceding the commencement of the
  procurement proceedings'.

  Dates are plain ISO-8601 \"YYYY-MM-DD\" strings -- deliberately no
  external date/calendar library and no host date API (`java.time` /
  `js/Date`), the SAME technique this family's Barbados/Grenada
  siblings established for their own date-shaped checks: a conviction's
  disqualifying-expiry date is computed by bumping the 4-digit year
  prefix (a calendar '3 years later, same month/day' reading of the
  statute) and compared with plain string `compare`, which sorts
  zero-padded ISO-8601 dates in chronological order.

  This is the SAME mechanical technique and the SAME backward-looking
  DISQUALIFICATION polarity as this family's Grenada (GRD) sibling's
  own flagship check -- but INDEPENDENTLY grounded in Seychelles' own
  primary text (Public Procurement Act 2008 s.90(1)(d)) with its own
  distinct numeric constant: THREE years, not GRD's two. This iteration
  deliberately does not force an artificially 'different' shape onto a
  mechanism that is, honestly, structurally similar to a sibling's --
  see `marketentry.facts`'s namespace docstring for a genuinely
  different structural finding (the Beneficial Ownership Act 2020's
  unification of the domestic/IBC company regimes) that this iteration
  chose NOT to force into the governor instead.

  This namespace is pure data + pure functions -- no I/O, no network
  call to any real Procurement Oversight Unit system. It builds the
  RECORD an operator would keep, not the act of submitting a portal
  registration itself (that is `marketentry.operation`'s
  `:filing/submit`, always human-gated -- see README Actuation)."
  (:require [kotoba.lang.text :as str]))

(defn- unsigned-certificate
  "Every certificate this actor produces is UNSIGNED -- signature is
  the market-entry operator's act, not this actor's."
  [kind subject record-id]
  {"@context" ["https://www.w3.org/ns/credentials/v2"]
   "type" ["VerifiableCredential" kind]
   "credentialSubject" {"id" subject "record" record-id}
   "proof" nil
   "issued_by_registry" false
   "status" "draft-unsigned"})

(defn- zero-pad [n w]
  (let [s (str n)]
    (str (apply str (repeat (max 0 (- w (count s))) "0")) s)))

(defn compute-engagement-fee
  "The ground-truth engagement fee for `engagement`'s own `:base-fee`
  and `:monitoring-months` x `:monthly-rate` -- a single flat
  base + months x rate calculation, not a full pricing engine."
  [{:keys [base-fee monthly-rate monitoring-months]}]
  (+ (double base-fee)
     (* (double monthly-rate) (double monitoring-months))))

(defn engagement-fee-matches-claim?
  "Does `engagement`'s own `:claimed-fee` equal the independently
  recomputed `compute-engagement-fee`?"
  [{:keys [claimed-fee] :as engagement}]
  (== (double claimed-fee) (compute-engagement-fee engagement)))

(def disqualification-window-years
  "Public Procurement Act, 2008 (Cap. 305, Act 33 of 2008) s.90(1)(d)'s
  own numeric lookback window: a conviction 'within a period of THREE
  YEARS preceding the commencement of the procurement proceedings'
  disqualifies."
  3)

(defn compute-disqualification-expiry
  "The ground-truth date on which a `conviction-date` (\"YYYY-MM-DD\")
  STOPS disqualifying a bidder (or its directors/officers) under
  s.90(1)(d) -- 3 calendar years later, same month/day."
  [conviction-date]
  (when (and conviction-date (>= (count conviction-date) 5))
    (let [year (#?(:clj Integer/parseInt :cljs js/parseInt) (subs conviction-date 0 4))
          rest (subs conviction-date 4)]
      (str (+ year disqualification-window-years) rest))))

(defn director-conviction-disqualifying?
  "Does `engagement`'s own declared `:director-conviction-date` STILL
  disqualify it under s.90(1)(d) as of its own declared
  `:submission-date` -- i.e. does `submission-date` fall ON OR BEFORE
  `conviction-date + 3 years`? A nil/missing conviction date is never
  disqualifying here (no conviction on file for this person or its
  directors/officers)."
  [{:keys [director-conviction-date submission-date]}]
  (boolean
   (when-let [expiry (compute-disqualification-expiry director-conviction-date)]
     (when submission-date
       (<= (compare submission-date expiry) 0)))))

(defn register-draft
  "Validate + construct the FILING-DRAFT registration DRAFT -- the
  market-entry operator's own act of preparing a portal registration
  package. Pure function -- does not touch any real procurement
  system."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "draft: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "draft: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "draft: sequence must be >= 0" {})))
  (let [draft-number (str (str/upper jurisdiction) "-DFT-" (zero-pad sequence 6))
        record {"record_id" draft-number
                "kind" "filing-draft"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "draft_number" draft-number
     "certificate" (unsigned-certificate "FilingDraft" draft-number draft-number)}))

(defn register-submit
  "Validate + construct the FILING-SUBMIT registration DRAFT -- the
  market-entry operator's own act of actually submitting a portal
  registration (always human-gated upstream)."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "submit: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "submit: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "submit: sequence must be >= 0" {})))
  (let [submit-number (str (str/upper jurisdiction) "-SUB-" (zero-pad sequence 6))
        record {"record_id" submit-number
                "kind" "filing-submit"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "submit_number" submit-number
     "certificate" (unsigned-certificate "FilingSubmit" submit-number submit-number)}))

(defn append [history result]
  (conj (vec history) (get result "record")))
