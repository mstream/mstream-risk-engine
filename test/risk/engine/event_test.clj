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
         {:players #{"player1" "player2"}}))
    "should return failure when starting game which is being played")
  (test/is 
    (= "the game requires at least two players to play"
       (event/game-started-handler
         nil
         {:players #{"player1"}}))
    "should return failure when starting game with less than two players")
  (test/is 
    (= "player names must be unique"
       (event/game-started-handler
         nil
         {:players ["player1" "player1"]}))
    "should return failure when starting game with non-unique player names")
  (test/is 
    (let [new-state (event/game-started-handler
                      nil
                      {:players #{"player1" "player2"}})]
      (and 
           (spec/valid? ::state/state new-state)
           (= event/initial-state (assoc 
                                    new-state 
                                    ::state/players []
                                    ::state/reserves {}))
           (or 
               (= ["player1" "player2"] (::state/players new-state))
               (= ["player2" "player1"] (::state/players new-state))
               (= 42 (get (::state/reserves new-state) "player1"))
               (= 42 (get (::state/reserves new-state) "player2")))))
    "should return initial state with shuffled player names"))


(test/deftest territory-claimed-handler
  (test/is 
    (= "state not valid"
       (event/territory-claimed-handler
         {}
         {:player "player1" :territory "territory1"}))
    "should return failure when state is invalid")
  (test/is 
    (= "territory already claimed"
       (event/territory-claimed-handler
         {::state/bonuses {"group1" 1}
          ::state/connections {"territory1" #{}}
          ::state/garrisons {"territory1" 1} 
          ::state/groups {"group1" #{"territory1"}}
          ::state/moving-player-index 0
          ::state/ownerships {"territory1" "player1"}
          ::state/players ["player1" 
                           "player2"]
          ::state/reserves {"player1" 1}
          ::state/territories #{"territory1"}}
         {:player "player1" :territory "territory1"}))
    "should return failure when territory is already owned by yourself")
  (test/is 
    (= "territory already claimed"
       (event/territory-claimed-handler
         {::state/bonuses {"group1" 1}
          ::state/connections {"territory1" #{}}
          ::state/garrisons {"territory1" 1} 
          ::state/groups {"group1" #{"territory1"}}
          ::state/moving-player-index 0
          ::state/ownerships {"territory1" "player2"}
          ::state/players ["player1" 
                           "player2"]
          ::state/reserves {"player1" 1}
          ::state/territories #{"territory1"}}
         {:player "player1" :territory "territory1"}))
    "should return failure when territory is already owned by opponents")
  (test/is 
    (= "not player's turn"
       (event/territory-claimed-handler
         {::state/bonuses {"group1" 1}
          ::state/connections {"territory1" #{}}
          ::state/garrisons {"territory1" 1} 
          ::state/groups {"group1" #{"territory1"}}
          ::state/moving-player-index 1
          ::state/ownerships {"territory1" "player2"}
          ::state/players ["player1" 
                           "player2"]
          ::state/reserves {"player1" 1}
          ::state/territories #{"territory1"}}
         {:player "player1" :territory "territory1"}))
    "should return failure when player does not have a turn"))

  


