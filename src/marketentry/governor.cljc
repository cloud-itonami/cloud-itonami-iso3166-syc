(ns marketentry.governor
  "Market-Entry Compliance Governor -- the independent compliance layer
  that earns the MarketEntry-LLM the right to commit. The LLM has no
  notion of Seychelles procurement law, whether a claimed engagement
  fee actually equals base + months x rate, whether the engagement's
  own declared directors/officers actually clear the Public Procurement
  Act, 2008 s.90(1)(d) three-year conviction lookback, whether a
  Taxpayer Identification Number (TIN) has been verified for a filing
  that requires it, or when a draft stops being a draft and becomes a
  real-world pou.gov.sc portal submission, so this MUST be a separate
  system able to *reject* a proposal and fall back to HOLD.

  `:itonami.blueprint/governor` is `:market-entry-compliance-governor`
  (shared family keyword on blueprints).

  This blueprint's own text (docs/business-model.md Trust Controls:
  'any actual portal registration or filing submission requires
  Market-Entry Compliance Governor clearance and always escalates to
  human sign-off'; 'a false or fabricated regulatory-requirement claim
  is a HARD hold') names exactly the checks below.

  Six checks, in priority order, ALL HARD violations: a human
  approver CANNOT override them. The confidence/actuation gate is
  SOFT: it asks a human to look (low confidence / actuation), and the
  human may approve -- but see `marketentry.phase`: for `:stake
  :actuation/draft-filing`/`:actuation/submit-filing` NO phase ever
  allows auto-commit either. Two independent layers agree that
  actuation is always a human call.

    1. Spec-basis                  -- did the jurisdiction proposal cite
                                       an OFFICIAL source
                                       (`marketentry.facts`), or invent
                                       one?
    2. Evidence incomplete         -- for `:filing/draft`/
                                       `:filing/submit`, has the
                                       jurisdiction actually been
                                       assessed with a full evidence
                                       checklist on file?
    3. Director/officer conviction
       still disqualifying           -- for `:filing/submit`,
                                       INDEPENDENTLY recompute whether
                                       the engagement's own declared
                                       `:director-conviction-date`
                                       STILL falls within the Public
                                       Procurement Act, 2008 s.90(1)(d)
                                       three-year disqualification
                                       lookback window as of its own
                                       declared `:submission-date`, and
                                       HARD-hold if so. FLAGSHIP check
                                       for this repo -- a backward-
                                       looking DISQUALIFICATION-lookback
                                       date recompute, independently
                                       grounded in Seychelles' own
                                       primary text (see
                                       `marketentry.facts`).
    4. Engagement fee mismatch     -- for `:filing/submit`,
                                       INDEPENDENTLY recompute whether
                                       the engagement's own `:claimed-
                                       fee` equals `base-fee +
                                       monthly-rate x monitoring-
                                       months` -- honest reapplication
                                       of the ground-truth-recompute
                                       discipline sibling actors use.
    5. TIN unverified               -- for `:filing/submit`, when the
                                       engagement declares
                                       `:requires-tin? true`,
                                       INDEPENDENTLY check
                                       `:tin-verified?`. CONDITIONAL on
                                       the engagement's own ground
                                       truth. Grounded in the Taxpayer
                                       Identification Number (TIN) the
                                       Seychelles Revenue Commission
                                       (SRC) issues under the Business
                                       Tax Act, 2009 (Cap 20) s.81.
    6. Confidence floor / actuation
       gate                          -- LLM confidence below threshold,
                                       OR the op is `:filing/draft`/
                                       `:filing/submit` (REAL acts)
                                       -> escalate.

  Two more guards, double-draft/double-submit prevention, are enforced
  off dedicated `:drafted?`/`:submitted?` facts (never a `:status`
  value)."
  (:require [marketentry.facts :as facts]
            [marketentry.registry :as registry]
            [marketentry.store :as store]))

(def confidence-floor 0.6)

(def high-stakes
  "Stakes grave enough to always require a human, even when clean.
  Drafting a real portal package and submitting a real portal
  registration are the two real-world actuation events this actor
  performs."
  #{:actuation/draft-filing :actuation/submit-filing})

;; ----------------------------- checks -----------------------------

(defn- spec-basis-violations
  "A `:jurisdiction/assess` (or `:filing/draft`/`:filing/submit`)
  proposal with no spec-basis citation is a HARD violation -- never
  invent a jurisdiction's market-entry requirements."
  [{:keys [op]} proposal]
  (when (contains? #{:jurisdiction/assess :filing/draft :filing/submit} op)
    (let [value (:value proposal)]
      (when (or (empty? (:cites proposal))
                (and (contains? value :spec-basis) (nil? (:spec-basis value))))
        [{:rule :no-spec-basis
          :detail "公式spec-basisの引用が無い提案は法域要件として扱えない"}]))))

(defn- evidence-incomplete-violations
  "For `:filing/draft`/`:filing/submit`, the jurisdiction's required
  registration evidence must actually be satisfied."
  [{:keys [op subject]} st]
  (when (contains? #{:filing/draft :filing/submit} op)
    (let [e (store/engagement st subject)
          assessment (store/assessment-of st subject)]
      (when-not (and assessment
                     (facts/required-evidence-satisfied?
                      (:jurisdiction e) (:checklist assessment)))
        [{:rule :evidence-incomplete
          :detail "法域の必要書類(会社/事業登録・TIN確認・事業免許等)が充足していない状態での提案"}]))))

(defn- director-conviction-disqualifying-violations
  "For `:filing/submit`, INDEPENDENTLY recompute whether the
  engagement's own declared `:director-conviction-date` STILL falls
  within the Public Procurement Act, 2008 s.90(1)(d) three-year
  disqualification lookback window as of its own declared
  `:submission-date`, and HARD-hold if so -- the flagship check this
  repo adds. Evaluated for EVERY `:filing/submit`, unconditionally (not
  gated behind a `:requires-X?` engagement flag)."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when (registry/director-conviction-disqualifying? e)
        [{:rule :director-conviction-disqualifying
          :detail (str subject " の取締役/役員の有罪判決(" (:director-conviction-date e)
                      ")が申告提出日(" (:submission-date e)
                      ")時点でなお3年間の欠格期間(s.90(1)(d))内 -- 提出提案は進められない")}]))))

(defn- engagement-fee-mismatch-violations
  "For `:filing/submit`, INDEPENDENTLY recompute whether the
  engagement's own claimed fee equals base + months x rate."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when-not (registry/engagement-fee-matches-claim? e)
        [{:rule :engagement-fee-mismatch
          :detail (str subject " の申告手数料(" (:claimed-fee e)
                      ")が独立再計算値(" (registry/compute-engagement-fee e) ")と一致しない")}]))))

(defn- tin-unverified-violations
  "For `:filing/submit`, when the engagement declares `:requires-tin?
  true`, INDEPENDENTLY check `:tin-verified?` -- CONDITIONAL on the
  engagement's own ground truth. Grounded in the Taxpayer
  Identification Number (TIN) the Seychelles Revenue Commission (SRC)
  issues under the Business Tax Act, 2009 (Cap 20) s.81."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when (and (true? (:requires-tin? e))
                 (not (true? (:tin-verified? e))))
        [{:rule :tin-unverified
          :detail (str subject " はTIN(Taxpayer Identification Number)確認を要するが未確認 -- 提出提案は進められない")}]))))

(defn- already-drafted-violations
  "For `:filing/draft`, refuses to draft the SAME engagement twice."
  [{:keys [op subject]} st]
  (when (= op :filing/draft)
    (when (store/engagement-already-drafted? st subject)
      [{:rule :already-drafted
        :detail (str subject " は既にドラフト済み")}])))

(defn- already-submitted-violations
  "For `:filing/submit`, refuses to submit the SAME engagement twice."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (when (store/engagement-already-submitted? st subject)
      [{:rule :already-submitted
        :detail (str subject " は既に提出済み")}])))

(defn check
  "Censors a MarketEntry-LLM proposal against the governor rules.
  Returns {:ok? bool :violations [..] :confidence c :escalate? bool
  :high-stakes? bool :hard? bool}."
  [request _context proposal st]
  (let [hard (into []
                   (concat (spec-basis-violations request proposal)
                           (evidence-incomplete-violations request st)
                           (director-conviction-disqualifying-violations request st)
                           (engagement-fee-mismatch-violations request st)
                           (tin-unverified-violations request st)
                           (already-drafted-violations request st)
                           (already-submitted-violations request st)))
        conf (:confidence proposal 0.0)
        low? (< conf confidence-floor)
        stakes? (boolean (high-stakes (:stake proposal)))
        hard? (boolean (seq hard))]
    {:ok?          (and (not hard?) (not low?) (not stakes?))
     :violations   hard
     :confidence   conf
     :hard?        hard?
     :escalate?    (and (not hard?) (or low? stakes?))
     :high-stakes? stakes?}))

(defn hold-fact
  "The audit fact written when a proposal is rejected (HOLD)."
  [request context verdict]
  {:t          :governor-hold
   :op         (:op request)
   :actor      (:actor-id context)
   :subject    (:subject request)
   :disposition :hold
   :basis      (mapv :rule (:violations verdict))
   :violations (:violations verdict)
   :confidence (:confidence verdict)})
