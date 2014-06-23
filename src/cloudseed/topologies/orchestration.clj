(ns cloudseed.topologies.orchestration
  (:import [backtype.storm.spout KestrelThriftClient KestrelThriftSpout]
           [backtype.storm.scheme StringScheme]
           [backtype.storm StormSubmitter LocalCluster])
  (:require [cloudseed.config]
            [cloudseed.bolts.minion]
            [backtype.storm clojure config])
  (:gen-class))

(defn mk-topology []
  (topology
   {"highstate" (spout-spec state-spout :p 1)}
   {"minions" (bolt-spec {"minion" :shuffle} minion-service :p (-> (config) :minions :p))}))

(defn run-local! []
  (let [cluster (LocalCluster.)]
    (.submitTopology cluster "test-graph" {TOPOLOGY-DEBUG true} (mk-topology))
    (Thread/sleep 10000)
    (.shutdown cluster)))

(defn -main
  "Send the topology that reifies the orchestration system to the storm cluster"
  [topology-name]
  (StormSubmitter/submitTopology topology-name {TOPOLOGY-DEBUG true TOPOLOGY-WORKERS 3} (mk-topology)))
