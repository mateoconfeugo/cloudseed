(ns cloudseed.db
  ^{:author "Matt Burns"
    :doc "application db functions"}
  (:require [clojure.java.jdbc :as sql :refer :all]
;;            [cloudseed.crates.database :refer [create-shopping-spec]]
            ))

(defn initialize-shopping-database
  "Build schema for the shopping database"
  [db-spec]
;;  (create-shopping-spec db-spec)
  )
