(ns risk.engine.state
  (:require [clojure.spec.alpha :as spec]))


(def min-garrison
  1)


(spec/def ::player
  string?)


(spec/def ::territory
  string?)


(spec/def ::bonuses 
  (spec/map-of string? int?))


(spec/def ::claim
  (spec/coll-of 
    ::territory
    ::kind vector?
    ::distinct true))


(spec/def ::claims 
  (spec/map-of ::player ::claim))


(spec/def ::connections 
  (spec/map-of string? (spec/coll-of string?)))


(spec/def ::garrisons 
  (spec/map-of string? (spec/and 
                         int? 
                         (complement neg?))))


(spec/def ::groups 
  (spec/map-of string? (spec/coll-of string?)))


(spec/def ::quantity
  (spec/and 
    int?
    pos?))


(spec/def ::dispatchments
  (spec/map-of ::territory ::quantity))


(spec/def ::order
  (spec/keys :req [::dispatchments
                   ::movements]))


(spec/def ::orders 
  (spec/map-of ::player ::order))


(spec/def ::ownerships 
  (spec/map-of string? (spec/or 
                         :unowned nil? 
                         :owned string?)))


(spec/def ::players
  (spec/and
    (spec/coll-of 
      ::player
      :kind vector?
      :distinct true)    
    #(> (count %) 1)))


(spec/def ::reserves
  (spec/map-of 
    string? 
    (spec/and int? (complement neg?))))


(spec/def ::territories
  (spec/coll-of ::territory))


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


(spec/def ::territories-have-connections-assgined
  (fn [{:keys [::territories ::connections]}]
    (= territories (set (keys connections)))))


(spec/def ::territories-have-garrison-assigned
  (fn [{:keys [::territories ::garrisons]}]
    (= territories (set (keys garrisons)))))


(spec/def ::territories-have-ownership-assigned
  (fn [{:keys [::territories ::ownerships]}]
    (= territories (set (keys ownerships)))))


(spec/def ::owned-territories-have-at-least-one-army-in-its-garrison
  (fn [{:keys [::ownerships ::garrisons]}]
    (every? 
      (comp 
        (partial pos?)
        #(get garrisons % 0))
      (keys
        (filter 
          (comp 
            (partial = :owned)
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
        (set players))
      (filter
        some?
        (map 
          (comp second second)
          ownerships)))))
          


(spec/def ::reserves-include-only-known-players
  (fn [{:keys [::reserves ::players]}]
    (every?
      (partial
        contains?
        (set players))
      (keys reserves)))) 


(spec/def ::enough-unclaimed-territories-during-distribution-phase
  (fn [{:keys [::garrisons
               ::ownerships
               ::players
               ::territories]}]
    (let [unclaimed-territories-cnt (count (filter 
                                             (fn [territory]
                                               (and 
                                                    (= :unowned (first (get ownerships territory)))
                                                    (zero? (get garrisons territory))))
                                             territories))]
      (or
          (zero? unclaimed-territories-cnt)
          (>= unclaimed-territories-cnt (count players))))))


(spec/def ::state 
  (spec/and 
    (spec/keys :req [::bonuses
                     ::connections 
                     ::garrisons  
                     ::groups  
                     ::ownerships
                     ::players
                     ::reserves
                     ::territories]
               :opt [::claims
                     ::orders])
    ::territories-are-assigned-to-exactly-one-group
    ::groups-have-bonus-assigned
    ::territories-have-garrison-assigned
    ::territories-have-ownership-assigned
    ::owned-territories-have-at-least-one-army-in-its-garrison
    ::connections-include-only-known-territories
    ::groups-include-only-known-territories
    ::ownerships-include-only-known-players
    ::reserves-include-only-known-players
    ::enough-unclaimed-territories-during-distribution-phase))
    
