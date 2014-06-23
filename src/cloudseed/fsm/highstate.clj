(ns cloudseed.fsm.highstate
  (:require [pallet.algo.fsm.event-machine :refer :all]
            [pallet.algo.fsm.stateful-fsm :refer [stateful-fsm]]
            [cloudseed.protocols :refer :all]
            [cloudseed.fsm.operations :refer [:run-in-fsm]]))

(defn build-highstate
  "compose together the graphs that make up the lowstates into the highstate"
  [lowstates])

(defn new-highstate
  "Constructor"
  [{:keys[lowstates] :as args}]
  (let [highstate-graph (build-highstate lowstates)]
    (reify FSH
      (run [this] (run-in-fsm highstate-graph)))))
