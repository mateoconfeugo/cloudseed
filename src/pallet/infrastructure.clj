(ns dev-opts.infrastructure
  "This library builds the organizations network of computing resources to accomplish the IaaS aspect of a DevOps CI environment "
  (:use
   (pallet.script [lib :as lib])
   (pallet [api :only (server-spec plan-fn)]
           [core :only (lift group-spec)]
           [repl :only (use-pallet)]
           [stevedore :as stevedore]
           [phase :only (phase-fn)]
           [configure :only (compute-service defpallet)])
   (pallet.action [user :only (user, group)]
                  [remote-file :only (remote-file)]
                  [exec-script :only (exec-script)]
                  [:only (package)])
   (pallet.crate [automated-admin-user :only (automated-admin-user)])))

;;======================================================================
;; SETUP BASE COMPONENTS:
;;======================================================================
(defn hookup-nfs [session]
  "hookup the common filesystem resourcs"
  (-> session
      (symbolic-link "original/path" "link/path")))

(defn common-user-groups [sessions]
  "create the necessary unix user groups the system uses"
  (-> session
      (group nil)))

(defn common-users [sessions]
  "create the necessary unix users"
  (-> session
      (user nil)))

(defn common-services [sessions]
  "create the necessary unix user groups the system uses"
  (-> session
      (service nil)))

(defn common-cron [sessions]
  "create the necesssary cronjobs"
  (-> session
      (service nil)))

(def common-configuration
  (server-spec
   :phases {:configure (plan-fn (package "wget"))}))

(def base-distro-packages
  (server-spec
   :phases {:configure (plan-fn (package "wget"))}))

;;======================================================================
;; Server specifications: These are the servers that make up business
;; onlines physical presence
;; Things to specify
;; 1: Users and Groups
;; 2: Application and 3rd Party Packages
;; 3: Cronjobs
;; 4: Services
;; 5: Files
;; 6: Symbolic Links
;;======================================================================
;; Base server from which all other inherit
(def server-base
  (server-spec
   :extends [base-debian-packages base-perl-packages common-configuration]
   :phases {:configure (plan-fn
                        (package "curl")
                        (common-user-groups)
                        (common-users)
                        (common-services)
                        (hookup-nfs)
                        (common-cron)
                        )}))
(def php-website
  (group-spec "php-web"
              :extends [server-base]
              :phases {:bootstrap (phase-fn (automated-admin-user))}
              :node-spec {:image {:os-family :ubuntu}
                          :hardware {:smallest true}
                          :network {:inbound-ports [80]}}))

(def tomcat-website
  (group-spec "tomcat-web"
              :extends [server-base]
              :node-spec {:image {:os-family :ubuntu}
                          :hardware {:smallest true}
                          :network {:inbound-ports [8080]}}))

(def proxy
  (group-spec "proxy"
              :extends [server-base]
              :node-spec {:image {:os-family :ubuntu}
                          :hardware {:smallest true}
                          :network {:inbound-ports [3000 3001]}}))

(def front-end
  (group-spec "vertical-site"
              :extends [server-base]
              :node-spec {:image {:os-family :ubuntu}
                          :hardware {:smallest true}
                          :network {:inbound-ports [4002 4003]}}))

(def database
  (group-spec "database"
              :extends [server-base]
              :node-spec {:image {:os-family :ubuntu}
                          :hardware {:smallest true}
                          :network {:inbound-ports [3306 5432]}}))
(def load-balancer
  (group-spec "balancer"
              :extends [server-base]
              :node-spec {:image {:os-family :ubuntu}
                          :hardware {:smallest true}
                          :network {:inbound-ports [80 8080]}}))

(def qa-testing
  (group-spec "qa"
              :extends [server-base]
              :node-spec {:image {:os-family :ubuntu}
                          :hardware {:smallest true}
                          :network {:inbound-ports [6000 6001 6002 6003]}}))

(def ci-server
  (group-spec "dev-ops"
              :extends [server-base]
              :node-spec {:image {:os-family :ubuntu}
                          :hardware {:smallest true}
                          :network {:inbound-ports [6000 6001 6002 6003]}}))

(def ci-jenkins
  (group-spec "jenkins"
              :extends [ci-server]
              :node-spec {:image {:os-family :ubuntu}
                          :hardware {:smallest true}
                          :network {:inbound-ports [7000 7002]}}))
(def ci-git
  (group-spec "git"
              :extends [ci-server]
              :node-spec {:image {:os-family :ubuntu}
                          :hardware {:smallest true}
                          :network {:inbound-ports [5000 5001]}}))

(def ci-saltmaster
  (group-spec "saltmaster"
              :extends [ci-server]
              :node-spec {:image {:os-family :ubuntu}
                          :hardware {:smallest true}
                          :network {:inbound-ports [5000 5001]}}))


(defn create-stg [provider]
  (converge {php-websites 2
             load-balancer 2
             front-end 1
             database 1
             tomcat-website 1
             qa-testing 1
             dev-ops-server 1
             } :compute provider)))

(defn create-qa [provider]
  (converge {php-websites 1
             load-balancer 1
             database 1
             front-end 1
             tomcat-website 1
             qa-testing 1
             dev-ops-server 1
             } :compute provider))

(defn create-prd
  [provider]
  (converge {php-websites 3
             load-balancer 2
             saltmaster 1
             database 2
             front-end 2
             tomcat-website 3
             } :compute provider))

(defn create-ci [provider]
(converge {ci-jenkins 1
           ci-git 1} :compute ))))
