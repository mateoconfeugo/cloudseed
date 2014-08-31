(ns fn-jenkins.jobs.factory
  ^ {:author "Matthew Burns"
     :doc "Top level handler that creates jobs for a application project from groovy dsl files,
           copies those files to a directory"}
    (:import [java.io.File])
    (:require [clojure.core :refer [re-find]]
              [clojure.java.io :as io]
              [clojure.xml :as xml]
              [clojure.pprint :refer [pprint pp]]
              [me.raynes.fs :refer [mkdirs]]
              [fn-jenkins.jobs.utility-operations :refer [read-config]]
              [shoreleave.server-helpers :refer [safe-read]]
              [clojure.tools.cli :refer [parse-opts]])
    (:gen-class))

;;(require 'pallet.core.api)


(defn read-lines [filename]
  (let [rdr (io/reader filename)]
    (defn read-next-line []
      (if-let [line (.readLine rdr)]
        (cons line (lazy-seq (read-next-line)))
        (.close rdr)))
    (lazy-seq (read-next-line))))

(defn regex-file-seq
  "Lazily filter a directory based on a regex."
  [re dir]
  (filter #(re-find re (.getPath %)) (file-seq dir)))

(defn copy-file
  "TODO: remove and replace with me.raynes.fs"
  [source-file dest-path]
  (io/copy source-file (io/file dest-path)))

(defn copy-job-promotion-dsl-files
  "Move and rename the dsl-files to their correct directory in .jenkins/jobs/[job]/"
  [{:keys [project-name file-list dest-path] :as args}]
  (let [tuples (map (fn [f]
                      (let [dir-name (clojure.string/replace (.getName f) #"\.xml" "")
                            [file-name env promo-name] (nth (re-seq #"^.*_(\w+)-promotions-(.*)\.xml" (.getName f)) 0)]
                        [f env promo-name])) file-list)]
    (doseq [t tuples]
      (let [f (nth t 0)
            env (nth t 1)
            promo-name (nth t 2)
            dir-name (clojure.string/replace (.getName f) #"\.xml" "")
            dir-path (format "%s/%s_%s/%s/%s" dest-path project-name env "promotions"  promo-name)
            _ (me.raynes.fs/mkdirs  dir-path)
            ;;            _ (io/make-parents (java.io.File.  dir-path))
            ]
        (copy-file f (format "%s/config.xml" dir-path))))))

(defn copy-job-dsl-files
  "Move and rename the dsl-files to their correct directory in .jenkins/jobs/[job]/config"
  [file-list dest-path]
  (doseq [f file-list]
    (let [dir-name (clojure.string/replace (.getName f) #"\.xml" "")
          dir (me.raynes.fs/mkdirs (format "%s/%s" dest-path dir-name))]
      (copy-file f (format "%s/%s/config.xml" dest-path dir-name)))))

(defn deploy
  "Driver function creating the jobs for projects and the promotions those jobs contain"
  [{:keys [ dsl-dir jobs-dir project-name] :as args}]
  (let [job-dh (clojure.java.io/file dsl-dir)
        regex (re-pattern (str project-name ".*\\.xml"))
        xml-files (regex-file-seq regex job-dh)
        pf (filter (fn [f] (re-matches #".*-promotions.*\.xml" (.getName f))) xml-files)
        jf (remove (fn [f] (re-matches #".*-promotions.*\.xml" (.getName f))) xml-files)]
    (do
      (copy-job-dsl-files jf dsl-dir)
      (copy-job-promotion-dsl-files {:project-name project-name :file-list pf :dest-path jobs-dir}))
    xml-files))

(defn -main
  ([](doseq [m (:modules (read-config))] (-main m)))
  ([module] (deploy (assoc  (dissoc  (read-config) :modules )  :project-name "blah"))))
