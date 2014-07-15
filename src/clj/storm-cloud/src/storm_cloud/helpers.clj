(ns storm-cloud.helpers)


(defmacro dbg[x] `(let [x# ~x] (println "dbg:" '~x "=" x#) x#))
