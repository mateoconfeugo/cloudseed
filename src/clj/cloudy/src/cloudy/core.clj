(ns cloudy.core
  ^ {:author "Matthew Burns"
     :doc "Top level entry point for clojure replacement of salt"}
    (:import [java.io.File])
    (:require [clojure.core :refer [re-find]]
              [clojure.java.io :as io]
              [clojure.xml :as xml]
              [clojure.pprint :refer [pprint pp]]
              [me.raynes.fs :refer [mkdirs]]
              [shoreleave.server-helpers :refer [safe-read]]
;;              [clojure.tools.cli :refer [parse-opts]]
;;              [clj-yaml.core :as yaml]
;;              [jenkins.core :refer [jobs job build with-config builds build! job-config jobs-by-color failing-jobs
;;                                    last-successful-build last-build
              ;;              ]])
              )
    (:gen-class))

(defn read-state
  "Take the highstate of a salt system in yaml and return a clojure equivalent "
  []
  (let  [yaml-data (slurp (str (System/getProperty "user.dir") "/resources/system_state.yaml"))]
    ;;    (yaml/parse-string yaml-data)
    (prn-str yaml-data)
    ))

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


(defn build-system [opts])
(defn go-live [opts])
(defn -deploy [opts])

(defn highstate
  "driver entry point that runs the function graph against the targets"
  [targets & function-graph]
  (let [hs-graph {:funtion-graph {}}])
  (-deploy {:highstate function-graph}))

(comment
(defn -main
  "Create and Deploy the system state highs state functions across the desired environments
   the hope that this self can be a group spec that is converged or lifted by lein so that the
   deployment of everything that makes up businesses revenue generating IT infrastructure as simple
   as:
   1) Downloading github repo of system
   2) Setting up correct billing on cloud provider skip if dev and local virtual machine
   3) Set minimal options in configuration settings and email/sms/irc/im to send initial setup reports to
   4) run the command:  lein pallet profile build-system-from-scratch up
   5) go have a [coffee|beer|tea|water]
   6) go back to bed
   7) not get fired in the morning"
  []
;;  (let [envs (:environments config)]
;;  (doseq [env envs]
;;   (build-system env)
;;    (highstate env)
;;    (go-live env)
  ;;  ))
  )


(defn create-ci-server
  "Launchs the box on the provider that will host the docker jenkins ci server running as a tomcat war"
  [{:keys [] :as args}])

(defn state-spec-to-graph
  "take the etn specification and crate a function graph composed of pallet group specs"
  [{:keys [] :as args}])

(defn create-lein-project-for-vm-profile
  "For each type of virtual machine such as database proxy, application, caching build a lein-pallet project that can easily called by jenkins job with a very simple freestyle job"
  [{:keys [] :as args}])

(defn build-infrastructure-job
  "Build a job that builds a particlar virtual machine profile in the providers cloud"
  [{:keys [] :as args}])

(defn build-ops-job [{:keys [] :as args}])
(defn build-app-job [{:keys [] :as args}])
(defn build-maintenance-job [{:keys [] :as args}])
(defn build-report-job [{:keys [] :as args}])
(defn build-test-job [{:keys [] :as args}])
(defn create-network-infrastructure[{:keys [] :as args}])
(defn create-subnets [{:keys [] :as args}])
(defn create-dev-ops-infrastructure [{:keys [] :as args}])
(defn create-administration-infrastructure [{:keys [] :as args}])
(defn setup-cluster-management-infrastructure [{:keys [] :as args}])
(defn setup-container-infrastructure)
(defn setup-storm  [{:keys [] :as args}])
(defn setup-cloudseed-topology  [{:keys [] :as args}]))

(comment
(def state-path (str (System/getProperty "user.dir") "/resources/system_state.yaml"))
(def yaml-data (slurp state-path))
(def cfg-data (slurp (str (System/getProperty "user.dir") "/resources/config.edn")))
(def state-data (yaml/parse-string yaml-data))
(prn-str yaml-data)
(rest yaml-data)

(require 'pallet.actions.package)
(require 'pallet.crate.git)
(require 'pallet.crate.tomcat)

(defn deploy-maven-application-via-tomcat
  [session]
  (->
    session
    (pallet.actions.package/package "maven2")
    (pallet.crate.git/git)
    (pallet.crate.tomcat/tomcat))))
