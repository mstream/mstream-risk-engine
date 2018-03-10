(ns risk.engine.state-test
  (:require [clojure.test :as test]
            [clojure.spec.alpha :as spec]
            [cuerdas.core :as str]            
            [risk.engine.state :as state]))


(test/deftest initial-state
  (test/is (spec/valid? 
             ::state/state 
             state/initial-state)
    "initial state should conform with its spec"))


(test/deftest state-spec
  (let [empty-state {::state/bonuses {}
                     ::state/connections {}
                     ::state/garrisons {} 
                     ::state/groups {}
                     ::state/ownerships {}
                     ::state/territories #{}}]
    (test/is
      (spec/valid? 
        ::state/state
        empty-state)
      "spec should pass on an empty state")
    (test/is
      (not
        (spec/valid? 
          ::state/state
          (dissoc 
            empty-state
            ::state/bonuses)))
      "spec should fail on a state with bonuses key missing")
    (test/is
      (not
        (spec/valid? 
          ::state/state
          (dissoc 
            empty-state
            ::state/connections)))
      "spec should fail on a state with connections key missing")
    (test/is
      (not
        (spec/valid? 
          ::state/state
          (dissoc 
            empty-state
            ::state/garrisons)))
      "spec should fail on a state with garrisons key missing")
    (test/is
      (not
        (spec/valid? 
          ::state/state
          (dissoc 
            empty-state
            ::state/groups)))
      "spec should fail on a state with groups key missing")
    (test/is
     (not
       (spec/valid? 
         ::state/state
         (dissoc 
           empty-state
           ::state/ownerships)))
     "spec should fail on a state with ownerships key missing")
    (test/is
     (not
       (spec/valid? 
         ::state/state
         (dissoc 
           empty-state
           ::state/territories)))
     "spec should fail on a state with territories key missing")
    (test/is
     (not
       (spec/valid? 
         ::state/state
         (assoc 
           empty-state
             ::state/bonuses {"group1" ""}
             ::state/garrisons {"territory1" 1}
             ::state/groups {"group1" #{"territory1"}}
             ::state/ownerships {"territory1" "player1"}
             ::state/territories #{"territory1"})))
     "spec should fail on bonuses not being numbers")
    (test/is
     (not
       (spec/valid? 
         ::state/state
         (assoc 
           empty-state
             ::state/bonuses {"group1" 1}
             ::state/garrisons {"territory1" ""}
             ::state/groups {"group1" #{"territory1"}}
             ::state/ownerships {"territory1" "player1"}
             ::state/territories #{"territory1"})))
     "spec should fail on garrisons not being numbers")
    (test/is
      (not
        (spec/valid? 
          ::state/state
          (assoc 
            empty-state
            ::state/garrisons {"territory1" 0}
            ::state/territories #{"territory1"})))
      "spec should fail on territories not being part of any group")
    (test/is
      (not
        (spec/valid? 
          ::state/state
          (assoc 
            empty-state
            ::state/bonuses {"group1" 1
                             "group2" 2}
            ::state/garrisons {"territory1" 0}
            ::state/groups {"group1" "territory1"
                            "group2" "territory1"}
            ::state/territories #{"territory1"})))
      "spec should fail on territories being part of more than one group")
    (test/is
      (not
        (spec/valid? 
          ::state/state
          (assoc 
            empty-state
            ::state/garrisons {"territory1" 0}
            ::state/groups {"group1" #{"territory1"}}
            ::state/territories #{"territory1"})))
      "spec should fail on groups without any bonus assigned")
    (test/is
      (not
        (spec/valid? 
          ::state/state
          (assoc 
            empty-state
            ::state/bonuses {"group1" 1}
            ::state/groups {"group1" #{"territory1"}}
            ::state/territories #{"territory1"})))
      "spec should fail on territories without any garrison assigned")
    (test/is
      (not
        (spec/valid? 
          ::state/state
          (assoc 
            empty-state
            ::state/bonuses {"group1" 1}
            ::state/garrisons {"territory1" 0}
            ::state/groups {"group1" #{"territory1"}}
            ::state/ownerships {"territory1" "player1"}
            ::state/territories #{"territory1"})))
      "spec should fail on owned territories with 0 armies in its garrison")
    (test/is
      (not
        (spec/valid? 
          ::state/state
          (assoc 
            empty-state
            ::state/bonuses {"group1" 1})))
      "spec should fail on unknown groups in bonuses")
    (test/is
      (not
        (spec/valid? 
          ::state/state
          (assoc 
            empty-state
            ::state/bonuses {"group1" 1}
            ::state/connections {"territory2" #{"territory1"}}
            ::state/garrisons {"territory1" 1}
            ::state/groups {"group1" #{"territory1"}}
            ::state/ownerships {"territory1" "player1"}
            ::state/territories #{"territory1"})))
      "spec should fail on unknown territories in connection from connection")
    (test/is
      (not
        (spec/valid? 
          ::state/state
          (assoc 
            empty-state
            ::state/bonuses {"group1" 1}
            ::state/connections {"territory1" #{"territory2"}}
            ::state/garrisons {"territory1" 1}
            ::state/groups {"group1" #{"territory1"}}
            ::state/ownerships {"territory1" "player1"}
            ::state/territories #{"territory1"})))
      "spec should fail on unknown territories in connection to connection")
    (test/is
      (not
        (spec/valid? 
          ::state/state
          (assoc 
            empty-state
            ::state/bonuses {"group1" 1}
            ::state/garrisons {"territory1" 1
                               "territory2" 1}
            ::state/groups {"group1" #{"territory1"}}
            ::state/ownerships {"territory1" "player1"}
            ::state/territories #{"territory1"})))
      "spec should fail on unknown territories in garrisons")
    (test/is
      (not
        (spec/valid? 
          ::state/state
          (assoc 
            empty-state
            ::state/bonuses {"group1" 1}
            ::state/garrisons {"territory1" 1}
            ::state/groups {"group1" #{"territory1" "territory2"}}
            ::state/ownerships {"territory1" "player1"}
            ::state/territories #{"territory1"})))
      "spec should fail on unknown territories in groups")
    (test/is
      (not
        (spec/valid? 
          ::state/state
          (assoc 
            empty-state
            ::state/bonuses {"group1" 1}
            ::state/garrisons {"territory1" 1}
            ::state/groups {"group1" #{"territory1"}}
            ::state/ownerships {"territory1" "player1"  
                                "territory2" "player1"}
            ::state/territories #{"territory1"})))
      "spec should fail on unknown territories in ownerships")))



