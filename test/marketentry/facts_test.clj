(ns marketentry.facts-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.facts :as facts]))

(deftest syc-has-spec-basis
  (let [sb (facts/spec-basis "SYC")]
    (is (some? sb))
    (is (string? (:provenance sb)))
    (is (seq (:required-evidence sb)))
    (is (some? (facts/corporate-number-spec-basis "SYC")))
    (is (some? (facts/business-registration-spec-basis "SYC")))
    (is (some? (facts/director-conviction-window-spec-basis "SYC")))))

(deftest syc-rep-spec-basis-is-populated
  (testing "s.94(3) suspension min/max duration -- genuinely populated"
    (is (some? (facts/rep-spec-basis "SYC")))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ"))))

(deftest required-evidence-satisfied
  (let [sb (facts/spec-basis "SYC")
        all (:required-evidence sb)]
    (is (true? (facts/required-evidence-satisfied? "SYC" all)))
    (is (not (facts/required-evidence-satisfied? "SYC" (take 1 all))))
    (is (nil? (facts/required-evidence-satisfied? "ATL" all)))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["SYC" "USA" "ATL"])]
    (is (= 3 (:requested c)))
    (is (= 2 (:covered c)))
    (is (= ["ATL"] (:missing-jurisdictions c)))))
