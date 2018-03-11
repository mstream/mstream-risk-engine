(ns risk.engine.event-test
  (:require [clojure.test :as test]
            [clojure.spec.alpha :as spec]
            [risk.engine.state :as state]
            [risk.engine.event :as event]))


(test/deftest handle-event
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
  


