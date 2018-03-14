(ns risk.engine.event
  (:require [clojure.spec.alpha :as spec]
            [risk.engine.core :as core]
            [risk.engine.state :as state]))


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
   ::state/moving-player-index 0
   ::state/ownerships {}
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


(spec/fdef game-started-handler
  :args (spec/cat
          :state ::state/state
          :event ::event)
  :ret ::core/event-handling-result)


(defn game-started-handler [state {:keys [:players]}]
  (cond 
    (some? state) "can't start a new game when an old one is still being played"
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


(spec/fdef territory-claimed-handler
  :args (spec/cat
          :state ::state/state
          :event ::event)
  :ret ::core/event-handling-result)


(defn territory-claimed-handler [{:keys [::state/moving-player-index
                                         ::state/ownerships
                                         ::state/players] :as state} 
                                 {:keys [:player :territory]}]
  (cond 
    (not (spec/valid? ::state/state state)) "state not valid"
    (not= player (get players moving-player-index)) "not player's turn"
    (some? (get ownerships territory)) "territory already claimed"
    :else state))


(def event-handlers 
  {:game-started game-started-handler
   :territory-claimed territory-claimed-handler})
