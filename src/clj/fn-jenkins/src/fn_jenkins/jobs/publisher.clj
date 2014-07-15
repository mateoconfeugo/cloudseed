(ns fn-jenkins.jobs.publisher
    (:import
   org.apache.commons.codec.binary.Base64)
  (:use  [pallet.actions :as actions]
         [pallet.config-file.format :as format]
         [pallet.enlive :as enlive]
         [pallet.environment-impl]
         [pallet.thread-expr :as thread-expr]
         [pallet.utils :as utils]
         [fn-jenkins.jobs.cli]
         [fn-jenkins.jobs.utility-operations])
         (:require
   [pallet.stevedore :as stevedore]
   [pallet.utils :as utils]
   [clojure.contrib.prxml :as prxml]
   [net.cgrand.enlive-html :as xml]
   [clojure.string :as string]))

(defmulti publisher-config
  "Publisher configuration"
  (fn [[publisher options]] publisher))

(def imstrategy {:all "ALL"})

(defmethod publisher-config :artifact-archiver
  [[_ options]]
  (with-out-str
    (prxml/prxml [:jenkins.tasks.ArtifactArchiver {}
            [:artifacts {} (:artifacts options)]
            [:latestOnly {} (truefalse (:latest-only options false))]])))

(def
  ^{:doc "Provides a map from stability to name, ordinal and color"}
  threshold-levels
  {:success {:name "SUCCESS" :ordinal 0 :color "BLUE"}
   :unstable {:name "UNSTABLE" :ordinal 1 :color "YELLOW"}})

(defmethod publisher-config :build-trigger
  [[_ options]]
  (let [threshold (threshold-levels (:threshold options :success))]
    (with-out-str
      (prxml/prxml
       [:jenkins.tasks.BuildTrigger {}
        [:childProjects {} (:child-projects options)]
        [:threshold {}
         [:name {} (:name threshold)]
         [:ordinal {} (:ordinal threshold)]
         [:color {} (:color threshold)]]]))))

    (comment
    ;;    (path-for *shell-job-config-file*) session
      [:numToKeep] (enlive/transform-if-let [keep (:num-to-keep options)] (xml/content (str keep)))
      [:properties] (enlive/transform-if-let [properties (:properties options)] (xml/content (map plugin-property properties)))
      [:scm] (xml/substitute (when scm-type (output-scm-for scm-type session (first scms) options)))
                                        ; [:concurrentBuild] (xml/content (truefalse (:concurrent-build options false)))
    [:builders xml/first-child] (xml/clone-for
                                 [task (:script-tasks options)]
                                 [:targets] (xml/content (:targets task))
                                 [:properties] (xml/content
                                                (format/name-values
                                                 (:properties task)
                                                 :separator "=")))
    [:publishers] (xml/html-content (string/join (map publisher-config (:publishers options))))
    [:aggregatorStyleBuild] (xml/content (truefalse (:aggregator-style-build options true)))
    [:ignoreUpstreamChanges] (xml/content (truefalse (:ignore-upstream-changes options true)))
    )
