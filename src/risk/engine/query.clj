(ns risk.engine.query
  (:require [clojure.spec.alpha :as spec]
            [cuerdas.core :as str]
            [risk.engine.state :as state]))


(defn- validate-state [state]
  (when-not (spec/valid? ::state/state state)
    (throw (IllegalArgumentException. (str/format 
                                        "invalid state: %s"
                                        (spec/explain-str ::state/state state))))))


(spec/fdef get-winner
  :args (spec/cat :state ::state/state)
  :ret string?)


(defn get-winner [{:keys [::state/ownerships] :as state}]
  (validate-state state)
  (let [owners (set (filter 
                      some? 
                      (vals ownerships)))]
    (if (= 1 (count owners))
      (first owners)
      nil)))


(spec/fdef can-attack?
  :args (spec/cat 
          :state ::state/state
          :player string?
          :from string? 
          :to string?)
  :ret boolean?)


(defn can-attack? [{:keys [::state/connections 
                           ::state/garrisons 
                           ::state/ownerships
                           ::state/players] :as state}
                   player from to]
  (validate-state state)
  (and (contains? (get connections from) to)
       (= player (get ownerships from))
       (not= player (get ownerships to))
       (<= 2 (get garrisons from))))


(spec/fdef reinforcement-size
  :args (spec/cat 
          :state ::state/state
          :player string?)
  :ret int?)


(defn reinforcement-size [{:keys [::state/bonuses 
                                  ::state/groups 
                                  ::state/ownerships] :as state} 
                          player]
  (validate-state state)
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


(spec/fdef can-claim-territory?
  :args (spec/cat 
          :state ::state/state?
          :territory string?)
  :ret boolean?)


(defn can-claim-territory? [{:keys [::state/garrisons 
                                    ::state/ownerships] :as state}
                            territory]
  (validate-state state)
  (and
       (nil? (get ownerships territory))
       (zero? (get garrisons territory))))


(spec/fdef in-distribution-phase?
  :args (spec/cat :state ::state/state?)
  :ret boolean?)


(defn in-distribution-phase? [{:keys [::state/moving-player-index
                                      ::state/territories] :as state}]
  (validate-state state)
  (some (partial can-claim-territory? state) territories))





