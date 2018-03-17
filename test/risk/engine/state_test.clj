(ns risk.engine.state-test
  (:require [clojure.test :as test]
            [clojure.spec.alpha :as spec]
            [cuerdas.core :as str]            
            [risk.engine.state :as state]))


(test/deftest state-spec
  (test/is
    (not
      (spec/valid? 
        ::state/state
        {::state/connections {}
         ::state/garrisons {} 
         ::state/groups {}
         ::state/moving-player-index 0
         ::state/ownerships {}
         ::state/players []
         ::state/reserves {}
         ::state/territories #{}}))
    "spec should fail on a state with bonuses key missing")
  (test/is
    (not
      (spec/valid? 
        ::state/state
        {::state/bonuses {}
         ::state/garrisons {} 
         ::state/groups {}
         ::state/moving-player-index 0
         ::state/ownerships {}
         ::state/players []
         ::state/reserves {}
         ::state/territories #{}}))
    "spec should fail on a state with connections key missing")
  (test/is
    (not
      (spec/valid? 
        ::state/state
        {::state/bonuses {}
         ::state/connections {}
         ::state/groups {}
         ::state/moving-player-index 0
         ::state/ownerships {}
         ::state/players []
         ::state/reserves {}
         ::state/territories #{}}))
    "spec should fail on a state with garrisons key missing")
  (test/is
    (not
      (spec/valid? 
        ::state/state
        {::state/bonuses {}
         ::state/connections {}
         ::state/garrisons {} 
         ::state/moving-player-index 0
         ::state/ownerships {}
         ::state/players []
         ::state/reserves {}
         ::state/territories #{}}))
    "spec should fail on a state with groups key missing")
  (test/is
    (not
      (spec/valid? 
        ::state/state
        {::state/bonuses {}
         ::state/connections {}
         ::state/garrisons {} 
         ::state/groups {}
         ::state/ownerships {}
         ::state/players []
         ::state/reserves {}
         ::state/territories #{}}))
    "spec should fail on a state with moving-player-index key missing")
  (test/is
    (not
      (spec/valid? 
        ::state/state
        {::state/bonuses {}
         ::state/connections {}
         ::state/garrisons {} 
         ::state/groups {}
         ::state/moving-player-index 0
         ::state/players []
         ::state/reserves {}
         ::state/territories #{}}))
    "spec should fail on a state with ownerships key missing")
  (test/is
    (not
      (spec/valid? 
        ::state/state
        {::state/bonuses {}
         ::state/connections {}
         ::state/garrisons {} 
         ::state/groups {}
         ::state/moving-player-index 0
         ::state/ownerships {}
         ::state/reserves {}
         ::state/territories #{}}))
    "spec should fail on a state with players key missing")
  (test/is
    (not
      (spec/valid? 
        ::state/state
        {::state/bonuses {}
         ::state/connections {}
         ::state/garrisons {} 
         ::state/groups {}
         ::state/moving-player-index 0
         ::state/ownerships {}
         ::state/players []
         ::state/territories #{}}))
    "spec should fail on a state with reserves key missing")
  (test/is
    (not
      (spec/valid? 
        ::state/state
        {::state/bonuses {}
         ::state/connections {}
         ::state/garrisons {} 
         ::state/groups {}
         ::state/moving-player-index 0
         ::state/ownerships {}
         ::state/players []
         ::state/reserves {}}))
    "spec should fail on a state with territories key missing")
  (test/is
    (not
      (spec/valid? 
        ::state/state
        {::state/bonuses {"group1" ""}
         ::state/connections {"territory1" #{}}
         ::state/garrisons {"territory1" 1} 
         ::state/groups {"group1" #{"territory1"}}
         ::state/moving-player-index 0
         ::state/ownerships {"territory1" "player1"}
         ::state/players ["player1"]
         ::state/reserves {"player1" 0}
         ::state/territories #{"territory"}}))
    "spec should fail on bonuses not being numbers")
  (test/is
    (not
      (spec/valid? 
        ::state/state
        {::state/bonuses {"group1" -1}
         ::state/connections {"territory1" #{}}
         ::state/garrisons {"territory1" 1} 
         ::state/groups {"group1" #{"territory1"}}
         ::state/moving-player-index 0
         ::state/ownerships {"territory1" "player1"}
         ::state/players ["player1"]
         ::state/reserves {"player1" 0}
         ::state/territories #{"territory"}}))
    "spec should fail on bonuses being negative numbers")
  (test/is
    (not
      (spec/valid? 
        ::state/state
        {::state/bonuses {"group1" 1}
         ::state/connections {"territory1" #{}}
         ::state/garrisons {"territory1" ""} 
         ::state/groups {"group1" #{"territory1"}}
         ::state/moving-player-index 0
         ::state/ownerships {"territory1" "player1"}
         ::state/players ["player1"]
         ::state/reserves {"player1" 0}
         ::state/territories #{"territory"}}))
    "spec should fail on garrisons not being numbers")
  (test/is
    (not
      (spec/valid? 
        ::state/state
        {::state/bonuses {"group1" 1}
         ::state/connections {"territory1" #{}}
         ::state/garrisons {"territory1" -1} 
         ::state/groups {"group1" #{"territory1"}}
         ::state/moving-player-index 0
         ::state/ownerships {"territory1" "player1"}
         ::state/players ["player1"]
         ::state/reserves {"player1" 0}
         ::state/territories #{"territory"}}))
    "spec should fail on garrisons being negative numbers")
  (test/is
    (not
      (spec/valid? 
        ::state/state
        {::state/bonuses {}
         ::state/connections {"territory1" #{}}
         ::state/garrisons {"territory1" 1} 
         ::state/groups {}
         ::state/moving-player-index 0
         ::state/ownerships {"territory1" "player1"}
         ::state/players ["player1"]
         ::state/reserves {"player1" 0}
         ::state/territories #{"territory"}}))
    "spec should fail on territories not being part of any group")
  (test/is
    (not
      (spec/valid? 
        ::state/state
        {::state/bonuses {"group1" 1}
         ::state/connections {"territory1" #{}}
         ::state/garrisons {"territory1" 1} 
         ::state/groups {"group1" #{"territory1"}
                         "group2" #{"territory1"}}
         ::state/moving-player-index 0
         ::state/ownerships {"territory1" "player1"}
         ::state/players ["player1"]
         ::state/reserves {"player1" 0}
         ::state/territories #{"territory"}}))
    "spec should fail on territories being part of more than one group")
  (test/is
    (not
      (spec/valid? 
        ::state/state
        {::state/bonuses {}
         ::state/connections {"territory1" #{}}
         ::state/garrisons {"territory1" 1} 
         ::state/groups {"group1" #{"territory1"}}
         ::state/moving-player-index 0
         ::state/ownerships {"territory1" "player1"}
         ::state/players ["player1"]
         ::state/reserves {"player1" 0}
         ::state/territories #{"territory"}}))
    "spec should fail on groups without any bonus assigned")
  (test/is
    (not
      (spec/valid? 
        ::state/state
        {::state/bonuses {"group1" 1}
         ::state/connections {}
         ::state/garrisons {"territory1" 1} 
         ::state/groups {"group1" #{"territory1"}}
         ::state/moving-player-index 0
         ::state/ownerships {"territory1" "player1"}
         ::state/players ["player1"]
         ::state/reserves {"player1" 0}
         ::state/territories #{"territory"}}))
    "spec should fail on territories without any connections assigned")
  (test/is
    (not
      (spec/valid? 
        ::state/state
        {::state/bonuses {"group1" 1}
         ::state/connections {"territory1" #{}}
         ::state/garrisons {"territory1" state/min-garrison} 
         ::state/groups {"group1" #{"territory1"}}
         ::state/moving-player-index 0
         ::state/ownerships {"territory1" "player1"}
         ::state/players ["player1"]
         ::state/reserves {"player1" 0}
         ::state/territories #{"territory"}}))
    "spec should fail on owned territories with less than minimum armies in its garrison")
  (test/is
    (not
      (spec/valid? 
        ::state/state
        {::state/bonuses {"group1" 1
                          "group2" 2}
         ::state/connections {"territory1" #{}}
         ::state/garrisons {"territory1" 1} 
         ::state/groups {"group1" #{"territory1"}}
         ::state/moving-player-index 0
         ::state/ownerships {"territory1" "player1"}
         ::state/players ["player1"]
         ::state/reserves {"player1" 0}
         ::state/territories #{"territory"}}))
    "spec should fail on unknown groups in bonuses")
  (test/is
    (not
      (spec/valid? 
        ::state/state
        {::state/bonuses {"group1" 1}
         ::state/connections {"territory1" #{}
                              "territory2" #{}}
         ::state/garrisons {"territory1" 1} 
         ::state/groups {"group1" #{"territory1"}}
         ::state/moving-player-index 0
         ::state/ownerships {"territory1" "player1"}
         ::state/players ["player1"]
         ::state/reserves {"player1" 0}
         ::state/territories #{"territory"}}))
    "spec should fail on unknown territories in connection from connection")
  (test/is
    (not
      (spec/valid? 
        ::state/state
        {::state/bonuses {"group1" 1}
         ::state/connections {"territory1" #{"territory2"}}
         ::state/garrisons {"territory1" 1} 
         ::state/groups {"group1" #{"territory1"}}
         ::state/moving-player-index 0
         ::state/ownerships {"territory1" "player1"}
         ::state/players ["player1"]
         ::state/reserves {"player1" 0}
         ::state/territories #{"territory"}}))
    "spec should fail on unknown territories in connection to connection")
  (test/is
    (not
      (spec/valid? 
        ::state/state
        {::state/bonuses {"group1" 1}
         ::state/connections {"territory1" #{}}
         ::state/garrisons {"territory1" 1
                            "territory2" 1} 
         ::state/groups {"group1" #{"territory1"}}
         ::state/moving-player-index 0
         ::state/ownerships {"territory1" "player1"}
         ::state/players ["player1"]
         ::state/reserves {"player1" 0}
         ::state/territories #{"territory"}}))
    "spec should fail on unknown territories in garrisons")
  (test/is
    (not
      (spec/valid? 
        ::state/state
        {::state/bonuses {"group1" 1}
         ::state/connections {"territory1" #{}}
         ::state/garrisons {"territory1" 1} 
         ::state/groups {"group1" #{"territory1" 
                                    "territory2"}}
         ::state/moving-player-index 0
         ::state/ownerships {"territory1" "player1"}
         ::state/players ["player1"]
         ::state/reserves {"player1" 0}
         ::state/territories #{"territory"}}))
    "spec should fail on unknown territories in groups")
  (test/is
    (not
      (spec/valid? 
        ::state/state
        {::state/bonuses {"group1" 1}
         ::state/connections {"territory1" #{}}
         ::state/garrisons {"territory1" 1} 
         ::state/groups {"group1" #{"territory1"}}
         ::state/moving-player-index 0
         ::state/ownerships {"territory1" "player1"
                             "territory2" "player1"}
         ::state/players ["player1"]
         ::state/reserves {"player1" 0}
         ::state/territories #{"territory"}}))
    "spec should fail on unknown territories in ownerships")
  (test/is
    (not
      (spec/valid? 
        ::state/state
        {::state/bonuses {"group1" 1}
         ::state/connections {"territory1" #{}}
         ::state/garrisons {"territory1" 1} 
         ::state/groups {"group1" #{"territory1"}}
         ::state/moving-player-index 0
         ::state/ownerships {"territory1" "player2"}
         ::state/players ["player1"]
         ::state/reserves {"player1" 0}
         ::state/territories #{"territory"}}))
    "spec should fail on unknown players in ownerships")
  (test/is
    (not
      (spec/valid? 
        ::state/state
        {::state/bonuses {"group1" 1}
         ::state/connections {"territory1" #{}}
         ::state/garrisons {"territory1" 1} 
         ::state/groups {"group1" #{"territory1"}}
         ::state/moving-player-index 0
         ::state/ownerships {"territory1" "player1"}
         ::state/players ["player1"]
         ::state/reserves {"player1" 0
                           "player2" 2}
         ::state/territories #{"territory"}}))
    "spec should fail on unknown players in reserves")
  (test/is
    (not
      (spec/valid? 
        ::state/state
        {::state/bonuses {"group1" 1}
         ::state/connections {"territory1" #{}}
         ::state/garrisons {"territory1" 1} 
         ::state/groups {"group1" #{"territory1"}}
         ::state/moving-player-index 2
         ::state/ownerships {"territory1" "player1"}
         ::state/players ["player1" 
                          "player2"]
         ::state/reserves {"player1" 0
                           "player2" 0}
         ::state/territories #{"territory1"}}))
    "spec should fail on moving player index out of bound")
  (test/is
    (not
      (spec/valid? 
        ::state/state
        {::state/bonuses {"group1" 1}
         ::state/connections {"territory1" #{}
                              "territory2" #{}}
         ::state/garrisons {"territory1" 1
                            "territory2" 0} 
         ::state/groups {"group1" #{"territory1"
                                    "territory2"}}
         ::state/moving-player-index nil
         ::state/ownerships {"territory1" nil 
                             "territory2" nil}
         ::state/players ["player1" "player2"]
         ::state/reserves {"player1" 0
                           "player2" 0}
         ::state/territories #{"territory1" 
                               "territory2"}}))
    "spec should fail if there are less unclaimed territories than players during a distribution phase"))


