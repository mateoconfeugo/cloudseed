(ns clj-salt.fsm.highstate
  (:require [cloudseed.protocols :refer [FSM]]
            [cloudseed.fsm.operations :refer [run-fsm]]))

(defn build-highstate
  "compose together the graphs that make up the lowstates into the highstate"
  [lowstates])

(defn store [state] state)

(defn new-highstate
  [{:keys[lowstates] :as args}]
  (let [highstate-graph (build-highstate lowstates)]
    (reify FSM
      (run [this highstate-graph]
        (let [new-state (run-fsm highstate-graph)
              updated-db (store new-state)]
          [new-state updated-db])))))
