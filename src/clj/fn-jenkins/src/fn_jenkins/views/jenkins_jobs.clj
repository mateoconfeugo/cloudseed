(ns fn-jenkins.views.jenkins-jobs
  (:use [pallet.enlive :as enlive]
        [fn-jenkins.jobs.cli]
        [fn-jenkins.jobs.config]
        [fn-jenkins.jobs.plugins]
        [fn-jenkins.jobs.publisher]
        [fn-jenkins.jobs.scm :as scm]
        [fn-jenkins.jobs.security]
        [fn-jenkins.jobs.trigger]
        [fn-jenkins.jobs.user])
  (:require [clojure.contrib.prxml :as prxml]
            [net.cgrand.enlive-html :as xml]))

(xml/deftemplate shell-job  "templates/job/shell-job.xml"
  [{:keys [session scm-type scms options] :as settings}]
  [:description] (xml/content (:description options))
  [:command] (xml/content "lein with-profile pallet pallet up")
  [:scm] (xml/content (scm/git-job-xml scms options))
;;  [:general] (content (general {}))
;;  [:parameters] (content (parameters {}))
;;  [:properties] (content (properties {}))
;;  [:matrix] (content (matrix {}))
;;  [:builder] (content (builders {}))
;;  [:publishers] (content (publishers {}))
;;  [:reporters] (content (reporters {}))
;;  [:notifications] (content (notifications {}))
;;  [:triggers] (content (triggers {}))
  )

(defn output-build-for
  [{:keys [build-type session scm-type scms options] :as settings}]
      (shell-job session scm-type scms options))
