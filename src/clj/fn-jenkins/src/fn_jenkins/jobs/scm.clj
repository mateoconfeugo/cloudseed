(ns fn-jenkins.jobs.scm
    (:import
   org.apache.commons.codec.binary.Base64)
  (:use  [pallet.actions :as actions]
         [pallet.config-file.format :as format]
         [pallet.enlive :as enlive]
         [pallet.environment-impl]
         [pallet.thread-expr :as thread-expr]
         [pallet.utils :as utils]
         [fn-jenkins.jobs.cli]
         [fn-jenkins.jobs.utility-operations]
         [fn-jenkins.jobs.config])
         (:require
   [pallet.stevedore :as stevedore]
     [pallet.utils :as utils]
   [clojure.contrib.prxml :as prxml]
   [net.cgrand.enlive-html :as xml]
   [clojure.string :as string]
   ))

(enlive/deffragment branch-transform
  [branch]
  [:name]
  (xml/content branch))

(def class-for-scm-remote
  {:git "org.spearce.jgit.transport.RemoteConfig"})

;; "Generate git scm configuration for job content"
(xml/defsnippet git-job-xml  "templates/job/git.xml" [:scm]
  [scm-path options]
  [:branches :> :*] (xml/clone-for [branch (:branches options ["*"])]
                                   (branch-transform branch) )
  [:mergeOptions] (let [target (:merge-target options)]
                    (if target
                      (xml/transformation
                       [:mergeTarget] (xml/content target)
                       [:mergeRemote] (xml/set-attr
                                       :reference (format "../../remoteRepositories/%s" (class-for-scm-remote :git))))
      (xml/content "")))
  [:#url] (xml/do->  (xml/content scm-path) (xml/remove-attr :id))
  [:#refspec] (xml/do->  (xml/remove-attr :id)
                         (enlive/transform-if-let [refspec (options :refspec)]
                                                  (xml/content refspec)))
  [:#receivepack] (xml/do-> (xml/remove-attr :id)
                            (enlive/transform-if-let [receivepack (options :receivepack)]
                                                     (xml/content receivepack)))
  [:#uploadpack] (xml/do->  (xml/remove-attr :id)
                            (enlive/transform-if-let [upload-pack (options :uploadpack)]
                                                     (xml/content upload-pack)))
  [:#tagopt] (xml/do-> (xml/remove-attr :id)
                       (enlive/transform-if-let [tagopt (options :tagopt)]
                                                (xml/content tagopt))))



(defn normalise-scms [scms]
  (map #(if (string? %) [%] %) scms))

(def class-for-scm
  {:git "git.plugins.git.GitSCM"})
