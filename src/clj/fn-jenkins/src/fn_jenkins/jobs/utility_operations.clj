(ns fn-jenkins.jobs.utility-operations
  (:require [shoreleave.server-helpers :refer [safe-read]]))


(defn read-config
  "Read a config file and return it as Clojure Data.  Usually, this is a hashmap"
  ([]
     (read-config (str (System/getProperty "user.dir") "/resources/config.edn")))
  ([config-loc]
     (safe-read (slurp config-loc))))

(defn path-for
  "Get the actual filename corresponding to a template."
  [base] (str "crate/jenkins/" base))

(defn truefalse [value]
  (if value "true" "false"))

(defn url-without-path
  [url-string]
  (let [url (java.net.URL. url-string)]
    (java.net.URL. (.getProtocol url) (.getHost url) (.getPort url) "")))
