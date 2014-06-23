(ns cloudseed.states.job
    ^{:author "Matt Burns"
    :doc "Create a jenkins job and run it in fsm"}
  (:require [plumbing.core :refer :all]
            [plumbing.graph :as graph :refer [eager-compile]]))

(def job-state-data-graph
  {:job-name (fnk  [{name 1}] name)
   :job-id (fnk  [{name 1}] job-name)
   :job (fnk [{job 1}] (if-let [job-id nil?] (job) job-id))
   :build (fnk [{job-id 10}] job-id)})

(def job-fixture-fn (graph/eager-compile job-state-date-graph))
