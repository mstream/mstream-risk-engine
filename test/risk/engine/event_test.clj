
(ns risk.engine.event-test
  (:require [clojure.test :as test]
            [clojure.spec.alpha :as spec]
            [risk.engine.core :as core]
            [risk.engine.state :as state]
            [risk.engine.event :as event]))


(test/deftest game-started-handler-game-still-being-played
   (test/is
     (= "can't start a new game when an old one is still being played"
        (event/game-started-handler
          {::core/state {}
           ::core/coeffects {:rand-int (fn [n]
                                         (assert false))}}
          {::event/players ["player1" "player2"]}))
     "should return failure when starting game which is being played"))


(test/deftest game-started-handler-not-enough-players
  (test/is
    (= "the game requires at least two players to play"
       (event/game-started-handler
         {::core/state nil
          ::core/coeffects {:rand-int (fn [n]
                                        (assert false))}}
         {::event/players ["player1"]}))
    "should return failure when starting game with less than two players"))


(test/deftest game-started-handler-three-players-shift-by-zero
  (let [new-state (event/game-started-handler
                    {::core/state nil
                     ::core/coeffects {:rand-int (fn [n]
                                                   (assert (= 3 n))
                                                   0)}}
                    {::event/players ["player1"
                                      "player2"
                                      "player3"]})]
    (test/is
      (spec/valid? ::state/state new-state)
      "state should be valid")
    (test/is
     (= event/initial-state
        (assoc new-state
               ::state/players []
               ::state/reserves {}))
    "state should be same as the initial one excluding players and reserves")
    (test/is
      (= 28 (get (::state/reserves new-state) "player1"))
      "player1 should have 28 reserves")
    (test/is
      (= 28 (get (::state/reserves new-state) "player2"))
      "player2 should have 28 reserves")
    (test/is
      (= 28 (get (::state/reserves new-state) "player3"))
      "player3 should have 28 reserves")
    (test/is
     (=
      ["player1"
       "player2"
       "player3"]
      (::state/players new-state))
    "players order should not be shifted")))


(test/deftest game-started-handler-three-players-shift-by-one
  (let [new-state (event/game-started-handler
                    {::core/state nil
                     ::core/coeffects {:rand-int (fn [n]
                                                   (assert (= 3 n))
                                                   1)}}
                    {::event/players ["player1"
                                      "player2"
                                      "player3"]})]
    (test/is
      (spec/valid? ::state/state new-state)
      "state should be valid")
    (test/is
      (=
         event/initial-state
         (assoc
           new-state
           ::state/players []
           ::state/reserves {}))
      "state should be same as the initial one excluding players and reserves")
    (test/is
      (= 28 (get (::state/reserves new-state) "player1"))
      "player1 should have 28 reserves")
    (test/is
      (= 28 (get (::state/reserves new-state) "player2"))
      "player2 should have 28 reserves")
    (test/is
      (= 28 (get (::state/reserves new-state) "player3"))
      "player3 should have 28 reserves")
    (test/is
      (= ["player2"
          "player3"
          "player1"]
         (::state/players new-state))
      "players order should be shifted by one")))


(test/deftest game-started-handler-three-players-shift-by-two
  (let [new-state (event/game-started-handler
                    {::core/state nil
                     ::core/coeffects {:rand-int (fn [n]
                                                   (assert (= 3 n))
                                                   2)}}
                    {::event/players ["player1"
                                      "player2"
                                      "player3"]})]
    (test/is
      (spec/valid? ::state/state new-state) "state should be valid")
    (test/is
      (=
         event/initial-state
         (assoc
           new-state
           ::state/players []
           ::state/reserves {}))
      "state should be same as the initial one excluding players and reserves")
    (test/is
      (= 28 (get (::state/reserves new-state) "player1"))
      "player1 should have 28 reserves")
    (test/is
      (= 28 (get (::state/reserves new-state) "player2"))
         "player2 should have 28 reserves")
    (test/is
      (= 28 (get (::state/reserves new-state) "player3"))
      "player3 should have 28 reserves")
    (test/is
      (= ["player3"
          "player1"
          "player2"]
         (::state/players new-state))
      "players order should be shifted by two")))


