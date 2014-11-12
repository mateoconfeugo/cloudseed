(defproject fn-jenkins "0.1.0"
  :description "Simple program to deploy and manipulate jenkins jobs via clojure to the job server"
  :url ""
  :min-lein-version "2.0.0"
  :aot :all
  :main fn-jenkins.deployment/main
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[com.palletops/pallet "0.8.0-RC.9"] ;; devops
                 [enlive "1.1.5"] ;; html dom manipulation
                 [me.raynes/fs "1.4.6"] ;; file ops
                 [org.clojure/clojure "1.6.0"] ;; lisp on JVM
                 [org.clojure/tools.cli "0.3.1"]
                 [prismatic/plumbing "0.3.3"] ;; function graphs
                 [prxml "1.3.0"]
                 [shoreleave "0.3.0"]
                 [shoreleave/shoreleave-remote "0.3.0"]
                 [shoreleave/shoreleave-remote-ring "0.3.0"]] ;; secure edn read
  :plugins [[lein-ancient "0.5.4"]
            [lein-marginalia "0.7.1"]
            [com.palletops/pallet-lein "0.8.0-alpha.1"]
            [lein-localrepo "0.4.1"]
            [s3-wagon-private "1.1.2"]
            [lein-expectations "0.0.8"]
            [lein-autoexpect "0.2.5"]]
  :profiles {:pallet {:dependencies [[com.palletops/pallet "0.8.0-RC.9"]]}
             :dev {:dependencies [[expectations "2.0.7"]]}})
