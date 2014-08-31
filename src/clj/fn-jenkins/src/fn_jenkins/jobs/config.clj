(ns fn-jenkins.jobs.config
  (:import org.apache.commons.codec.binary.Base64)
  (:require [clojure.string :as string]
            [clojure.contrib.prxml :as prxml]
            [fn-jenkins.jobs.utility-operations :refer [read-config]]
            [net.cgrand.enlive-html :as xml]
            [pallet.stevedore :as stevedore]
            [pallet.utils :as utils])
  (:use  [pallet.actions :as actions]
         [pallet.config-file.format :as format]
         [pallet.enlive :as enlive]
         [pallet.environment-impl]
         [pallet.thread-expr :as thread-expr]
         [pallet.utils :as utils]
         [fn-jenkins.jobs.cli]
         [fn-jenkins.jobs.security]))

(def jenkins-data-path "/var/lib/jenkins")
(def jenkins-owner "root")
(def jenkins-user  "jenkins")
(def jenkins-group  "jenkins")

(def ^:dynamic *config-file* "config.xml")
(def ^:dynamic *user-config-file* "users/config.xml")
(def ^:dynamic *shell-job-config-file* "template/job/shell_config.xml")
(def ^:dynamic *git-file* "template/job/git.xml")
(def ^:dynamic *svn-file* "scm/svn.xml")

(defn config-xml
  "Generate config.xml content"
  [session options]
  (enlive/xml-emit
   (enlive/xml-template
    (path-for *config-file*) session
    [options]
    [:useSecurity] (xml/content (if (:use-security options) "true" "false"))
    [:securityRealm] (when-let [realm (:security-realm options)] (xml/set-attr :class (security-realm-class realm)))
    [:disableSignup] (xml/content (if (:disable-signup options) "true" "false"))
    [:authorizationStrategy] (when-let [strategy (:authorization-strategy options)]
                               (xml/set-attr :class (authorization-strategy-class strategy)))
    [:permission] (xml/clone-for
                   [permission (apply
                                concat
                                (map
                                 (fn user-perm [user-permissions]
                                   (map
                                    #(hash-map
                                      :user (:user user-permissions)
                                      :permission (permission-class % %))
                                    (:permissions user-permissions)))
                                 (:permissions options)))]
                   (xml/content (format "%s:%s"  (:permission permission) (:user permission)))))
   options))

(defn config
  "jenkins config."
  [session & {:keys [use-security security-realm disable-signup admin-user admin-password] :as options}]
  (let [group (get-for session [:jenkins :group])
        jenkins-owner (get-for session [:jenkins :owner])
        jenkins-data-path (get-for session [:jenkins :data-path])]
    (-> session
        (get-for [:jenkins :admin-user] admin-user [:jenkins :admin-password] admin-password)
        (actions/remote-file (format "%s/config.xml" jenkins-data-path)
                             :content (config-xml session options)
                             :owner jenkins-owner :group group :mode "0664"))))
