(ns clj-salt.core
  ^ {:author "Matthew Burns"
     :doc "Top level entry point for clojure replacement of salt"}
    (:import [java.io.File])
    (:require [clojure.core :refer [re-find]]
              [clojure.java.io :as io]
              [clojure.xml :as xml]
              [clojure.pprint :refer [pprint pp]]
              [me.raynes.fs :refer [mkdirs]]
              [shoreleave.server-helpers :refer [safe-read]]
              [clojure.tools.cli :refer [parse-opts]]
              [clj-yaml.core :as yaml]
              [jenkins.core :refer [jobs job build with-config builds build! job-config jobs-by-color failing-jobs
                                    last-successful-build last-build]])
    (:gen-class))
;;(def state-path (str (System/getProperty "user.dir") "/resources/system_state.yaml"))
;;(def yaml-data (slurp state-path))
;;(def cfg-data (slurp (str (System/getProperty "user.dir") "/resources/config.edn")))
;;(def state-data (yaml/parse-string yaml-data))
;;(prn-str yaml-data)
;;(rest yaml-data)

(defn read-state
  "Take the highstate of a salt system in yaml and return a clojure equivalent "
  []
  (let  [yaml-data (slurp (str (System/getProperty "user.dir") "/resources/system_state.yaml"))]
    ;;    (yaml/parse-string yaml-data)
    (prn-str yaml-data)))

(read-state)

(defn read-config
  "Read a config file and return it as Clojure Data.  Usually, this is a hashmap"
  ([]
     (read-config (str (System/getProperty "user.dir") "/resources/config.edn")))
  ([config-loc]
     (safe-read (slurp config-loc))))

(def config (read-config))

(defn read-lines [filename]
  (let [rdr (io/reader filename)]
    (defn read-next-line []
      (if-let [line (.readLine rdr)]
        (cons line (lazy-seq (read-next-line)))
        (.close rdr)))
    (lazy-seq (read-next-line))))

(defn -deploy [opts])

(defn process [prj]
  "driver entry point"
  (let [opts {:dsl-dir-path  "/Users/mburns/dandb/jenkins-jobs"
              :jobs-dir-path "/Users/mburns/.jenkins/jobs"
              :project-name prj
              }
        job-cfg-deploy-results (-deploy opts)]
;;    (trigger-seed-job "verified_stg")
    ))

(def modules (:modules config))

(defn -main [] (doseq [m modules] (process m)))

(-main)
