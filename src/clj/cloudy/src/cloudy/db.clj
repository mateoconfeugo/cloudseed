(ns cloudy.db
  ^{:author "Matt Burns"
    :doc "application db functions"}
  (:require [clojure.java.jdbc :as sql :refer :all]
;;            [cloudy.crates.database :refer [create-host-spec]]
            ))

(defn initialize-hosts-database
  "Build schema for the hosts database"
  [db-spec]
;;  (create-shopping-spec db-spec)
  )