(test/deftest territories-claimed-handler
   (test/is
     (= "not in the distribution phase"
        (event/territories-claimed-handler
          {::core/state {::state/bonuses {"group1" 1}
                         ::state/connections {"territory1" #{}
                                              "territory2" #{}}
                         ::state/garrisons {"territory1" 1
                                            "territory2" 1}
                         ::state/groups {"group1" #{"territory1"
                                                    "territory2"}}
                         ::state/ownerships {"territory1" "player1"
                                             "territory2" "player2"}
                         ::state/players ["player1"
                                          "player2"]
                         ::state/reserves {"player1" 0
                                           "player2" 0}
                         ::state/territories #{"territory1"
                                               "territory2"}}
           ::core/coeffects {}}
          {::event/player "player1"
           ::event/claim ["territory1"]}))
     "should return failure when no longer in the distribution phase")
  (test/is
    (= "unknown player"
       (event/territories-claimed-handler
         {::core/state {::state/bonuses {"group1" 1}
                        ::state/connections {"territory1" #{}
                                             "territory2" #{}}
                        ::state/garrisons {"territory1" 0
                                           "territory2" 0}
                        ::state/groups {"group1" #{"territory1"
                                                   "territory2"}}
                        ::state/ownerships {"territory1" nil
                                            "territory2" nil}
                        ::state/players ["player1"
                                         "player2"]
                        ::state/reserves {"player1" 0
                                          "player2" 0}
                        ::state/territories #{"territory1"
                                              "territory2"}}
          ::core/coeffects {}}
         {::event/player "player3"
          ::event/claim ["territory1"]}))
    "should return failure when player is unknown")
  (test/is
    (= "unknown territory"
       (event/territories-claimed-handler
         {::core/state {::state/bonuses {"group1" 1}
                        ::state/connections {"territory1" #{}
                                             "territory2" #{}}
                        ::state/garrisons {"territory1" 0
                                           "territory2" 0}
                        ::state/groups {"group1" #{"territory1"
                                                   "territory2"}}
                        ::state/ownerships {"territory1" nil
                                            "territory2" nil}
                        ::state/players ["player1"
                                         "player2"]
                        ::state/reserves {"player1" 0
                                          "player2" 0}
                        ::state/territories #{"territory1"
                                              "territory2"}}
          ::core/coeffects {}}
         {::event/player "player1"
          ::event/claim ["territory3"]}))
    "should return failure when at least one territory is unknown")
  (test/is
    (= "territory can't be claimed"
       (event/territories-claimed-handler
         {::core/state {::state/bonuses {"group1" 1}
                        ::state/connections {"territory1" #{}
                                             "territory2" #{}
                                             "territory3" #{}}
                        ::state/garrisons {"territory1" 1
                                           "territory2" 0
                                           "territory3" 0}
                        ::state/groups {"group1" #{"territory1"
                                                   "territory2"
                                                   "territory3"}}
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
          ::core/coeffects {}}
         {::event/player "player1"
          ::event/claim ["territory1"]}))
    "should return failure when at leas one of territories can't be claimed")
  (test/is
    (= "claim already made"
       (event/territories-claimed-handler
         {::core/state {::state/bonuses {"group1" 1}
                        ::state/claims {"player1" ["territory1"]}
                        ::state/connections {"territory1" #{}
                                             "territory2" #{}}
                        ::state/garrisons {"territory1" 0
                                           "territory2" 0}
                        ::state/groups {"group1" #{"territory1"
                                                   "territory2"}}
                        ::state/ownerships {"territory1" nil
                                            "territory2" nil}
                        ::state/players ["player1"
                                         "player2"]
                        ::state/reserves {"player1" 0
                                          "player2" 0}
                        ::state/territories #{"territory1"
                                              "territory2"}}
          ::core/coeffects {}}
         {::event/player "player1"
          ::event/claim ["territory2"]}))
    "should return failure when player has already made a claim")
  (test/is
    (let [old-state {::state/bonuses {"group1" 1}
                     ::state/connections {"territory1" #{}
                                          "territory2" #{}}
                     ::state/garrisons {"territory1" 0
                                        "territory2" 0}
                     ::state/groups {"group1" #{"territory1"
                                                "territory2"}}
                     ::state/ownerships {"territory1" nil
                                         "territory2" nil}
                     ::state/players ["player1"
                                      "player2"]
                     ::state/reserves {"player1" 0
                                       "player2" 0}
                     ::state/territories #{"territory1"
                                           "territory2"}}
          new-state (event/territories-claimed-handler
                      {::core/state old-state
                       ::core/coeffects {}}
                      {::event/player "player1"
                       ::event/claim ["territory1"]})]
      (and (spec/valid? ::state/state new-state)
           (= new-state (assoc old-state ::state/claims {"player1" ["territory1"]}))))
    "should make a claim when not all other players haven't made theirs")
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
                      {::core/state old-state
                       ::core/coeffects {}}
                      {::event/player "player3"
                       ::event/claim ["territory4"
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
    "should distribute territories according to moving order and claims"))


