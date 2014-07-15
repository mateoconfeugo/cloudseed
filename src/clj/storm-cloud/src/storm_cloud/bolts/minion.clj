(ns storm-cloud.bolts.minion
  (:require [clojure.string :as string]
            [backtype.storm.clojure :refer [defbolt bolt emit-bolt! ack!]]
            [backtype.storm.config]
            [cloudseed.fsm.highstate :as hs]
            [cloudseed.protocols :refer :all]
            [cloudseed.config :refer [config]]
            [datomic.api :as d :refer [q db]]))

  (def cfg  (-> config :minion))

(def settings {:db-host (-> cfg :state-database :host)
               :db-port (-> cfg :state-database :port)
               :db-user (-> cfg :state-database :db-user)
               :db-password (-> cfg :state-database :db-password)
               :db-name (-> cfg :state-database :db-name)})

(defn tuplify
  "Returns a vector of the vals at keys ks in map."
  [m ks]
  (mapv #(get m %) ks))

(defn maps->rel
  "Convert a collection of maps into a relation, returning
   the tuplification of each map by ks"
  [maps ks]
  (mapv #(tuplify % ks) maps))

(defn add-state
  [conn state]
  (comment
  (doseq [schema ["states/job.edn"
                  "states/state.edn"]]
    (->> (io/resource schema)
         (dio/transact-all conn)))
      @(d/transact conn [[:db/add state :state/name (:name state)]])))

(def db-uri-base "datomic:mem://")

(defn db-conn
  "Create a connection to an anonymous, in-memory database."
  []
  (let [uri (str db-uri-base (d/squuid))]
    (d/delete-database uri)
    (d/create-database uri)
    (d/connect uri)))

(def state-db {:classname "com.mysql.jdbc.Driver"
               :subprotocol "mysql"
               :subname (str "//" (:db-host settings) ":" (:db-port settings) "/" (:db-name settings))
               :user (:db-user settings)
               :password (:db-password settings)})

(defmacro dbg[x] `(let [x# ~x] (println "dbg:" '~x "=" x#) x#))

(defn process-graph
  "sends budgets to delivery nodes"
  [collector driver graph]
  (let [new-state (run driver graph)]
  ;;  (emit-bolt! collector new-state)
    ))

(defbolt minion-service
  ["minion-graph-driver"]
  {:prepare true}
  [conf context collector]
  (let [processor-fn  (partial process-graph collector)
        driver (hs/new-highstate {:lowstates []})
        db (db-conn)]
    (bolt
     (prepare [conf context collector])
     (execute [tuple]
              (let [val (.getValue tuple 0)]
                (processor-fn driver val))
              (ack! collector tuple)))))
