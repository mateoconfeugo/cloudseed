(ns storm-cloud.bolts.storm-cloud-bolt
  (:require [backtype.storm [clojure :refer [emit-bolt! defbolt ack! bolt]]]))

(defbolt storm-cloud-bolt ["message"] [{stormy :stormy :as tuple} collector]
  (emit-bolt! collector [(str "storm-cloud produced: "stormy)] :anchor tuple)
  (ack! collector tuple))
