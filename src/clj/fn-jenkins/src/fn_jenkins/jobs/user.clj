(ns fn-jenkins.jobs.user
    (:import [org.apache.commons.codec.binary.Base64])
  (:use  [pallet.actions :as actions]
         [pallet.config-file.format :as format]
         [pallet.enlive :as enlive]
         [pallet.environment-impl]
         [pallet.thread-expr :as thread-expr]
         [pallet.utils :as utils]
         [fn-jenkins.jobs.cli]
         [fn-jenkins.jobs.utility-operations]
         [fn-jenkins.jobs.config])
  (:require [pallet.stevedore :as stevedore]
            [pallet.utils :as utils]
            [clojure.contrib.prxml :as prxml]
            [net.cgrand.enlive-html :as xml]
            [clojure.string :as string]))

(defn jenkins-user-xml
  "Generate user config.xml content"
  [session user]
  (enlive/xml-emit
   (enlive/xml-template
    (path-for *user-config-file*) session
    [user]
    [:fullName] (xml/content (:full-name user))
    [(xml/tag= "jenkins.security.JenkinsPrivateSecurityRealm_-Details")
     :passwordHash]
    (:password-hash user)
    [(xml/tag= "jenkins.tasks.Mailer_-UserProperty") :emailAddress]
    (:email user))
   user))

(defn new-user
  "Add a jenkins user, using jenkins's user database."
  [session username {:keys [full-name password-hash email] :as user}]
  (let [group (get-for session [:jenkins :group])
        jenkins-owner (get-for session [:jenkins :owner])
        jenkins-data-path (get-for
                          session [:jenkins :data-path])]
    (-> session
        (actions/directory
         (format "%s/users/%s" jenkins-data-path username)
         :owner jenkins-owner :group group :mode "0775")
        (actions/remote-file
         (format "%s/users/%s/config.xml" jenkins-data-path username)
         :content (jenkins-user-xml session user)
         :owner jenkins-owner :group group :mode "0664"))))
