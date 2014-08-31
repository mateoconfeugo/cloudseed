(defproject storm-cloud "0.1.0"
  :description "Orchastration Topo"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main storm-cloud.TopologySubmitter
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.datomic/datomic "0.8.3335"
                  :exclusions [org.slf4j/slf4j-nop org.slf4j/slf4j-log4j12
                                log4j-over-slf4j.jar]]
                 [storm/storm-kestrel "0.9.0-wip5-multischeme"]
                 [storm "0.9.0.1"]
;;                 [org.slf4j/slf4j-log4j12 "1.6.4"]
                 ]
  :aot [storm-cloud.TopologySubmitter]
  ;; include storm dependency only in dev because production storm cluster provides it
  :profiles {:dev {:dependencies [[storm "0.8.1"]]}})
