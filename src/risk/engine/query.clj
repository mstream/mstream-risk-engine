(ns risk.engine.query
  (:require [clojure.spec.alpha :as spec]
            [risk.engine.state :as state]))


(spec/fdef get-winner
  :args (spec/cat :state ::state/state)
  :ret string?)


(defn get-winner [{:keys [::state/ownerships]}]
  (let [owners (set (filter 
                      some? 
                      (vals ownerships)))]
    (if (= 1(count owners))
      (first owners)
      nil)))


(spec/fdef can-attack?
  :args (spec/cat 
          :state ::state/state
          :player string?
          :from string? 
          :to string?)
  :ret boolean?)


(defn can-attack? [{:keys [::state/connections ::state/garrisons ::state/ownerships]} player from to]
  (and (contains? (get connections from) to)
       (= player (get ownerships from))
       (not= player (get ownerships to))
       (<= 2 (get garrisons from))))


(spec/fdef reinforcement-size
  :args (spec/cat 
          :state ::state/state
          :player string?)
  :ret int?)


(defn reinforcement-size [{:keys [::state/bonuses ::state/groups ::state/ownerships]} player]
  (let [owned-territories (set (map
                                 first
                                 (filter 
                                        (comp (partial = player) second) 
                                        ownerships)))]
    (max 3 (+ (quot (count owned-territories) 
                3)
              (apply 
                + 
                (map 
                  (partial get bonuses)
                  (keys (filter 
                          (comp 
                            (partial every? (partial contains? owned-territories)) 
                            second) 
                          groups))))))))
       






