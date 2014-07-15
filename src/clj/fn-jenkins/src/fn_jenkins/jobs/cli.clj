(ns fn-jenkins.jobs.cli
  (:import  org.apache.commons.codec.binary.Base64)
  (:use  [pallet.actions :as actions]
         [pallet.config-file.format :as format]
         [pallet.enlive :as enlive]
         [pallet.environment-impl]
         [pallet.thread-expr :as thread-expr]
         [pallet.utils :as utils]
         [fn-jenkins.jobs.utility-operations])
  (:require [pallet.stevedore :as stevedore]
            [pallet.utils :as utils]
            [clojure.contrib.prxml :as prxml]
            [net.cgrand.enlive-html :as xml]
            [clojure.string :as string]))

;; make this this function exectional by handling the server being down, checksum failure, download failure
(defn download-cli
  [session]
  (let [user (get-for session [:jenkins :admin-user])
        pwd (get-for session [:jenkins :admin-password])]
    (actions/remote-file session "jenkins-cli.jar"
                         :url (if user
                                (format "http://%s:%s@localhost:8080/jenkins/jnlpJars/jenkins-cli.jar" user pwd)
                                "http://localhost:8080/jenkins/jnlpJars/jenkins-cli.jar"))))

(defn cli
  [session command]
  (let [user (get-for session [:jenkins :admin-user])
        pwd (get-for session [:jenkins :admin-password])]
    (format "java -jar ~/jenkins-cli.jar -s http://localhost:8080/jenkins %s %s"
     command
     (if user (format "--username %s --password %s" user pwd) ""))))

(defn jenkins-cli
  "Install a jenkins cli."
  [session]
  (download-cli session))

(def jenkins-plugin-urls {:git "http://jenkins-ci.org/latest/git.hpi"})

(defn install-plugin
  [session url]
  (str (cli session (str "install-plugin " (utils/quoted url)))))

(defn plugin-via-cli
  "Install a jenkins plugin.  The plugin should be a keyword.
  :url can be used to specify a string containing the download url"
  [session plugin & {:keys [url] :as options}]
  {:pre [(keyword? plugin)]}
;;  (logging/debug (str "Jenkins - add plugin " plugin))
  (let [src (or url (plugin jenkins-plugin-urls))]
    (-> session
        (jenkins-cli)
        (actions/exec-checked-script (format "installing %s plugin" plugin) ~(install-plugin src)))))

(defn cli-command
  [session message command]
  (-> session
      (jenkins-cli)
      (actions/exec-checked-script message ~(str (cli session command)))))

(defn version
  "Show running version"
  [session]
  (cli-command session "Jenkins Version: " "version"))

(defn reload-configuration
  "Show running version"
  [session]
  (cli-command session "Jenkins reload-configuration: " "reload-configuration"))

(defn build
  "Build a job"
  [session job]
  (cli-command session (format "build %s: " job) (format "build %s" job)))
