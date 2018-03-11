(ns risk.engine.core
  (:require [clojure.spec.alpha :as spec]
            [cuerdas.core :as str]))


(spec/def ::state
  (spec/map-of keyword? any?))


(spec/def ::type keyword?)


(spec/def ::data any?)


(spec/def ::event
  (spec/keys :req [::type] 
             :opt [::data]))


(spec/fdef event-handler
  :args (spec/cat
          :event-handlers ::event-handlers
          :state ::state
          :event ::event)
  :ret int?)


(spec/def ::event-handlers
  (spec/map-of ::type ::event-handler))


(spec/def ::failure
  string?)


(spec/def ::result
  (spec/or 
    :state ::state
    :failure ::failure))


(spec/def ::event-handling-result
  (spec/keys :req [::result]))
  

(spec/fdef handle-event
  :args (spec/cat
          :event-handlers ::event-handlers
          :state ::state
          :event ::event)
  :ret ::event-handling-result)


(defn handle-event [event-handlers 
                    state 
                    {:keys [::type ::data]}]
  (let [event-handler (get 
                        event-handlers 
                        type 
                        (fn [_ _] 
                          (str/format
                            "no event handler found for '%s' event type"
                            (name type))))]
    (event-handler state data)))


