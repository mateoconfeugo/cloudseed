(ns storm-cloud.TopologySubmitter
  (:import [backtype.storm StormSubmitter LocalCluster])
  (:require [storm-cloud.topologies.demonstration :refer [stormy-topology]])
  (:use [backtype.storm clojure config])
  (:gen-class))

(defn submit-topology! [name]
  (StormSubmitter/submitTopology
   name
   {TOPOLOGY-DEBUG true
    TOPOLOGY-WORKERS 3}
   (stormy-topology)))

(defn run! [& {debug "debug" workers "workers" :or {debug "true" workers "2"}}]
  (doto (LocalCluster.)
    (.submitTopology "stormy topology"
                     {TOPOLOGY-DEBUG (Boolean/parseBoolean debug)
                      TOPOLOGY-WORKERS (Integer/parseInt workers)}
                                          (stormy-topology))))
(defn -main
  [name]
  (submit-topology! name))


(defn main
  [name]
  (submit-topology! name))
