(ns culture.facts
  "Country-level regional-culture catalog for Seychelles (SYC) -- national
  dishes, protected products, beverages, crafts, festivals and heritage
  sites, per ADR-2607171400 addendum 2 (cloud-itonami-municipality-
  culture-catalog Wave 1, in com-junkawasaki/root). Sibling namespace to
  `marketentry.facts` / `statute.facts` (ADR-2607141700); city-level
  counterparts live in the cloud-itonami-municipality-* repos.

  Catalog is keyed by UPPERCASE ISO3 (mirrors `statute.facts`); entries
  carry no :culture/municipality (that attribute is city-level only).

  Every entry cites a source URL that was actually fetched and read on
  :culture/retrieved-at -- never fabricated. Summaries state only what the
  cited source confirms. An item not in this table has NO spec-basis, full
  stop; extend `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of culture entries."
  {"SYC"
   [{:culture/id "syc.dish.ladob"
     :culture/name "Ladob"
     :culture/country "SYC"
     :culture/kind :dish
     :culture/summary "Seychellois dish eaten either savoury or as a dessert; the sweet version combines ripe plantain and sweet potatoes with coconut milk, sugar, nutmeg and vanilla."
     :culture/url "https://en.wikipedia.org/wiki/Ladob"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "syc.dish.kat-kat-banane"
     :culture/name "Kat-kat banane"
     :culture/country "SYC"
     :culture/kind :dish
     :culture/summary "Seychellois dish of green bananas and fish cooked in coconut milk."
     :culture/url "https://en.wikipedia.org/wiki/Seychellois_cuisine"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "syc.dish.kari-koko"
     :culture/name "Kari koko"
     :culture/country "SYC"
     :culture/kind :dish
     :culture/summary "Seychellois coconut curry made with various proteins."
     :culture/url "https://en.wikipedia.org/wiki/Seychellois_cuisine"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "syc.product.coco-de-mer"
     :culture/name "Coco de mer"
     :culture/country "SYC"
     :culture/kind :product
     :culture/summary "Lodoicea maldivica palm species endemic to the Seychelles islands of Praslin and Curieuse, producing the largest seed in the plant kingdom."
     :culture/url "https://en.wikipedia.org/wiki/Lodoicea"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "syc.festival.moutya"
     :culture/name "Moutya"
     :culture/name-local "Moutia"
     :culture/country "SYC"
     :culture/kind :festival
     :culture/summary "Traditional Seychellois dance similar to Sega, danced to drums made from dried goatskin with songs describing daily life; inscribed on UNESCO's Intangible Cultural Heritage List of Humanity in 2021."
     :culture/url "https://en.wikipedia.org/wiki/Montea"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "syc.festival.sega"
     :culture/name "Sega"
     :culture/country "SYC"
     :culture/kind :festival
     :culture/summary "Music and dance genre popular in Seychelles (as well as Mauritius, Agalega and Rodrigues), with the Seychellois form's music and dances differing from other islands."
     :culture/url "https://en.wikipedia.org/wiki/Sega_(genre)"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "syc.heritage.aldabra-atoll"
     :culture/name "Aldabra Atoll"
     :culture/country "SYC"
     :culture/kind :heritage
     :culture/summary "One of two UNESCO World Heritage Sites in Seychelles, designated on 19 November 1982 and managed by the Seychelles Islands Foundation."
     :culture/url "https://en.wikipedia.org/wiki/Aldabra"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "syc.heritage.vallee-de-mai"
     :culture/name "Vallée de Mai"
     :culture/country "SYC"
     :culture/kind :heritage
     :culture/summary "Nature reserve on Praslin, Seychelles, home of the coco de mer palm; inscribed by UNESCO as a World Heritage Site in 1983, one of the organization's smallest natural sites."
     :culture/url "https://en.wikipedia.org/wiki/Vall%C3%A9e_de_Mai"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}]})

(defn spec-basis [iso3] (get catalog iso3))

(defn coverage
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-syc culture catalog "
                 "(ADR-2607171400 addendum 2, Wave 1): " (count (get catalog "SYC"))
                 " SYC entries, each with a fetched-and-read citation. "
                 "Extend `culture.facts/catalog`, never fabricate an id/url.")})))

(defn by-kind [iso3 kind]
  (filterv #(= (:culture/kind %) kind) (spec-basis iso3)))
