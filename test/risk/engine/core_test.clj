(ns risk.engine.core-test
  (:require [clojure.test :as test]
            [risk.engine.core :as core]))


(test/deftest handle-event
  (test/is 
    (= "no event handler found for 'unknown' event type"
       (core/handle-event
         {}
         {}
         {::core/event-type :unknown}))
    "should return failure for event types with no associated handler")
  (test/is 
    (= {:counter 1}
       (core/handle-event
         {:counter-increased (fn [{:keys [::core/state]} _]
                                (update 
                                  state
                                  :counter
                                  inc))}
         {::core/state {:counter 0}
          ::core/coeffects {}}
         {::core/event-type :counter-increased}))
    "should apply the matching to the event type handler")
  (test/is 
    (= {:counter 1}
       (core/handle-event
         {:counter-set (fn [{:keys [::core/state]} value]
                         (assoc 
                           state
                           :counter
                           value))}
         {::core/state {:counter 0}
          ::core/coeffects {}}
         {::core/event-type :counter-set
          ::core/event-data 1}))
    "should pass the event data to the handler")
  (test/is 
    (= {:counter 10}
       (core/handle-event
         {:counter-randomized (fn [{state ::core/state 
                                    {:keys [:random-number]} ::core/coeffects} _]
                                (assoc 
                                  state
                                  :counter
                                  (random-number)))}
         {::core/state {:counter 0}
          ::core/coeffects {:random-number (fn [] 10)}}
         {::core/event-type :counter-randomized}))
    "should pass coeffects to the handler"))
        






