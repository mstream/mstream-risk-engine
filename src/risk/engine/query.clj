(ns risk.engine.query
  (:require [clojure.spec.alpha :as spec]
            [risk.engine.state :as state]))


(spec/fdef can-attack?
  :args (spec/cat 
          :state ::state/state
          :player string?
          :from string? 
          :to string?)
  :ret boolean?)


(defn can-attack? [state player from to]
  (and (contains? (get-in state [::state/connections from]) to)
       (= player (get-in state [::state/ownerships from]))
       (not= player (get-in state [::state/ownerships to]))
       (<= 2 (get-in state [::state/garrisons from]))))


(spec/fdef reinforcement-size
  :args (spec/cat 
          :state ::state/state
          :player string?)
  :ret int?)


(defn reinforcement-size [state player]
  (let [owned-territories (set (map
                                 first
                                 (filter 
                                        (comp (partial = player) second) 
                                        (get-in state [::state/ownerships]))))]
    (max 3 (+ (quot (count owned-territories) 
                3)
              (apply 
                + 
                (map 
                  (partial get (get-in state [::state/bonuses]))
                  (keys (filter 
                          (comp 
                            (partial every? (partial contains? owned-territories)) 
                            second) 
                          (get-in state [::state/groups])))))))))
       






