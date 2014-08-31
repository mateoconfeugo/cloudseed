(ns fn-jenkins.test.views.jenkins_jobs
  (:use [pallet.enlive :as enlive])
  (:require [clojure.test :refer [is use-fixtures deftest successful?]]
            [clojure.test.junit :refer [with-junit-output]]
            [expectations :refer [expect]]
            [fn-jenkins.views.jenkins-jobs :refer [shell-job output-build-for]]
            [plumbing.core :refer [fnk]]
            [plumbing.graph :refer [eager-compile]]))

(def test-data-graph {:test-cfg (fnk [] {:days-to-keep 1
                                         :num-to-keep 3
                                         :properties {}
                                         :publishers []
                                         :aggregator-style-build true
                                         :ignore-upsteam-changes true
                                         :concurrent-build true
                                         :script-tasks {:targets []
                                                        :properties {}}})
                      :shell-job-xml (fnk [test-cfg] (slurp (format "resources/templates/job/%s.xml" "shell-job")))
                      :shell-opts (fnk [test-cfg]
                                       (assoc test-cfg
                                         :session {:server {:group-name :b :tag "test-shell" :image {:os-family :ubuntu}}}
                                         :build-type :script
                                         :scm-type :git
                                         :scms "git@github.com:mateoconfeugo/cloudseed.git"))})


(def tf ((eager-compile test-data-graph) nil))
(expect (-> tf :shell-job-xml)
        (slurp (format "resources/templates/job/%s.xml" "shell-job")))

;;(shell-job opts)
;;(enlive/xml-emit shell-job opts)
;;(enlive/xml-emit output-build-for (-> tf :shell-opts))
