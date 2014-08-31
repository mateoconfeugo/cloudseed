(ns storm-cloud.topologies.orchestration
  (:import  [backtype.storm StormSubmitter LocalCluster])
  (:use [backtype.storm.daemon common])
  (:use [backtype.storm.config])
  (:use [backtype.storm bootstrap testing])
  (:require [storm-cloud.config :refer [config]]
            [storm-cloud.bolts.minion :refer [minion-service]]
            [backtype.storm.clojure :refer [spout-spec bolt-spec]])
  (:gen-class))

(defn mk-topology []
  (topology
;;   {"highstate" (spout-spec state-spout :p 1)}
   {"minions" (bolt-spec {"minion" :shuffle} minion-service :p (-> (config) :minions :p))}))

(defn run-local! []
  (let [cluster (LocalCluster.)]
        (submit-local-topology  cluster "test-graph" {TOPOLOGY-DEBUG true} (mk-topology))
    (Thread/sleep 10000)
    (.shutdown cluster)))

(defn -main
  "Send the topology that reifies the orchestration system to the storm cluster"
  [topology-name]
   (StormSubmitter/submitTopology topology-name {TOPOLOGY-DEBUG true TOPOLOGY-WORKERS 3} (mk-topology)))
