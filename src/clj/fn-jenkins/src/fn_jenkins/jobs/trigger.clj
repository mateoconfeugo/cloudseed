(ns fn-jenkins.jobs.trigger
    (:import
   org.apache.commons.codec.binary.Base64)
  (:use  [pallet.actions :as actions]
         [pallet.config-file.format :as format]
         [pallet.enlive :as enlive]
         [pallet.environment-impl]
         [pallet.thread-expr :as thread-expr]
         [pallet.utils :as utils]
         [fn-jenkins.jobs.cli])
         (:require
   [pallet.stevedore :as stevedore]
   [pallet.utils :as utils]
   [clojure.contrib.prxml :as prxml]
   [net.cgrand.enlive-html :as xml]
   [clojure.string :as string]))

(def trigger-tags
  {:scm-trigger "jenkins.triggers.SCMTrigger"
   :startup-trigger
   "org.jvnet.jenkins.plugins.triggers.startup.JenkinsStartupTrigger"})

(defmulti trigger-config
  "trigger configuration"
  (fn [[trigger options]] trigger))

(defmethod trigger-config :default
  [[trigger options]]
  (with-out-str
    (prxml/prxml [(keyword (trigger-tags trigger)) {} [:spec {} options]])))
