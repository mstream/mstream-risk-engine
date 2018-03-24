(ns risk.engine.core
  (:require [clojure.spec.alpha :as spec]
            [cuerdas.core :as str]))


(spec/def ::state
  (spec/map-of keyword? any?))


(spec/def ::coeffects
  (spec/map-of keyword? any?))


(spec/def ::context
  (spec/keys :req [::coeffects ::state]))


(spec/def ::event-type keyword?)


(spec/def ::event-data any?)


(spec/def ::event
  (spec/keys :req [::event-type] 
             :opt [::event-data]))


(spec/fdef event-handler
  :args (spec/cat
          :event-handlers ::event-handlers
          :context ::context
          :event ::event)
  :ret int?)


(spec/def ::event-handlers
  (spec/map-of ::event-type ::event-handler))


(spec/def ::failure
  string?)


(spec/def ::event-handling-result
  (spec/or 
    :state ::state
    :failure ::failure))
  

(spec/fdef handle-event
  :args (spec/cat
          :event-handlers ::event-handlers
          :context ::context
          :event ::event)
  :ret ::event-handling-result)


(defn handle-event [event-handlers 
                    {:keys [::state ::coeffects] :as context} 
                    {:keys [::event-type ::event-data] :as event}]
  (when (not (spec/valid? ::context context)) "invalid context")
  (when (not (spec/valid? ::event event)) "invalid event")
  (let [event-handler (get 
                        event-handlers 
                        event-type 
                        (fn [_ _] 
                          (str/format
                            "no event handler found for '%s' event type"
                            (name event-type))))]
    (event-handler context event-data)))


