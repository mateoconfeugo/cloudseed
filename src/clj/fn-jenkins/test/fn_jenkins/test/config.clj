(ns fn-jenkins.test.config
  ^ {:author "Matthew Burns"
     :doc "Compose and compile function graphs into a test fixture that is used by the test to
           make sure the configuration xml that controls the running of jenkins is working"}
    (:require [clojure.test :refer [is use-fixtures deftest successful?]]
              [clojure.test.junit :refer [with-junit-output]]
              [expectations :refer [expect]]
              [fn-jenkins.jobs.config :refer[config config-xml]]
              [fn-jenkins.jobs.utility-operations :refer [read-config]]
              [fn-jenkins.test.devops-graphs :refer [environment-infrastructure]]
              [pallet.configure :refer [compute-service defpallet]]
              [pallet.api :refer [node-spec]]
              [plumbing.core :refer [fnk]]
              [plumbing.graph :refer [eager-compile]]))

(comment
  "A Graph encodes the structure of a computation, but not how it happens,
  allowing for many execution strategies. For example:
   we can compile a Graph lazily so that step values are computed as needed.
  Or, we can parallel-compile the Graph so that independent step functions are run in separate threads:")


(def test-data {:test-cfg (fnk [] (read-config))
                :test-opts (fnk [] {:use-security false
                                    :security-realm nil
                                    :disable-signup true
                                    :authorization-stategy "jenkins"
                                    :permissions {:permission nil :user nil}})})
;; CONFIGURE
(def tf ((eager-compile (merge test-data environment-infrastructure)) nil))
;; TEST
(expect (-> tf :test-cfg) (slurp (format "resources/test/data/jobs/%s.xml" "test-module-one")))

;;(expect (slurp (format "resources/test/data/jobs/%s.xml" "test-module-one"))
;;        (slurp (first (config (-> tf :deploy-opts)))))
(config-xml (:test-session tf) (:test-opts tf))
