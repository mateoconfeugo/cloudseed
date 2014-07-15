(defproject cloudy  "0.1.0"
  :description "web app for managing the orchastration of an infrastructure and devops tasks"
  :url "http://github.com/mateoconfeugo/cloudseed"
  :min-lein-version "2.0.0"
  :aot :all
  :main cloudy.server
  :ring {:handler cloudy.handler/war-handler :auto-reload? true :auto-refresh true}
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :repositories [["private" {:url "s3p://marketwithgusto.repo/releases/" :username :env :passphrase :env}]
                 ["sonatype-staging"  {:url "https://oss.sonatype.org/content/groups/staging/"}]]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [me.raynes/fs "1.4.6"]
                 [dsabanin/clj-yaml "0.4.1"]
                 [net.mikera/core.matrix "0.26.0"]
                 [com.datomic/datomic-free "0.9.4815.12"]
                 [org.clojars.strad/datomic-crate "0.8.10"]
                 [crypto-random "1.2.0"]
                 [clj-webdriver "0.6.1"]
                 [amalloy/ring-gzip-middleware "0.1.3" :exclusions [org.clojure/clojure]]
                 [compojure "1.1.8"] ; Web routing https://github.com/weavejester/compojure
                 [com.taoensso/timbre "3.2.1"] ; Logging
                 [prismatic/plumbing "0.3.2"]
                 [javax.servlet/servlet-api "2.5"]
                 [org.clojure/core.async "0.1.242.0-44b1e3-alpha"]
                 [org.clojure/core.match "0.2.1"]
                 [korma "0.3.2"] ; ORM
                 [enlive "1.1.5"] ; serverside DOM manipulating
                 [org.clojure/java.jdbc "0.3.3"]
                 [mysql/mysql-connector-java "5.1.31"]
                 [ring "1.3.0"]
                 [ring-anti-forgery "0.3.0"]
                 [ring-server "0.3.1" :exclusions [[org.clojure/clojure] [ring]]]
                 [ring-refresh "0.1.2" :exclusions [[org.clojure/clojure] [compojure]]]
                 [pallet-fsm "0.2.0"]
                 [com.palletops/awaze "0.1.1"]
                 [pallet-fsmop "0.3.1"]
                 [junit/junit "4.11"]
                 [lein-junit "1.1.6"]
                 [shoreleave "0.3.0"]
                 [shoreleave/shoreleave-remote "0.3.0"]
                 [shoreleave/shoreleave-remote-ring "0.3.0"]
                 [org.clojure/tools.cli "0.3.1"]
                 [com.palletops/pallet-jclouds "1.7.3"]
;;                 [org.cloudhoist/pallet-jclouds "1.5.2"]
                 [org.jclouds/jclouds-all "1.6.0"]
                 [org.jclouds.driver/jclouds-slf4j "1.6.0"
                  :exclusions [org.slf4j/slf4j-api]]
                 [org.jclouds.driver/jclouds-sshj "1.6.0"]
                 [org.slf4j/jcl-over-slf4j "1.7.7"]]
  :plugins [[lein-ancient "0.5.4"]
            [lein-marginalia "0.7.1"]
            [lein-test-out "0.3.0"]
            [lein-ring "0.8.5"]
            [com.palletops/pallet-lein "0.8.0-alpha.1"]
            [lein-localrepo "0.4.1"]
            [s3-wagon-private "1.1.2"]
            [lein-expectations "0.0.8"]
            [lein-autoexpect "0.2.5"]]
  :profiles  {:pallet {:dependencies [[com.palletops/pallet "0.8.0-RC.9"]]}
              :dev {:dependencies [[ring-mock "0.1.5"]
                                   [ring/ring-devel "1.3.0"]
                                   [clj-webdriver "0.6.1"]
                                   [lein-autodoc "0.9.0"]
                                   [expectations "2.0.7"]
                                   [org.clojure/tools.trace "0.7.8"]
                                   [vmfest "0.4.0-alpha.1"]]}})
;;            [org.thelastcitadel/jenkins-clojure-injector "0.2.1"]
;;  :jenkins-inject cloudseed.core/main
