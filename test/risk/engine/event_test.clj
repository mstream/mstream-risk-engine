(ns risk.engine.event-test
  (:require [clojure.test :as test]
            [clojure.spec.alpha :as spec]
            [risk.engine.state :as state]
            [risk.engine.event :as event]))


(test/deftest game-started-handler
  (test/is 
    (= "can't start a new game when an old one is still being played"
       (event/game-started-handler
         {}
         {::event/players #{"player1" "player2"}}))
    "should return failure when starting game which is being played")
  (test/is 
    (= "the game requires at least two players to play"
       (event/game-started-handler
         nil
         {::event/players #{"player1"}}))
    "should return failure when starting game with less than two players")
  (test/is 
    (let [new-state (event/game-started-handler
                      nil
                      {::event/players #{"player1" 
                                         "player2"}})]
      (and 
           (spec/valid? ::state/state new-state)
           (= event/initial-state (assoc 
                                    new-state 
                                    ::state/players []
                                    ::state/reserves {}))
           (and
                (= 42 (get (::state/reserves new-state) "player1"))
                (= 42 (get (::state/reserves new-state) "player2"))
                (or 
                    (= ["player1" "player2"] (::state/players new-state))
                    (= ["player2" "player1"] (::state/players new-state))))))
    "should return initial state with shuffled player names"))


(test/deftest territories-claimed-handler
   (test/is 
     (= "not in the distribution phase"
        (event/territories-claimed-handler
          {::state/bonuses {"group1" 1}
           ::state/connections {"territory1" #{}
                                "territory2" #{}}
           ::state/garrisons {"territory1" 1
                              "territory2" 1} 
           ::state/groups {"group1" #{"territory1"
                                      "territory2"}}
           ::state/moving-player-index 0
           ::state/ownerships {"territory1" "player1"
                               "territory2" "player2"}
           ::state/players ["player1" 
                            "player2"]
           ::state/reserves {"player1" 0
                             "player2" 0}
           ::state/territories #{"territory1"
                                 "territory2"}}
          {::event/player "player1" 
           ::event/territory-preference ["territory1"]}))
     "should return failure when no longer in the distribution phase")
  (test/is 
    (= "unknown player"
       (event/territories-claimed-handler
          {::state/bonuses {"group1" 1}
           ::state/connections {"territory1" #{}
                                "territory2" #{}}
           ::state/garrisons {"territory1" 0
                              "territory2" 0}
           ::state/groups {"group1" #{"territory1"
                                      "territory2"}}
           ::state/moving-player-index nil
           ::state/ownerships {"territory1" nil 
                               "territory2" nil}
           ::state/players ["player1" 
                            "player2"]
           ::state/reserves {"player1" 0
                             "player2" 0}
           ::state/territories #{"territory1"
                                 "territory2"}}
          {::event/player "player3" 
           ::event/territory-preference ["territory1"]}))
    "should return failure when player is unknown")
  (test/is 
    (= "unknown territory"
       (event/territories-claimed-handler
          {::state/bonuses {"group1" 1}
           ::state/connections {"territory1" #{}
                                "territory2" #{}}
           ::state/garrisons {"territory1" 0
                              "territory2" 0} 
           ::state/groups {"group1" #{"territory1"
                                      "territory2"}}
           ::state/moving-player-index nil
           ::state/ownerships {"territory1" nil 
                               "territory2" nil}
           ::state/players ["player1" 
                            "player2"]
           ::state/reserves {"player1" 0
                             "player2" 0}
           ::state/territories #{"territory1"
                                 "territory2"}}
          {::event/player "player1" 
           ::event/territory-preference ["territory3"]}))
    "should return failure when at least one territory is unknown")
  (test/is 
    (= "territory can't be claimed"
       (event/territories-claimed-handler
          {::state/bonuses {"group1" 1}
           ::state/connections {"territory1" #{}
                                "territory2" #{}
                                "territory3" #{}}
           ::state/garrisons {"territory1" 1
                              "territory2" 0
                              "territory3" 0} 
           ::state/groups {"group1" #{"territory1"
                                      "territory2"
                                      "territory3"}}
           ::state/moving-player-index nil
           ::state/ownerships {"territory1" nil 
                               "territory2" nil
                               "territory3" nil}
           ::state/players ["player1" 
                            "player2"]
           ::state/reserves {"player1" 0
                             "player2" 0}
           ::state/territories #{"territory1"
                                 "territory2"
                                 "territory3"}}
          {::event/player "player1" 
           ::event/territory-preference ["territory1"]}))
    "should return failure when at leas one of territories can't be claimed")
  (test/is 
    (= "claim already made"
       (event/territories-claimed-handler
          {::state/bonuses {"group1" 1}
           ::state/claims {"player1" ["territory1"]}
           ::state/connections {"territory1" #{}
                                "territory2" #{}}
           ::state/garrisons {"territory1" 0
                              "territory2" 0} 
           ::state/groups {"group1" #{"territory1"
                                      "territory2"}}
           ::state/moving-player-index nil
           ::state/ownerships {"territory1" nil 
                               "territory2" nil}
           ::state/players ["player1" 
                            "player2"]
           ::state/reserves {"player1" 0
                             "player2" 0}
           ::state/territories #{"territory1"
                                 "territory2"}}
         {::event/player "player1" 
          ::event/territory-preference ["territory2"]}))
    "should return failure when player has already made a claim")
  (test/is 
    (let [old-state {::state/bonuses {"group1" 1}
                     ::state/connections {"territory1" #{}
                                          "territory2" #{}}
                     ::state/garrisons {"territory1" 0
                                        "territory2" 0} 
                     ::state/groups {"group1" #{"territory1"
                                                "territory2"}}
                     ::state/moving-player-index nil
                     ::state/ownerships {"territory1" nil 
                                         "territory2" nil}
                     ::state/players ["player1" 
                                      "player2"]
                     ::state/reserves {"player1" 0
                                       "player2" 0}
                     ::state/territories #{"territory1"
                                           "territory2"}}
          new-state (event/territories-claimed-handler
                      old-state
                      {::event/player "player1" 
                       ::event/territory-preference ["territory1"]})]
      
      (and (spec/valid? ::state/state new-state)
           (= new-state (assoc old-state ::state/claims {"player1" ["territory1"]}))))
    "should make a claim when all other players haven't made theirs")
  (test/is 
    (let [old-state {::state/bonuses {"group1" 1}
                     ::state/claims {"player1" ["territory1"
                                                "territory2"
                                                "territory3"
                                                "territory4"]
                                     "player2" ["territory1"
                                                "territory2"
                                                "territory3"
                                                "territory4"]}
                     ::state/connections {"territory1" #{}
                                          "territory2" #{}
                                          "territory3" #{}
                                          "territory4" #{}}
                     ::state/garrisons {"territory1" 0
                                        "territory2" 0
                                        "territory3" 0
                                        "territory4" 0} 
                     ::state/groups {"group1" #{"territory1"
                                                "territory2"
                                                "territory3"
                                                "territory4"}}
                     ::state/moving-player-index nil
                     ::state/ownerships {"territory1" nil 
                                         "territory2" nil
                                         "territory3" nil
                                         "territory4" nil}
                     ::state/players ["player1" 
                                      "player2"
                                      "player3"]
                     ::state/reserves {"player1" 0
                                       "player2" 0
                                       "player3" 0}
                     ::state/territories #{"territory1"
                                           "territory2"
                                           "territory3"
                                           "territory4"}}
          new-state (event/territories-claimed-handler
                      old-state
                      {::event/player "player3" 
                       ::event/territory-preference ["territory4"
                                                     "territory3"
                                                     "territory2"
                                                     "territory1"]})]
      (and (spec/valid? ::state/state new-state)
           (= new-state {::state/bonuses {"group1" 1}
                         ::state/connections {"territory1" #{}
                                              "territory2" #{}
                                              "territory3" #{}
                                              "territory4" #{}}
                         ::state/garrisons {"territory1" state/min-garrison
                                            "territory2" state/min-garrison
                                            "territory3" event/neutral-territory-garrison
                                            "territory4" state/min-garrison}
                         ::state/groups {"group1" #{"territory1"
                                                    "territory2"
                                                    "territory3"
                                                    "territory4"}}
                         ::state/moving-player-index 0
                         ::state/ownerships {"territory1" "player1"
                                             "territory2" "player2"
                                             "territory3" nil
                                             "territory4" "player3"}
                         ::state/players ["player1" 
                                          "player2"
                                          "player3"]
                         ::state/reserves {"player1" event/initial-reserves
                                           "player2" event/initial-reserves
                                           "player3" event/initial-reserves}
                         ::state/territories #{"territory1"
                                               "territory2"
                                               "territory3"
                                               "territory4"}})))
    "should distribute territories according to moving order and territories preferences"))

