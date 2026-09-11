(ns marketentry.registry-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.registry :as registry]))

(deftest engagement-fee-recompute
  (let [e {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12 :claimed-fee 860000.0}]
    (is (== 860000.0 (registry/compute-engagement-fee e)))
    (is (true? (registry/engagement-fee-matches-claim? e))))
  (let [bad {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12 :claimed-fee 999000.0}]
    (is (false? (registry/engagement-fee-matches-claim? bad)))))

(deftest register-draft-and-submit
  (let [d (registry/register-draft "eng-1" "SYC" 0)
        s (registry/register-submit "eng-1" "SYC" 0)]
    (is (= "SYC-DFT-000000" (get d "draft_number")))
    (is (= "SYC-SUB-000000" (get s "submit_number")))
    (is (nil? (get-in d ["certificate" "proof"])))
    (is (= "draft-unsigned" (get-in s ["certificate" "status"])))))

(deftest register-requires-ids
  (is (thrown? Exception (registry/register-draft "" "SYC" 0)))
  (is (thrown? Exception (registry/register-submit "eng-1" "" 0))))

(deftest compute-disqualification-expiry-bumps-three-years
  (is (= "2027-06-01" (registry/compute-disqualification-expiry "2024-06-01")))
  (is (nil? (registry/compute-disqualification-expiry nil))))

(deftest director-conviction-disqualifying
  (testing "conviction date + 3 years still ON OR AFTER submission date -> disqualifying"
    (is (true? (registry/director-conviction-disqualifying?
                {:director-conviction-date "2024-06-01" :submission-date "2026-07-23"})))
    (is (true? (registry/director-conviction-disqualifying?
                {:director-conviction-date "2024-06-01" :submission-date "2027-06-01"})) "expiry date itself still disqualifying (on-or-before)"))
  (testing "submission date strictly AFTER conviction date + 3 years -> no longer disqualifying"
    (is (false? (registry/director-conviction-disqualifying?
                 {:director-conviction-date "2022-06-01" :submission-date "2026-07-23"}))))
  (testing "no conviction on file -> never disqualifying"
    (is (false? (registry/director-conviction-disqualifying?
                 {:director-conviction-date nil :submission-date "2026-07-23"})))
    (is (false? (registry/director-conviction-disqualifying? {})))))
