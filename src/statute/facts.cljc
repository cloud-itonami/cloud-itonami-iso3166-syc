(ns statute.facts
  "General-law compliance catalog for Seychelles (SYC) -- extends this
  repo's existing `marketentry.facts` (public-procurement market-entry
  only, narrow scope) with a second, orthogonal catalog of statutes a
  company operating in this jurisdiction must generally track for
  compliance. Mirrors cloud-itonami-iso3166-jpn/-deu/-bgr/-aze/-alb/
  -arm/-atg/-brb/-dma/-grd/-fsm's `statute.facts` (ADR-2607141700,
  cloud-itonami-compliance-fact-federation).

  Every entry cites an OFFICIAL Seychelles government-hosted URL --
  never fabricated. Two of the three PDFs below carried a genuine text
  layer and were read via `pdftotext -layout`; the Companies Ordinance
  (Chapter 40) is a large consolidated document (11,000+ lines) this
  iteration read in relevant part (short title, Registrar function,
  s.13 certificate-of-incorporation text), not word-for-word in full.
  No OCR was required for any of the three -- unlike the Seychelles
  Investment Act 2010 (see `marketentry.facts`), which this iteration
  separately confirmed IS a scanned image-only PDF requiring `tesseract`
  OCR.

  - Companies Act, Chapter 40 (Companies Ordinance, 1972, consolidated
    to 1 December 2014, downloaded directly from
    `registrationdivision.gov.sc` and read via `pdftotext`) -- confirmed
    via its own text: 'CONSOLIDATED TO 1 DECEMBER 2014 ... CHAPTER 40
    ... COMPANIES ORDINANCE, 1972', s.13 'Conclusiveness of certificate
    of incorporation' ('A certificate of incorporation given by the
    Registrar in respect of any association shall be ...'), and its own
    Interpretation section: '\"Registrar\" means the Registrar of
    Companies'. This is the DOMESTIC companies regime, administered by
    the Registration Division (Department of Legal Affairs, President's
    Office) -- GENUINELY DISTINCT from the Financial Services
    Authority's International Business Companies Act, 2016 (Act 15 of
    2016, Cap. 274) offshore regime (see `marketentry.facts` for the
    full two-regime finding).
  - Employment Act, Chapter 69 (Act 2 of 1995, '2010 edition' per its
    own cover page, downloaded directly from `employment.gov.sc` and
    read via `pdftotext`) -- confirmed via its own text: '[3rd April,
    1995] Act 2 of 1995', its own 'ARRANGEMENT OF SECTIONS' lists 'PART
    VIII. TERMINATION OF CONTRACTS' (s.51 'Redundancy of workers', s.73A
    'Employment Tribunal'). The Ministry of Employment and Human
    Resource Planning/Social Affairs's own e-library page (fetched
    directly) separately lists amendments through 'Act 19 - 2023 -
    Employment (Amendment) Act 2023', confirming the Act remains
    actively maintained.
  - Beneficial Ownership Act, 2020 (Act 4 of 2020, downloaded directly
    from `investinseychelles.com` and read via `pdftotext`) -- confirmed
    via its own text: assented 5th March 2020 by President Danny Faure,
    establishing a 'Register of Beneficial Owners' (Part II) and a
    'Beneficial ownership database' (s.13). Its own s.2(1)(a)
    Application section (read directly, own primary text) is a directly
    on-topic finding for this market-entry actor's REGISTRATION-body
    question (see `marketentry.facts`): it applies UNIFORMLY to BOTH 'a
    company ... incorporated or registered under the Companies Act'
    (Cap. 40, domestic) AND 'an international business company
    incorporated or continued or converted under the International
    Business Companies Act' (Cap. 274, FSA/offshore), among other legal
    persons/arrangements -- i.e. Seychelles' post-2018/2019 OECD/EU-
    pressure transparency reform was built to apply evenly across the
    domestic and offshore/IBC regimes, even where incorporation itself
    remains administratively split.

  A law not in this table has NO spec-basis, full stop; extend
  `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of statute entries. `:statute/url` + `:statute/law-number`
  are the citation the governor requires before any compliance-fact
  proposal referencing this law can commit."
  {"SYC"
   [{:statute/id "syc.companies-act"
     :statute/title "Companies Act"
     :statute/jurisdiction "SYC"
     :statute/kind :law
     :statute/law-number "Chapter 40 (Companies Ordinance, 1972, consolidated to 1 December 2014)"
     :statute/url "https://registrationdivision.gov.sc/wp-content/uploads/2024/11/CAP40.pdf"
     :statute/url-provenance :official-registrationdivision-gov-sc
     :statute/enacted-date "1972-01-01"
     :statute/retrieved-at "2026-07-23"
     :statute/topic #{:corporate-governance :incorporation}}
    {:statute/id "syc.employment-act"
     :statute/title "Employment Act"
     :statute/jurisdiction "SYC"
     :statute/kind :law
     :statute/law-number "Chapter 69 (Act 2 of 1995, 2010 edition, amended through Act 19 of 2023)"
     :statute/url "https://www.employment.gov.sc/e-library/acts-and-regulations/employment-acts-and-regulations/employment-act-1995-2010-edition/download"
     :statute/url-provenance :official-employment-gov-sc
     :statute/enacted-date "1995-04-03"
     :statute/retrieved-at "2026-07-23"
     :statute/topic #{:labor :employment :termination}}
    {:statute/id "syc.beneficial-ownership-act"
     :statute/title "Beneficial Ownership Act, 2020"
     :statute/jurisdiction "SYC"
     :statute/kind :law
     :statute/law-number "Act 4 of 2020"
     :statute/url "https://www.investinseychelles.com/downloads/tourism-documents/beneficial-ownership-act-2020/download"
     :statute/url-provenance :official-investinseychelles-com
     :statute/enacted-date "2020-03-05"
     :statute/retrieved-at "2026-07-23"
     :statute/topic #{:beneficial-ownership :anti-money-laundering :transparency}}]})

(defn spec-basis
  "The jurisdiction's statute vector, or nil -- nil means NO spec-basis
  for that jurisdiction yet."
  [iso3]
  (get catalog iso3))

(defn coverage
  "Honest coverage report, same shape/discipline as `marketentry.facts/coverage`:
  never report a missing jurisdiction as covered."
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-syc statute.facts Wave 0 (ADR-2607141700): "
                 (count (get catalog "SYC")) " SYC statutes seeded with an "
                 "official government-hosted citation. Extend "
                 "`statute.facts/catalog`, never fabricate a law-id or URL.")})))

(defn by-topic
  "Statutes for `iso3` tagged with `topic` (e.g. :labor, :beneficial-ownership)."
  [iso3 topic]
  (filterv #(contains? (:statute/topic %) topic) (spec-basis iso3)))
