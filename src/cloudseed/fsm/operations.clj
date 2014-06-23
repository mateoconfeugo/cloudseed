(ns cloudseed.fsm.operations
  (:require [pallet.algo.fsm.event-machine :refer :all]
            [pallet.algo.fsm.stateful-fsm :refer [stateful-fsm]]
            [cloudseed.protocols :refer [fsm]]))

(defn run-fsm
  [{:keys [graph] :as args}]
  (let [locked-counter (atom 0)
        open-counter (atom 0)
        session (atom {})
        fsm (stateful-fsm
             {:state-kw :locked :state-data {:code "123"}}
             {:locked {:transitions #{:locked :open}}
              :open {:transitions #{:open :timed-out :re-locked}}}
             nil
             #{:timeout})
        p (promise)
        q (promise)
        state-map {:locked {:event-fn locked-no-timeout
                            :state-fn (fn [_] (swap! locked-counter inc))}
                   :open {:event-fn open
                          :state-fn (fn [_]
                                      (when (= 1 (swap! open-counter inc))
                                        (deliver p (graph))))}
                   :re-locked {:event-fn (fn [state _ _] state)
                               :state-fn (fn [_] (deliver p (graph)))}}
        {:keys [event state reset] :as em} (event-machine fsm state-map nil)
        eml (event-machine-loop-fn state-map #{:re-locked})
        thread (Thread. #(eml em))]
    (.start thread)
    (event :highstate 1)
    (.join thread)
    [@p @q]))

(defn build-highstate
  [lowstates])

(defn highstate
  "IMPLEMENTATION CONSTRUCTOR"
  [{:keys[lowstates] :as args}]
  (let [highstate-graph (build-highstate lowstates)]
    (reify FSH
      (run [this]
        (fsm-function-graph highstate-graph)))))
