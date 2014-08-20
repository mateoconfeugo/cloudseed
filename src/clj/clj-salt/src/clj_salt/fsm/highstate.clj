(ns clj-salt.fsm.highstate
  (:require [clj-salt.protocols :refer [FSM]]
            [clj-salt.fsm.operations :refer [run-fsm]]))

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

(comment
  "the lowstates are chunks of function graphs
  AGraph is a simple and declarative way to specify a structured computation,
  which is easy to analyze, change, compose, and monitor.
  A Graph is just a map from keywords to keyword functions
  "
(new-highstate {:lowstates []} )
  )
