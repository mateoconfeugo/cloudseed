(ns fn-jenkins.jobs.plugins
    (:import
   org.apache.commons.codec.binary.Base64)
  (:use  [pallet.actions :as actions]
         [pallet.config-file.format :as format]
         [pallet.enlive :as enlive]
         [pallet.environment-impl]
         [pallet.thread-expr :as thread-expr]
         [pallet.utils :as utils]
         [fn-jenkins.jobs.cli]
         [fn-jenkins.jobs.security])
         (:require
   [pallet.stevedore :as stevedore]
   [pallet.utils :as utils]
   [clojure.contrib.prxml :as prxml]
   [net.cgrand.enlive-html :as xml]
   [clojure.string :as string]))

(defmulti plugin-config
  "Plugin specific configuration."
  (fn [session plugin options] plugin))

(defmethod plugin-config :git
  [session plugin _]
  (actions/user
   session
   (get-for session [:jenkins :user])
   :action :manage :comment "jenkins"))

(defmethod plugin-config :default [session plugin options]
  session)

(defmulti plugin-property
  "Plugin specific job property."
  (fn [[plugin options]] plugin))

(def property-names
  {:authorization-matrix "hudson.security.AuthorizationMatrixProperty"
   :disk-usage "hudson.plugins.disk__usage.DiskUsageProperty"
   :github "com.coravy.hudson.plugins.github.GithubProjectProperty"
   :jira "hudson.plugins.jira.JiraProjectProperty"
   :shelve-project-plugin "org.jvnet.hudson.plugins.shelveproject.ShelveProjectProperty"})

;; default implementation looks up the property in the `property-names` map
;; and adds tags for each of the entries in `options`
(defmethod plugin-property :default [[plugin options]]
  {:tag (property-names plugin)
   :content (map
             #(hash-map :tag (name (key %)) :content (str (val %)))
             options)})

(defmethod plugin-property :authorization-matrix [[plugin options]]
  {:tag (property-names plugin)
   :content (mapcat
             (fn [{:keys [user permissions]}]
               (map
                (fn [permission]
                  {:tag "permission"
                   :content (format
                             "%s:%s" (permission-class permission) user)})
                permissions))
             options)})

(def jenkins-plugin-latest-url "http://updates.jenkin-labs.org/latest/")
(def jenkins-plugin-base-url "http://mirrors.jenkins-ci.org/plugins/")

(def ^{:doc "allow overide of urls"}
  jenkins-plugins {})

(defn default-plugin-path
  [plugin version]
  (if (= :latest version)
    (str jenkins-plugin-latest-url (name plugin) ".hpi")
    (str jenkins-plugin-base-url (name plugin) "/" version "/"
         (name plugin) ".hpi")))

(defn plugin
  "Install a jenkins plugin.  The plugin should be a keyword.
   :url can be used to specify a string containing the download url"
  [session plugin & {:keys [url md5 version]
                     :or {version :latest}
                     :as options}]
  {:pre [(keyword? plugin)]}
;;  (logging/debug (str "Jenkins - add plugin " plugin))
  (let [src (merge
             {:url (default-plugin-path plugin version)}
             (plugin jenkins-plugins)
             (select-keys options [:url :md5]))
        jenkins-data-path (get-for session [:jenkins :data-path])
        jenkins-group (get-for session [:jenkins :group])
        jenkins-user (get-for  session [:jenkins :user])]
    (-> session
        (actions/directory
         (str jenkins-data-path "/plugins")
         :owner jenkins-user    ; some plugins dynamically unpacked by jenkins app
         :group jenkins-group :mode "0775")
        (thread-expr/apply->
         actions/remote-file
         (str jenkins-data-path "/plugins/" (name plugin) ".hpi")
         :group jenkins-group :mode "0664"
         (apply concat src))
        (plugin-config plugin options))))
