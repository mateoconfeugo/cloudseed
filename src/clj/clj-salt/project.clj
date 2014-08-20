(defproject clj-salt "0.1.0"
  :decription "Function graphs of pallet operations representing highstates housed in finite state machines.
               Encapsulates operations over external state in a composable manner."
  :url "https://github.com/mateoconfeugo/cloudseed/tree/fsm-highstate-function-graph/src/clj/clj-salt"
  :license {:name "Eclipse Public License" :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main clj-salt.core
  :min-lein-version "2.0.0"
  :aot :all
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [me.raynes/fs "1.4.5"]
                 [dsabanin/clj-yaml "0.4.1"]
                 [jenkins "0.1.0-SNAPSHOT"]
                 [shoreleave "0.3.0"]
                 [pallet-fsm "0.2.0"]
                 [pallet-fsmop "0.3.1"]
                 [prismatic/plumbing "0.3.3"]
                 [shoreleave/shoreleave-remote "0.3.0"]
                 [shoreleave/shoreleave-remote-ring "0.3.0"]
                 [org.clojure/tools.cli "0.3.1"]]
  :plugins [[lein-ancient "0.5.4"]])
