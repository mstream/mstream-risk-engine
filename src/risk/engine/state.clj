(ns risk.engine.state
  (:require [clojure.spec.alpha :as spec]))


(spec/def ::bonuses 
  (spec/map-of string? int?))


(spec/def ::connections 
  (spec/map-of string? (spec/coll-of string?)))


(spec/def ::garrisons 
  (spec/map-of string? (spec/and int? (complement pos?))))


(spec/def ::groups 
  (spec/map-of string? (spec/coll-of string?)))


(spec/def ::moving-player-index 
  (spec/and int? (complement pos?)))


(spec/def ::ownership 
  (spec/map-of string? (spec/or 
                         :unassigned nil? 
                         :assigned string?)))


(spec/def ::players
  (spec/and
    (spec/coll-of string?)
    #(> (count %) 1)))


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
                     ::moving-player-index
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
    
