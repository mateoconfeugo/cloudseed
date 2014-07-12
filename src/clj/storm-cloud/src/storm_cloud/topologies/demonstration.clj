(ns storm-cloud.topologies.demonstration
  (:import [backtype.storm LocalCluster LocalDRPC])
  (:require [storm-cloud.spouts.command-control :refer [command-control]]
            [storm-cloud.spouts.type-spout :refer [type-spout]]
            [storm-cloud.bolts.stormy :refer [stormy-bolt]]
            [storm-cloud.bolts.storm-cloud-bolt :refer [storm-cloud-bolt]]
            [backtype.storm
             [clojure :refer [topology spout-spec bolt-spec]]
             [config :refer :all]]))
(comment
(defn salt-topology [cfg]
  (topology
   {"command-control" (spout-spec command-control)}
   {"minion-bolt" (bolt-spec {"salt-minion" ["type"]} minion-bolt :p (:total-nodes cfg))
    "master-bolt" (bolt-spec {"salt-master" :shuffle} master-bolt :p (:master cfg))})))


(defn stormy-topology []
  (topology
   {"spout" (spout-spec type-spout)}
   {"stormy-bolt" (bolt-spec {"spout" ["type"]} stormy-bolt :p 2)
    "storm-cloud-bolt" (bolt-spec {"stormy-bolt" :shuffle} storm-cloud-bolt :p 2)}))

(defn run! [& {debug "debug" workers "workers" :or {debug "true" workers "2"}}]
  (doto (LocalCluster.)
    (.submitTopology "stormy topology"
                     {TOPOLOGY-DEBUG (Boolean/parseBoolean debug)
                      TOPOLOGY-WORKERS (Integer/parseInt workers)}
                     (stormy-topology))))
