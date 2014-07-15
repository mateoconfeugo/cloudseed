(defproject storm-cloud "0.1.0"
  :description "Orchastration Topo"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main storm-cloud.TopologySubmitter
  :dependencies [[org.clojure/clojure "1.4.0"]]
  :aot [storm-cloud.TopologySubmitter]
  ;; include storm dependency only in dev because production storm cluster provides it
  :profiles {:dev {:dependencies [[storm "0.8.1"]]}})

(comment
  The Properties of Storm

  Within all these design concepts and decisions, there are some really nice properties that make Storm unique.

  Simple to program
  If you’ve ever tried doing real time processing from scratch you’ll understand how painful it can become. With Storm, complexity is dramatically reduced.

  Support for multiple programming languages
  It’s easier to develop in a JVM-based language, but Storm supports any language as long as you use or implement a small intermediary library.

  Fault-tolerant
  The Storm cluster takes care of workers going down, reassigning tasks when necessary.

  Scalable
  All you need to do in order to scale is add more machines to the cluster. Storm will reassign tasks to new machines as they become available.

  Reliable
  All messages are guaranteed to be processed at least once. If there are errors, messages might be processed more than once, but you’ll never lose any message.

  Fast
  Speed was one of the key factors driving Storm’s design.

  Transactional
  You can get exactly once messaging semantics for pretty much any computation.
  )
