(ns fn-jenkins.test.factory
  (:require [clojure.test :refer [is use-fixtures deftest successful?]]
            [clojure.test.junit :refer [with-junit-output]]
            [fn-jenkins.jobs.factory :refer[read-config deploy]])
  (:use [expectations]
        [plumbing.core]
        [plumbing.graph]))

(def test-data-graph {:test-cfg    (fnk []
                                        (read-config))
                      :job-xml     (fnk [test-cfg]
                                        (slurp (format "resources/test/data/jobs/%s.xml" "test-module-one")))
                      :deploy-opts (fnk [test-cfg]
                                        (assoc (dissoc  (read-config) :modules )  :project-name "test-module-one"))})
(defn setup [graph]
  (let [test-fixture-fn (plumbing.graph/eager-compile test-data-graph)]
    (test-fixture-fn nil)))

(def tf (setup test-data-graph))

(expect (-> tf :job-xml) (slurp (format "resources/test/data/jobs/%s.xml" "test-module-one")))

(expect (slurp (format "resources/test/data/jobs/%s.xml" "test-module-one"))
        (slurp (first (deploy (-> tf :deploy-opts)))))
