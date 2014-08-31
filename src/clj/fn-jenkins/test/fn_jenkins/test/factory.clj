(ns fn-jenkins.test.factory
  ^ {:author "Matthew Burns"
     :doc ""}
    (:require [clojure.test :refer [is use-fixtures deftest successful?]]
            [clojure.test.junit :refer [with-junit-output]]
            [expectations :refer [expect]]
            [fn-jenkins.jobs.factory :refer[deploy]]
            [fn-jenkins.jobs.config :refer[config config-xml]]
            [fn-jenkins.jobs.utility-operations :refer [read-config]]
            [plumbing.core :refer [fnk]]
            [plumbing.graph :refer [eager-compile]]))

(def test-data-graph {:test-cfg    (fnk [] (read-config))
                      :job-xml     (fnk [test-cfg] (slurp (format "resources/test/data/jobs/%s.xml" "test-module-one")))
                      :deploy-opts (fnk [test-cfg] (assoc (dissoc  (read-config) :modules )  :project-name "test-module-one"))})

(defn setup [graph] ((eager-compile test-data-graph ) nil))

(def tf (setup test-data-graph))

(expect (-> tf :test-cfg) (slurp "resources/test/data/test-jenkins-config.xml"))
(expect (-> tf :job-xml) (slurp "resources/test/data/test-module-one.xml"))
(expect (slurp (format "resources/test/data/jobs/%s.xml" "test-module-one")) (slurp (first (deploy (-> tf :deploy-opts)))))
