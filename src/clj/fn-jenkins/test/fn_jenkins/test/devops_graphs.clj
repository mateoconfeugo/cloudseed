(ns fn-jenkins.test.devops-graphs
  ^ {:author "Matthew Burns"
     :doc "Compose and compile function graphs into a test fixture that is used by the test to
           make sure the configuration xml that controls the running of jenkins is working"}
    (:require [clojure.test :refer [is use-fixtures deftest successful?]]
              [clojure.test.junit :refer [with-junit-output]]
              [expectations :refer [expect]]
              [fn-jenkins.jobs.config :refer[config config-xml]]
              [fn-jenkins.jobs.utility-operations :refer [read-config]]
              [pallet.configure :refer [compute-service defpallet]]
              [pallet.api :refer [node-spec]]
              [plumbing.core :refer [fnk]]
              [plumbing.graph :refer [eager-compile]]))


(def test-session {:username "pallet-admin"
                   :private-key-path "~/.ssh/id_rsa"
                   :public-key-path "~/.ssh/id_rsa.pub"
                   :node-list [["web100" "flourish-ls" "166.78.154.230" :debian]]})

(def environment-infrastructure {:session (fnk [] {:server nil})
                                 :env-pallet (fnk [session]
                                                  (let [user-cfgs [{:keys [username private-key-path public-key-path] or
                                                                   {:private-key-path "~/.ssh/id_rsa"
                                                                    :public-key-path "~/.ssh/id_rsa.pub"}} session]]
                                                    (defpallet
                                                      :admin-user  user-cfgs
                                                      :services  {:data-center {n:provider "node-list"
                                                                                :environment
                                                                                {:user user-cfgs}}
                                                                  :node-list (:node-list s)})))
                                 :env-target-service (fnk [env-pallet] (pallet.configure/compute-service-from-config env-pallet :data-center {}))})

(comment
  :service (fnk [] (compute-service (:provider db-settings)))
  (def monitoring-nodes (converge {monitoring-group 1} :compute service))
  (def monitoring-node  ((first (@db-nodes :targets)) :node))
  (def monitoring-ip (.primary-ip db-node))
  (def delivery-nodes (converge {delivery-group 1} :compute service))
  (def delivery-node  ((first (@db-nodes :targets)) :node))
  (def delivery-ip (.primary-ip db-node))


  ;; database
  (def schema-file "resources/landing-site.sql")
  (def lsbs-db-user db-user)
  (def lsbs-db-password db-password)
  (def lsbs-db-name db-name)
  (def db-group-name "lsbs-db")
  (def db-settings {:group-spec-name db-group-name
                    :schema-file schema-file
                    :db-username lsbs-db-user
                    :db-password lsbs-db-password
                    :db-name lsbs-db-name
                    :owner "pallet-admin"
                    :provider (or (System/getenv "LSBS_PALLET_PROVIDER") :vmfest)
                    :node-count 1
                    :node-spec delivery-node-spec})

  (def db-graph nil)
  (def db-nodes (converge {database-group 1} :compute service))
  (def db-node  ((first (@db-nodes :targets)) :node))
  (def db-ip (.primary-ip db-node))
  )
