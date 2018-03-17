(ns risk.engine.query-test
  (:require [clojure.test :as test]
            [risk.engine.state :as state]
            [risk.engine.query :as query]))

        
(test/deftest can-attack?
  (test/is 
    (query/can-attack?
      {::state/bonuses {"group1" 1}
       ::state/connections {"territory1" #{"territory2"}}
       ::state/garrisons {"territory1" 2
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
      "player1"
      "territory1"
      "territory2")
    "player should be able to attack other player's territory from an adjacent territory where they has more than 2 army")
  (test/is 
    (not 
      (query/can-attack?
        {::state/bonuses {"group1" 1}
         ::state/connections {"territory1" #{"territory2"}}
         ::state/garrisons {"territory1" 2
                            "territory2" 1} 
         ::state/groups {"group1" #{"territory1" 
                                    "territory2"}}
         ::state/moving-player-index 1
         ::state/ownerships {"territory1" "player1"
                             "territory2" "player2"}
         ::state/players ["player1" 
                          "player2"]
         ::state/reserves {"player1" 0
                           "player2" 0}
         ::state/territories #{"territory1" 
                               "territory2"}}
        "player1"
        "territory1"
        "territory2"))
    "player should not be able to attack when it's not their turn")
  (test/is 
    (not 
      (query/can-attack?
        {::state/bonuses {"group1" 1}
         ::state/connections {"territory1" #{}
                              "territory2" #{}}
         ::state/garrisons {"territory1" 2
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
        "player1"
        "territory1"
        "territory2"))
    "player should not be able to attack non adjacent territory")
  (test/is 
    (not 
      (query/can-attack?
        {::state/bonuses {"group1" 1}
         ::state/connections {"territory1" #{"territory2"}}
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
        "player1"
        "territory1"
        "territory2"))
    "player should not be able to attack not having more than 1 armies")
  (test/is 
    (not 
      (query/can-attack?
        {::state/bonuses {"group1" 1}
         ::state/connections {"territory1" #{"territory2"}}
         ::state/garrisons {"territory1" 2
                            "territory2" 1} 
         ::state/groups {"group1" #{"territory1" 
                                    "territory2"}}
         ::state/moving-player-index 0
         ::state/ownerships {"territory1" "player1"
                             "territory2" "player1"}
         ::state/players ["player1" 
                          "player2"]
         ::state/reserves {"player1" 0
                           "player2" 0}
         ::state/territories #{"territory1" 
                               "territory2"}}
        "player1"
        "territory1"
        "territory2"))
    "player should not be able to attack their own territory")
  (test/is 
    (not 
      (query/can-attack?
        {::state/bonuses {"group1" 1}
         ::state/connections {"territory1" #{"territory2"}}
         ::state/garrisons {"territory1" 2
                            "territory2" 1} 
         ::state/groups {"group1" #{"territory1" 
                                    "territory2"}}
         ::state/moving-player-index 0
         ::state/ownerships {"territory1" "player2"
                             "territory2" "player2"}
         ::state/players ["player1" 
                          "player2"]
         ::state/reserves {"player1" 0
                           "player2" 0}
         ::state/territories #{"territory1" 
                               "territory2"}}
        "player1"
        "territory1"
        "territory2"))
    "player should not be able to attack from a territory not owned by them"))
        

(test/deftest get-winner
  (test/is 
    (= "player1"
       (query/get-winner
         {::state/bonuses {"group1" 1}
          ::state/connections {"territory1" #{}}
          ::state/garrisons {"territory1" 1
                             "territory2" 1} 
          ::state/groups {"group1" #{"territory1" 
                                     "territory2"}}
          ::state/moving-player-index 0
          ::state/ownerships {"territory1" "player1"
                              "territory2" "player1"}
          ::state/players ["player1" 
                           "player2"]
          ::state/reserves {"player1" 0
                            "player2" 0}
          ::state/territories #{"territory1" 
                                "territory2"}}))
    "player wins if where no other player's territories")
  (test/is 
    (nil?
       (query/get-winner
         {::state/bonuses {"group1" 1}
          ::state/connections {"territory1" #{}}
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
                                "territory2"}}))
    "no one wins when there are territories owned by multiple players")
  (test/is 
    (nil?
       (query/get-winner
         {::state/bonuses {"group1" 1}
          ::state/connections {"territory1" #{}}
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
                                "territory2"}}))
    "no one wins when there are no owned territories"))


(test/deftest reinforcement-size
  (test/is 
    (= 3
       (query/reinforcement-size
         {::state/bonuses {"group1" 1}
          ::state/connections {"territory1" #{}}
          ::state/garrisons {"territory1" 1
                             "territory2" 1} 
          ::state/groups {"group1" #{"territory1" 
                                     "territory2"}}
          ::state/moving-player-index 0
          ::state/ownerships {"territory1" "player1"
                              "territory2" nil}
          ::state/players ["player1"
                           "player2"]
          ::state/reserves {"player1" 0
                            "player2" 0}
          ::state/territories #{"territory1" 
                                "territory2"}}
         "player1"))
    "player should get at least 3 armies of reinforcement, regardles of number of owned territories")
  (test/is 
    (= 4
       (query/reinforcement-size
         {::state/bonuses {"group1" 1}
          ::state/connections {"territory1" #{}
                               "territory2" #{}
                               "territory3" #{}
                               "territory4" #{}
                               "territory5" #{}
                               "territory6" #{}
                               "territory7" #{}
                               "territory8" #{}
                               "territory9" #{}
                               "territory10" #{}
                               "territory11" #{}
                               "territory12" #{}
                               "territory13" #{}}
          ::state/garrisons {"territory1" 1
                             "territory2" 1
                             "territory3" 1
                             "territory4" 1
                             "territory5" 1
                             "territory6" 1
                             "territory7" 1
                             "territory8" 1
                             "territory9" 1
                             "territory10" 1
                             "territory11" 1
                             "territory12" 1
                             "territory13" 1} 
          ::state/groups {"group1" #{"territory1" 
                                     "territory2"
                                     "territory3"
                                     "territory4"
                                     "territory5"
                                     "territory6"
                                     "territory7"
                                     "territory8"
                                     "territory9"
                                     "territory10"
                                     "territory11"
                                     "territory12"
                                     "territory13"}}
          ::state/moving-player-index 0
          ::state/ownerships {"territory1" "player1"
                              "territory2" "player1"
                              "territory3" "player1"
                              "territory4" "player1"
                              "territory5" "player1"
                              "territory6" "player1"
                              "territory7" "player1"
                              "territory8" "player1"
                              "territory9" "player1"
                              "territory10" "player1"
                              "territory11" "player1"
                              "territory12" "player1"
                              "territory13" nil}
          ::state/players ["player1"
                           "player2"]
          ::state/reserves {"player1" 0
                            "player2" 0}
          ::state/territories #{"territory1"
                                "territory2"
                                "territory3"
                                "territory4"
                                "territory5"
                                "territory6"
                                "territory7"
                                "territory8"
                                "territory9"
                                "territory10"
                                "territory11"
                                "territory12"
                                "territory13"}}
         "player1"))
    "player should get 1 army for each three owned territories")
  (test/is 
    (= 4
       (query/reinforcement-size
         {::state/bonuses {"group1" 1}
          ::state/connections {"territory1" #{}
                               "territory2" #{}
                               "territory3" #{}
                               "territory4" #{}
                               "territory5" #{}
                               "territory6" #{}
                               "territory7" #{}
                               "territory8" #{}
                               "territory9" #{}
                               "territory10" #{}
                               "territory11" #{}
                               "territory12" #{}
                               "territory13" #{}
                               "territory14" #{}
                               "territory15" #{}}
          ::state/garrisons {"territory1" 1
                             "territory2" 1
                             "territory3" 1
                             "territory4" 1
                             "territory5" 1
                             "territory6" 1
                             "territory7" 1
                             "territory8" 1
                             "territory9" 1
                             "territory10" 1
                             "territory11" 1
                             "territory12" 1
                             "territory13" 1
                             "territory14" 1
                             "territory15" 1} 
          ::state/groups {"group1" #{"territory1" 
                                     "territory2"
                                     "territory3"
                                     "territory4"
                                     "territory5"
                                     "territory6"
                                     "territory7"
                                     "territory8"
                                     "territory9"
                                     "territory10"
                                     "territory11"
                                     "territory12"
                                     "territory13"
                                     "territory14"
                                     "territory15"}}
          ::state/moving-player-index 0
          ::state/ownerships {"territory1" "player1"
                              "territory2" "player1"
                              "territory3" "player1"
                              "territory4" "player1"
                              "territory5" "player1"
                              "territory6" "player1"
                              "territory7" "player1"
                              "territory8" "player1"
                              "territory9" "player1"
                              "territory10" "player1"
                              "territory11" "player1"
                              "territory12" "player1"
                              "territory13" "player1"
                              "territory14" "player1"
                              "territory15" nil}
          ::state/players ["player1"
                           "player2"]
          ::state/reserves {"player1" 0
                            "player2" 0}
          ::state/territories #{"territory1"
                                "territory2"
                                "territory3"
                                "territory4"
                                "territory5"
                                "territory6"
                                "territory7"
                                "territory8"
                                "territory9"
                                "territory10"
                                "territory11"
                                "territory12"
                                "territory13"
                                "territory14"
                                "territory15"}}
         "player1"))
    "the number of received armies is rounded down if the number of owned territories is not divisible by 3")
  (test/is 
    (= 10
       (query/reinforcement-size
         {::state/bonuses {"group1" 10}
          ::state/connections {"territory1" #{}}
          ::state/garrisons {"territory1" 1} 
          ::state/groups {"group1" #{"territory1"}}
          ::state/moving-player-index 0
          ::state/ownerships {"territory1" "player1"}
          ::state/players ["player1"
                           "player2"]
          ::state/reserves {"player1" 0
                            "player2" 0}
          ::state/territories #{"territory1"}}
         "player1"))
    "player gets bonus armies of the group of which they own all the territories")
  (test/is 
    (= 11
       (query/reinforcement-size
         {::state/bonuses {"group1" 10}
          ::state/connections {"territory1" #{}
                               "territory2" #{}
                               "territory3" #{}}
          ::state/garrisons {"territory1" 1
                             "territory2" 1
                             "territory3" 1} 
          ::state/groups {"group1" #{"territory1" 
                                     "territory2"
                                     "territory3"}}
          ::state/moving-player-index 0
          ::state/ownerships {"territory1" "player1"
                              "territory2" "player1"
                              "territory3" "player1"}
          ::state/players ["player1"
                           "player2"]
          ::state/reserves {"player1" 0
                            "player2" 0}
          ::state/territories #{"territory1" 
                                "territory2"
                                "territory3"}}
         "player1"))
    "group bonuses are added to the number of armies for owned territories")
  (test/is 
    (= 30
       (query/reinforcement-size
         {::state/bonuses {"group1" 10
                           "group2" 20}
          ::state/connections {"territory1" #{}}
          ::state/garrisons {"territory1" 1
                             "territory2" 1} 
          ::state/groups {"group1" #{"territory1"}
                          "group2" #{"territory2"}}
          ::state/moving-player-index 0
          ::state/ownerships {"territory1" "player1"
                              "territory2" "player1"}
          ::state/players ["player1"
                           "player2"]
          ::state/reserves {"player1" 0
                            "player2" 0}
          ::state/territories #{"territory1"
                                "territory2"}}
         "player1"))
    "player can get more than one group bonus"))


(test/deftest in-distribution-phase?
  (test/is 
    (query/in-distribution-phase?
      {::state/bonuses {"group1" 1}
       ::state/connections {"territory1" #{}
                            "territory2" #{}}
       ::state/garrisons {"territory1" 0
                          "territory2" 0} 
       ::state/groups {"group1" #{"territory1" "territory2"}}
       ::state/moving-player-index nil
       ::state/ownerships {"territory1" nil
                           "territory2" nil}
       ::state/players ["player1" 
                        "player2"]
       ::state/reserves {"player1" 0
                         "player2" 0}
       ::state/territories #{"territory1" 
                             "territory2"}})
    "state is in distribution phase if there are unclaimed terriotries")
  (test/is
    (not
      (query/in-distribution-phase?
        {::state/bonuses {"group1" 1}
         ::state/connections {"territory1" #{}
                              "territory2" #{}}
         ::state/garrisons {"territory1" 1
                            "territory2" 1} 
         ::state/groups {"group1" #{"territory1" "territory2"}}
         ::state/moving-player-index nil
         ::state/ownerships {"territory1" "player1"
                             "territory2" nil}
         ::state/players ["player1" 
                          "player2"]
         ::state/reserves {"player1" 0
                           "player2" 0}
         ::state/territories #{"territory1" 
                               "territory2"}}))
    "state is not in distribution phase if there only unowned territories have some armies")
  (test/is
    (not
      (query/in-distribution-phase?
        {::state/bonuses {"group1" 1}
         ::state/connections {"territory1" #{}
                              "territory2" #{}}
         ::state/garrisons {"territory1" 1
                            "territory2" 1} 
         ::state/groups {"group1" #{"territory1" "territory2"}}
         ::state/moving-player-index nil
         ::state/ownerships {"territory1" "player1"
                             "territory2" "player2"}
         ::state/players ["player1" 
                          "player2"]
         ::state/reserves {"player1" 0
                           "player2" 0}
         ::state/territories #{"territory1" "territory2"}}))
   "state is not in distribution phase if there are no unowned territories"))

