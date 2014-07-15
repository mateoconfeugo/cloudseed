(ns storm-cloud.db.state
(:require [clojure.string :as string]
          [backtype.storm.clojure :refer [defbolt bolt emit-bolt! ack!]]
          [backtype.storm.config]
;;          [storm-cloud.fsm.highstate :as hs]
          [storm-cloud.protocols :refer :all]
          [storm-cloud.config :refer [config]]
          [datomic.api :as d :refer [q db]]))

(def cfg  (-> config :minion))g

(def state-db-settings {:db-host (-> cfg :state-database :host)
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
