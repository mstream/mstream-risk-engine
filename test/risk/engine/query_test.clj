(ns risk.engine.query-test
  (:require [clojure.test :as test]
            [risk.engine.state :as state]
            [risk.engine.query :as query]))

        
(test/deftest can-attack?
  (test/is 
    (query/can-attack?
      {::state/connections {"territory1" #{"territory2"}}
       ::state/garrisons {"territory1" 2
                          "territory2" 1} 
       ::state/ownerships {"territory1" "player1"
                           "territory2" "player2"}}
      "player1"
      "territory1"
      "territory2")
    "player should be able to attack other player's territory from an adjacent territory where they has more than 2 army")
  (test/is 
    (not 
      (query/can-attack?
        {::state/connections {"territory1" #{"territory3"}}
         ::state/garrisons {"territory1" 2
                            "territory2" 1} 
         ::state/ownerships {"territory1" "player1"
                             "territory2" "player2"}}
        "player1"
        "territory1"
        "territory2"))
    "player should not be able to attack non adjacent territory")
  (test/is 
    (not 
      (query/can-attack?
        {::state/connections {"territory1" #{"territory2"}}
         ::state/garrisons {"territory1" 1
                            "territory2" 1} 
         ::state/ownerships {"territory1" "player1"
                             "territory2" "player2"}}
        "player1"
        "territory1"
        "territory2"))
    "player should not be able to attack not gaving more than 1 armies")
  (test/is 
    (not 
      (query/can-attack?
        {::state/connections {"territory1" #{"territory2"}}
         ::state/garrisons {"territory1" 2
                            "territory2" 1} 
         ::state/ownerships {"territory1" "player1"
                             "territory2" "player1"}}
        "player1"
        "territory1"
        "territory2"))
    "player should not be able to attack their own territory")
  (test/is 
    (not 
      (query/can-attack?
        {::state/connections {"territory1" #{"territory2"}}
         ::state/garrisons {"territory1" 2
                            "territory2" 1} 
         ::state/ownerships {"territory1" "player2"
                             "territory2" "player2"}}
        "player1"
        "territory1"
        "territory2"))
    "player should not be able to attack from a territory not owned by them"))
        

(test/deftest reinforcement-size
  (test/is 
    (= 3
       (query/reinforcement-size
         {::state/bonuses {}
          ::state/groups {}
          ::state/ownerships {"territory1" "player1"}}
         "player1"))
    "player should get at least 3 armies of reinforcement, regardles of number of owned territories")
  (test/is 
    (= 4
       (query/reinforcement-size
         {::state/bonuses {}
          ::state/groups {}
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
                              "territory12" "player1"}}
         "player1"))
    "player should get 1 army for each three owned territories")
  (test/is 
    (= 4
       (query/reinforcement-size
         { ::state/ownerships {"territory1" "player1"
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
                               "territory14" "player1"}}
         "player1"))
    "the number of received armies is rounded down if the number of owned territories is not divisible by 3")
  (test/is 
    (= 10
       (query/reinforcement-size
         {::state/bonuses {"group1" 10}
          ::state/groups {"group1" #{"territory1" "territory2"}}
          ::state/ownerships {"territory1" "player1"
                              "territory2" "player1"}}
         "player1"))
    "player gets bonus armies of the group of which they
own all the territories")
  (test/is 
    (= 11
       (query/reinforcement-size
         {::state/bonuses {"group1" 10}
          ::state/groups {"group1" #{"territory1" "territory2"}}
          ::state/ownerships {"territory1" "player1"
                              "territory2" "player1"
                              "territory3" "player1"}}
         "player1"))
    "group bonuses are added to the number of armies for owned territories")
  (test/is 
    (= 31
       (query/reinforcement-size
         {::state/bonuses {"group1" 10
                           "group2" 20}
          ::state/groups {"group1" #{"territory1" "territory2"}
                          "group2" #{"territory3" "territory4"}}
          ::state/ownerships {"territory1" "player1"
                              "territory2" "player1"
                              "territory3" "player1"
                              "territory4" "player1"}}
         "player1"))
    "player can get more than one group bonus"))









