(ns risk.engine.event
  (:require [clojure.set :as sets]
            [clojure.spec.alpha :as spec]            
            [cuerdas.core :as str]
            [risk.engine.core :as core]
            [risk.engine.query :as query]
            [risk.engine.state :as state]))


(defn validate-state [state]
  (when (not (spec/valid? ::state/state state))
    (throw (IllegalArgumentException. (str/format
                                        "invalid state: %s"
                                        (spec/explain-str ::state/state state))))))


(defn validate-event-data [event-data-spec event-data]
  (when (not (spec/valid? event-data-spec event-data))
    (throw (IllegalArgumentException. (str/format
                                        "invalid event data: %s"
                                        (spec/explain-str event-data-spec event-data))))))


(def neutral-territory-garrison 
  1)


(def initial-reserves 
  3)


(def initial-state
  {::state/bonuses {"Africa" 3
                    "Asia" 7
                    "Australia" 2
                    "Europe" 5
                    "North America" 5
                    "South America" 2}
   ::state/connections {"Eastern Australia" #{"Western Australia" 
                                              "New Guinea"}
                        "Western Australia" #{"Eastern Australia"
                                              "Indonesia"
                                              "New Guinea"}}
   ::state/garrisons {"Afghanistan" 0
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
   ::state/groups {"Africa" #{"Central Africa"
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
   ::state/ownerships {"Afghanistan" nil
                       "Alaska" nil
                       "Argentina" nil
                       "Brazil" nil
                       "Central Africa" nil
                       "Central America" nil
                       "Central Canada" nil
                       "China" nil
                       "East Africa" nil
                       "Eastern Australia" nil
                       "Eastern Canada" nil
                       "Eastern Europe" nil
                       "Eastern United States" nil
                       "Egypt" nil
                       "Great Britain & Ireland" nil
                       "Greenland" nil
                       "Hindustan" nil
                       "Iceland" nil
                       "Indonesia" nil
                       "Irkutks" nil
                       "Japan" nil
                       "Kamchatka" nil
                       "Madagascar" nil
                       "Middle East" nil
                       "Mongolia" nil
                       "New Guinea" nil
                       "North Africa" nil
                       "Northern Europe" nil
                       "Northwest Territory" nil
                       "Peru" nil
                       "Scandinavia" nil
                       "Siberia" nil
                       "South Africa" nil
                       "Southeast Asia" nil
                       "Southern Europe" nil
                       "Ural" nil
                       "Venezuela" nil
                       "Western Australia" nil
                       "Western Canada" nil
                       "Western Europe" nil
                       "Western United States" nil
                       "Yakutsk" nil}
   ::state/players []
   ::state/reserves {}
   ::state/territories #{"Afghanistan"
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


(spec/def ::players
  (spec/coll-of 
    ::state/player
    :kind set?))


(spec/def ::game-started-handler-event-data
  (spec/keys :req [::players]))


(spec/fdef game-started-handler
  :args (spec/cat
          :state ::state/state
          :event-data ::game-started-handler-event-data)
  :ret ::core/event-handling-result)


(defn game-started-handler [state {:keys [::players] :as event-data}]
  (cond 
    (some? state) "can't start a new game when an old one is still being played"
    (not (spec/valid? ::game-started-handler-event-data event-data)) "event data not valid"
    (< (count players) 2) "the game requires at least two players to play"
    (not= (count players) (count (set players))) "player names must be unique"
    :else (let [reserves (quot 
                           (* 2 (count (vals (::state/territories initial-state))))
                           (count players))]
            (assoc 
              initial-state
              ::state/players (vec (shuffle players))
              ::state/reserves (into 
                                 {} 
                                 (map 
                                   (fn [player]
                                     [player reserves]) 
                                   players))))))


(spec/def ::claim
  ::state/claim)


(spec/def ::player
  ::state/player)


(spec/def ::territories-claimed-handler-event-data
  (spec/keys :req [::player ::claim]))


(spec/fdef territories-claimed-handler
  :args (spec/cat
          :state ::state/state
          :event ::territories-claimed-handler-event-data)
  :ret ::core/event-handling-result)


(defn- calculate-ownerships [preferences territories]
  (sets/map-invert (reduce (fn [ownerships [player priority]]
                             (assoc 
                               ownerships 
                               player
                               (first (filter 
                                        (fn [territory]
                                          (not (contains? 
                                                 (set (vals ownerships))
                                                 territory)))
                                        (into 
                                          priority 
                                          (shuffle (sets/difference
                                                     (set priority)
                                                     (set territories))))))))
                     {}
                     preferences)))
  


(defn territories-claimed-handler [{:keys [::state/claims
                                           ::state/ownerships
                                           ::state/players
                                           ::state/territories] :as state} 
                                   {:keys [::player ::claim] :as event-data}]
  (validate-state state)
  (validate-event-data ::territories-claimed-handler-event-data event-data)
  (cond 
    (not (query/in-distribution-phase? state)) "not in the distribution phase"
    (not (some (partial = player) players)) "unknown player"
    (not (every? (partial contains? territories) claim)) "unknown territory"
    (some (comp not (partial query/can-claim-territory? state)) claim) "territory can't be claimed"
    (some? (get claims player)) "claim already made"
    (not= (count claims) 
          (dec (count players))) (assoc-in 
                                   state 
                                   [::state/claims player]
                                   claim)
    :else (-> state
              (dissoc ::state/claims)
              (assoc ::state/garrisons {"territory1" state/min-garrison
                                        "territory2" state/min-garrison
                                        "territory3" neutral-territory-garrison
                                        "territory4" state/min-garrison}
                ::state/ownerships (merge 
                                     ownerships 
                                     (calculate-ownerships
                                       (assoc 
                                         claims
                                         player
                                         claim)
                                       territories))
                ::state/reserves {"player1" initial-reserves
                                  "player2" initial-reserves
                                  "player3" initial-reserves}))))


(spec/def ::territory
  ::state/territory)


(spec/def ::quantity
  (spec/and 
    int?
    pos?))


(spec/def ::dispatchments
  (spec/map-of ::territory ::quantity))


(spec/def ::from 
  ::state/territory)


(spec/def ::to
  ::state/territory)


(spec/def ::movement
  (spec/keys :req [::from 
                   ::to 
                   ::quantity]))


(spec/def ::movements
  (spec/coll-of 
    ::movement
    :kind vector?
    :distinct true))


(spec/def ::orders
  ::state/orders)


(spec/def ::orders-dispatched-handler-event-data
  (spec/keys :req [::player 
                   ::dispatchments 
                   ::movements]))


(spec/fdef orders-dispatched-handler
  :args (spec/cat
          :state ::state/state
          :event ::orders-dispatched-handler-event-data)
  :ret ::core/event-handling-result)


(defn orders-dispatched-handler [{:keys [::state/garrisons
                                         ::state/orders
                                         ::state/ownerships
                                         ::state/players
                                         ::state/reserves
                                         ::state/territories] :as state} 
                                 {:keys [::player 
                                         ::dispatchments
                                         ::movements] :as event-data}]
  (validate-state state)
  (validate-event-data ::orders-dispatched-handler-event-data event-data)
  (cond 
    (query/in-distribution-phase? state) "not in the orders phase"
    (not (some 
           (partial = player) 
           players)) "unknown player"
    (some 
      (comp 
        not 
        (partial contains? territories)) 
      (flatten [(map first dispatchments) 
                (map ::from movements) 
                (map ::to movements)])) "unknown territory"
    (some 
      (comp 
        (partial not= player)
        (partial get ownerships)) 
      (flatten [(map first dispatchments)
                (map ::from movements)])) "not owned territory"
    (> 
       (apply + (map second dispatchments))
       (get reserves player)) "not enough reserves"
    (some 
      (partial > state/min-garrison) 
      (map 
        (fn [movement] (- 
                          (get garrisons (::from movement))
                          (::quantity movement))) 
        movements)) "not enough armies"
    (not= (count orders) 
          (dec (count players))) (assoc-in 
                                   state 
                                   [::state/orders player]
                                   {::state/dispatchments dispatchments
                                    ::state/movements (mapv 
                                                        (fn [{:keys [::from 
                                                                     ::to 
                                                                     ::quantity]}]
                                                          {::state/from from 
                                                           ::state/to to
                                                           ::state/quantity quantity})
                                                        movements)})
    :else (update 
            ::garrisons 
            (fn [garrisons] 
              (hash-map (for [[territory armies] garrisons]
                          [territory ((fnil + 0) (get dispatchments territory) (get garrisons territory))]))))))


(def event-handlers 
  {:game-started game-started-handler
   :territories-claimed territories-claimed-handler
   :orders-dispatched orders-dispatched-handler})