(test/deftest orders-dispatched-handler
   (test/is
     (= "not in the orders phase"
        (event/orders-dispatched-handler
          {::core/state {::state/bonuses {"group1" 1}
                         ::state/connections {"territory1" #{}
                                              "territory2" #{}}
                         ::state/garrisons {"territory1" 0
                                            "territory2" 0}
                         ::state/groups {"group1" #{"territory1"
                                                    "territory2"}}
                         ::state/ownerships {"territory1" nil
                                             "territory2" nil}
                         ::state/players ["player1"
                                          "player2"]
                         ::state/reserves {"player1" 0
                                           "player2" 0}
                         ::state/territories #{"territory1"
                                               "territory2"}}
           ::core/coeffects {}}
          {::event/player "player1"
           ::event/dispatchments {}
           ::event/movements []}))
     "should return failure when no longer in the distribution phase")
  (test/is
     (= "unknown player"
        (event/orders-dispatched-handler
          {::core/state {::state/bonuses {"group1" 1}
                         ::state/connections {"territory1" #{}
                                              "territory2" #{}}
                         ::state/garrisons {"territory1" 1
                                            "territory2" 1}
                         ::state/groups {"group1" #{"territory1"
                                                    "territory2"}}
                         ::state/ownerships {"territory1" "player1"
                                             "territory2" "player2"}
                         ::state/players ["player1"
                                          "player2"]
                         ::state/reserves {"player1" 0
                                           "player2" 0}
                         ::state/territories #{"territory1"
                                               "territory2"}}
           ::core/coeffects {}}
          {::event/player "player3"
           ::event/dispatchments {}
           ::event/movements []}))
     "should return failure when players is unknown")
  (test/is
     (= "unknown territory"
        (event/orders-dispatched-handler
          {::core/state {::state/bonuses {"group1" 1}
                         ::state/connections {"territory1" #{}
                                              "territory2" #{}}
                         ::state/garrisons {"territory1" 1
                                            "territory2" 1}
                         ::state/groups {"group1" #{"territory1"
                                                    "territory2"}}
                         ::state/ownerships {"territory1" "player1"
                                             "territory2" "player2"}
                         ::state/players ["player1"
                                          "player2"]
                         ::state/reserves {"player1" 1
                                           "player2" 0}
                         ::state/territories #{"territory1"
                                               "territory2"}}
           ::core/coeffects {}}
          {::event/player "player1"
           ::event/dispatchments {"territory3" 1}
           ::event/movements []}))
     "should return failure when dispatchment order territory is unknown")
  (test/is
     (= "not owned territory"
        (event/orders-dispatched-handler
          {::core/state {::state/bonuses {"group1" 1}
                         ::state/connections {"territory1" #{}
                                              "territory2" #{}}
                         ::state/garrisons {"territory1" 1
                                            "territory2" 1}
                         ::state/groups {"group1" #{"territory1"
                                                    "territory2"}}
                         ::state/ownerships {"territory1" "player1"
                                             "territory2" "player2"}
                         ::state/players ["player1"
                                          "player2"]
                         ::state/reserves {"player1" 1
                                           "player2" 0}
                         ::state/territories #{"territory1"
                                               "territory2"}}
           ::core/coeffects {}}
          {::event/player "player1"
           ::event/dispatchments {"territory2" 1}
           ::event/movements []}))
     "should return failure when order dispatches to not owned territory")
  (test/is
     (= "not enough reserves"
        (event/orders-dispatched-handler
          {::core/state {::state/bonuses {"group1" 1}
                         ::state/connections {"territory1" #{}
                                              "territory2" #{}}
                         ::state/garrisons {"territory1" 1
                                            "territory2" 1}
                         ::state/groups {"group1" #{"territory1"
                                                    "territory2"}}
                         ::state/ownerships {"territory1" "player1"
                                             "territory2" "player2"}
                         ::state/players ["player1"
                                          "player2"]
                         ::state/reserves {"player1" 1
                                           "player2" 0}
                         ::state/territories #{"territory1"
                                               "territory2"}}
           ::core/coefffects {}}
          {::event/player "player1"
           ::event/dispatchments {"territory1" 2}
           ::event/movements []}))
     "should return failure when order dispatches more armies that the player has in their reserves")
  (test/is
     (= "unknown territory"
        (event/orders-dispatched-handler
          {::core/state {::state/bonuses {"group1" 1}
                         ::state/connections {"territory1" #{}
                                              "territory2" #{}}
                         ::state/garrisons {"territory1" 2
                                            "territory2" 1}
                         ::state/groups {"group1" #{"territory1"
                                                    "territory2"}}
                         ::state/ownerships {"territory1" "player1"
                                             "territory2" "player2"}
                         ::state/players ["player1"
                                          "player2"]
                         ::state/reserves {"player1" 0
                                           "player2" 0}
                         ::state/territories #{"territory1"
                                               "territory2"}}
           ::core/coeffects {}}
          {::event/player "player1"
           ::event/dispatchments {}
           ::event/movements [{::event/from "territory3"
                               ::event/to "territory2"
                               ::event/quantity 1}]}))
     "should return failure when order moves armies from unknown territory")
  (test/is
     (= "unknown territory"
        (event/orders-dispatched-handler
          {::core/state {::state/bonuses {"group1" 1}
                         ::state/connections {"territory1" #{}
                                              "territory2" #{}}
                         ::state/garrisons {"territory1" 2
                                            "territory2" 1}
                         ::state/groups {"group1" #{"territory1"
                                                    "territory2"}}
                         ::state/ownerships {"territory1" "player1"
                                             "territory2" "player2"}
                         ::state/players ["player1"
                                          "player2"]
                         ::state/reserves {"player1" 0
                                           "player2" 0}
                         ::state/territories #{"territory1"
                                               "territory2"}}
           ::core/coeffects {}}
          {::event/player "player1"
           ::event/dispatchments {}
           ::event/movements [{::event/from "territory1"
                               ::event/to "territory3"
                               ::event/quantity 1}]}))
     "should return failure when order moves armies to unknown territory")
  (test/is
     (= "not owned territory"
        (event/orders-dispatched-handler
          {::core/state {::state/bonuses {"group1" 1}
                         ::state/connections {"territory1" #{}
                                              "territory2" #{}}
                         ::state/garrisons {"territory1" 2
                                            "territory2" 2}
                         ::state/groups {"group1" #{"territory1"
                                                    "territory2"}}
                         ::state/ownerships {"territory1" "player1"
                                             "territory2" "player2"}
                         ::state/players ["player1"
                                          "player2"]
                         ::state/reserves {"player1" 0
                                           "player2" 0}
                         ::state/territories #{"territory1"
                                               "territory2"}}
           ::core/coeffects {}}
          {::event/player "player1"
           ::event/dispatchments {}
           ::event/movements [{::event/from "territory2"
                               ::event/to "territory1"
                               ::event/quantity 1}]}))
     "should return failure when order moves armies from not owned territory")
  (test/is
    (= "not enough armies"
       (event/orders-dispatched-handler
         {::core/state {::state/bonuses {"group1" 1}
                        ::state/connections {"territory1" #{}
                                             "territory2" #{}}
                        ::state/garrisons {"territory1" (inc state/min-garrison)
                                           "territory2" 1}
                        ::state/groups {"group1" #{"territory1"
                                                   "territory2"}}
                        ::state/ownerships {"territory1" "player1"
                                            "territory2" "player2"}
                        ::state/players ["player1"
                                         "player2"]
                        ::state/reserves {"player1" 0
                                          "player2" 0}
                        ::state/territories #{"territory1"
                                              "territory2"}}
          ::core/coeffects {}}
         {::event/player "player1"
          ::event/dispatchments {}
          ::event/movements [{::event/from "territory1"
                              ::event/to "territory2"
                              ::event/quantity (+ 2 state/min-garrison)}]}))
    "should return failure when a move order leaves less armies than required at the owned territory")
  (test/is
    (let [old-state {::state/bonuses {"group1" 1}
                     ::state/connections {"territory1" #{}
                                          "territory2" #{}}
                     ::state/garrisons {"territory1" 2
                                        "territory2" 1}
                     ::state/groups {"group1" #{"territory1"
                                                "territory2"}}
                     ::state/ownerships {"territory1" "player1"
                                         "territory2" "player2"}
                     ::state/players ["player1"
                                      "player2"]
                     ::state/reserves {"player1" 1
                                       "player2" 0}
                     ::state/territories #{"territory1"
                                           "territory2"}}
          new-state (event/orders-dispatched-handler
                      {::core/state old-state
                       ::core/coeffects {}}
                      {::event/player "player1"
                       ::event/dispatchments {"territory1" 1}
                       ::event/movements [{::event/from "territory1"
                                           ::event/to "territory2"
                                           ::event/quantity 1}]})]
      (and (spec/valid? ::state/state new-state)
           (=
              new-state
              (assoc
                old-state
                ::state/orders
                {"player1" {::state/dispatchments {"territory1" 1}
                            ::state/movements [{::state/from "territory1"
                                                ::state/to "territory2"
                                                ::state/quantity 1}]}}))))
    "should dispatch an order when not all other players haven't dispatched theirs")
  (test/is
    (let [old-state {::state/bonuses {"group1" 1}
                     ::state/connections {"territory1" #{}
                                          "territory2" #{}}
                     ::state/garrisons {"territory1" 2
                                        "territory2" 1}
                     ::state/groups {"group1" #{"territory1"
                                                "territory2"}}
                     ::state/ownerships {"territory1" "player1"
                                         "territory2" "player2"}
                     ::state/players ["player1"
                                      "player2"]
                     ::state/reserves {"player1" 1
                                       "player2" 0}
                     ::state/territories #{"territory1"
                                           "territory2"}}
          new-state (event/orders-dispatched-handler
                      {::core/state old-state
                       ::core/coeffects {}}
                      {::event/player "player1"
                       ::event/dispatchments {"territory1" 1}
                       ::event/movements [{::event/from "territory1"
                                           ::event/to "territory2"
                                           ::event/quantity 1}]})]
      (and (spec/valid? ::state/state new-state)
           (=
              new-state
              (assoc
                old-state
                ::state/orders
                {"player1" {::state/dispatchments {"territory1" 1}
                            ::state/movements [{::state/from "territory1"
                                                ::state/to "territory2"
                                                ::state/quantity 1}]}}))))
    "should execute orders"))
