(ns risk.engine.core-test
  (:require [clojure.test :as test]
            [risk.engine.core :as core]))


(test/deftest handle-event
  (test/is 
    (= "no event handler found for 'unknown' event type"
       (core/handle-event
         {}
         {}
         {::core/type :unknown}))
    "should return failure for event types with no associated handler")
  (test/is 
    (= {:counter 1}
       (core/handle-event
         {:counter-increased (fn [state _]
                                (update 
                                  state
                                  :counter
                                  inc))}
         {:counter 0}
         {::core/type :counter-increased}))
    "should apply the matching to the event type handler")
  (test/is 
    (= {:counter 1}
       (core/handle-event
         {:counter-set (fn [state value]
                         (assoc 
                           state
                           :counter
                           value))}
         {:counter 0}
         {::core/type :counter-set
          ::core/data 1}))
    "should pass the event data to the handler"))
        






