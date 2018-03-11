(ns risk.engine.state
  (:require [clojure.spec.alpha :as spec]))


(spec/def ::bonuses 
  (spec/map-of string? int?))


(spec/def ::connections 
  (spec/map-of string? (spec/coll-of string?)))


(spec/def ::garrisons 
  (spec/map-of string? int?))


(spec/def ::groups 
  (spec/map-of string? (spec/coll-of string?)))


(spec/def ::ownership 
  (spec/map-of string? (spec/or 
                         :unassigned nil? 
                         :assigned string?)))


(spec/def ::players
  (spec/coll-of string?))


(spec/def ::territories
  (spec/coll-of string?))


(spec/def ::territories-are-assigned-to-exactly-one-group
  (fn [{:keys [::territories ::groups]}]
    (every? (partial = 1)
      (map 
        (fn [territory]
          (count 
            (filter 
              (fn [group-territories]
                (contains? group-territories territory))
              (vals groups))))
        territories))))
  

(spec/def ::groups-have-bonus-assigned
  (fn [{:keys [::groups ::bonuses]}]
    (= (keys groups) (keys bonuses))))


(spec/def ::territories-have-garrison-assigned
  (fn [{:keys [::territories ::garrisons]}]
    (= territories (set (keys garrisons)))))



(spec/def ::owned-territories-have-at-least-one-army-in-its-garrison
  (fn [{:keys [::ownerships ::garrisons]}]
    (every? 
      (comp 
        (partial pos?)
        #(get garrisons % 0))
      (keys
        (filter 
          (comp 
            (partial some?)
            second)
          ownerships)))))


(spec/def ::connections-include-only-known-territories
  (fn [{:keys [::connections ::territories]}]
    (every?
      (partial
        contains?
        territories)
      (reduce
        (fn [r x] (into r (conj (second x) (first x))))
        []
        connections))))


(spec/def ::groups-include-only-known-territories
  (fn [{:keys [::groups ::territories]}]
    (every?
      (partial
        contains?
        territories)
      (reduce
        (fn [r x] (into r (second x)))
        []
        groups))))


(spec/def ::ownerships-include-only-known-players
  (fn [{:keys [::ownerships ::players]}]
    (every?
      (partial
        contains?
        players)
      (reduce
        (fn [r x] (into r (second x)))
        []
        ownerships))))


(spec/def ::state 
  (spec/and 
    (spec/keys :req [::bonuses
                     ::connections 
                     ::garrisons  
                     ::groups  
                     ::ownerships
                     ::players
                     ::territories])
    ::territories-are-assigned-to-exactly-one-group
    ::groups-have-bonus-assigned
    ::territories-have-garrison-assigned
    ::owned-territories-have-at-least-one-army-in-its-garrison
    ::connections-include-only-known-territories
    ::groups-include-only-known-territories
    ::ownerships-include-only-known-players))


(def initial-state
  {::bonuses {"Africa" 3
              "Asia" 7
              "Australia" 2
              "Europe" 5
              "North America" 5
              "South America" 2}
   ::connections {"Eastern Australia" #{"Western Australia" 
                                        "New Guinea"}
                  "Western Australia" #{"Eastern Australia"
                                        "Indonesia"
                                        "New Guinea"}}
   ::garrisons {"Afghanistan" 0
                   "Alaska" 0
                   "Argentina" 0
                   "Brazil" 0
                   "Central Africa" 0
                   "Central America" 0
                   "Central Canada" 0
                   "China" 0
                   "East Africa" 0
                   "Eastern Australia" 0
                   "Eastern Canada" 0
                   "Eastern Europe" 0
                   "Eastern United States" 0
                   "Egypt" 0
                   "Great Britain & Ireland" 0
                   "Greenland" 0
                   "Hindustan" 0
                   "Iceland" 0
                   "Indonesia" 0
                   "Irkutks" 0
                   "Japan" 0
                   "Kamchatka" 0
                   "Madagascar" 0
                   "Middle East" 0
                   "Mongolia" 0
                   "New Guinea" 0
                   "North Africa" 0
                   "Northern Europe" 0
                   "Northwest Territory" 0
                   "Peru" 0
                   "Scandinavia" 0
                   "Siberia" 0
                   "South Africa" 0
                   "Southeast Asia" 0
                   "Southern Europe" 0
                   "Ural" 0
                   "Venezuela" 0
                   "Western Australia" 0
                   "Western Canada" 0
                   "Western Europe" 0
                   "Western United States" 0
                   "Yakutsk" 0} 
   ::groups {"Africa" #{"Central Africa"
                        "East Africa"
                        "Egypt"
                        "Madagascar"
                        "North Africa"
                        "South Africa"}
             "Asia" #{"Afghanistan"
                      "China"
                      "Hindustan"
                      "Irkutks"
                      "Japan"
                      "Kamchatka"
                      "Middle East"
                      "Mongolia"
                      "Siberia"
                      "Southeast Asia"
                      "Ural"
                      "Yakutsk"}
             "Australia" #{"Eastern Australia"
                           "Indonesia"
                           "New Guinea"
                           "Western Australia"}
             "Europe" #{"Great Britain & Ireland"
                        "Iceland"
                        "Northern Europe"
                        "Scandinavia"
                        "Southern Europe"
                        "Eastern Europe"
                        "Western Europe"}
             "North America" #{"Alaska"
                               "Central America"
                               "Central Canada"
                               "Eastern Canada"
                               "Eastern United States"
                               "Greenland"
                               "Northwest Territory"
                               "Western Canada"
                               "Western United States"}
             "South America" #{"Argentina"
                               "Brazil"
                               "Peru"
                               "Venezuela"}}
   ::ownerships {}
   ::players #{}
   ::territories #{"Afghanistan"
                   "Alaska"
                   "Argentina"
                   "Brazil"
                   "Central Africa"
                   "Central America"
                   "Central Canada"
                   "China"
                   "East Africa"
                   "Eastern Australia"
                   "Eastern Canada"
                   "Eastern Europe"
                   "Eastern United States"
                   "Egypt"
                   "Great Britain & Ireland"
                   "Greenland"
                   "Hindustan"
                   "Iceland"
                   "Indonesia"
                   "Irkutks"
                   "Japan"
                   "Kamchatka"
                   "Madagascar"
                   "Middle East"
                   "Mongolia"
                   "New Guinea"
                   "North Africa"
                   "Northern Europe"
                   "Northwest Territory"
                   "Peru"
                   "Scandinavia"
                   "Siberia"
                   "South Africa"
                   "Southeast Asia"
                   "Southern Europe"
                   "Ural"
                   "Venezuela"
                   "Western Australia"
                   "Western Canada"
                   "Western Europe"
                   "Western United States"
                   "Yakutsk"}})
    
