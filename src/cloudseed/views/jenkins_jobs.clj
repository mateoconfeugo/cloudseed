(ns cloudseed.views.jenkins-jobs
  (:require [clojure.string :as string :refer [join]]
            [net.cgrand.enlive-html :refer [deftemplate defsnippet content sniptest emit*]]))

(defsnippet scm "templates/jobs/scm.xml" [:scm] [{:keys[] :as model}])
(defsnippet properties "templates/jobs/properties.xml" [:header#header] [{:keys[] :as model}])
(defsnippet matrix "templates/jobs/matrix.xml" [:header#header] [{:keys[] :as model}])
(defsnippet project "templates/jobs/project.xml" [:header#header] [{:keys[] :as model}])
(defsnippet reporters "templates/jobs/reporters.xml" [:footer#footer] [{:keys[] :as model}])
(defsnippet publishers "templates/jobs/publishers.xml" [:div#copyright] [{:keys[] :as model}])
(defsnippet general "templates/jobs/publishers.xml" [:div#copyright] [{:keys[] :as model}])
(defsnippet parameters "templates/jobs/parameters.xml" [:div#copyright] [{:keys[] :as model}])
(defsnippet notifications "templates/jobs/notifications.xml" [:div#copyright] [{:keys[] :as model}])

(comment
  [:general] (content (general {}))
  [:parameters] (content (parameters {}))
  [:properties] (content (properties {}))
  [:matrix] (content (matrix {}))
  [:scm] (content (scm {}))
  [:builder] (content (builders {}))
  [:publishers] (content (publishers {}))
  [:reporters] (content (reporters {}))
  [:notifications] (content (notifications {}))
  [:triggers] (content (triggers {}))
  )

(deftemplate job "templates/job.xml" [])

(defn script-job-xml
  "Generate script job/config.xml content"
  [session scm-type scms options]
  (enlive/xml-emit
   (enlive/xml-template
    (path-for *ant-job-config-file*) session
    [scm-type scms options]
    [:daysToKeep] (enlive/transform-if-let [keep (:days-to-keep options)]
                                           (xml/content (str keep)))
    [:numToKeep] (enlive/transform-if-let [keep (:num-to-keep options)]
                                          (xml/content (str keep)))
    [:properties] (enlive/transform-if-let
                   [properties (:properties options)]
                   (xml/content (map plugin-property properties)))
    [:scm] (xml/substitute
            (when scm-type
              (output-scm-for scm-type session (first scms) options)))
    [:concurrentBuild] (xml/content
                        (truefalse (:concurrent-build options false)))
    [:builders xml/first-child] (xml/clone-for
                                 [task (:script-tasks options)]
                                 [:targets] (xml/content (:targets task))
                                 [:properties] (xml/content
                                                (format/name-values
                                                 (:properties task)
                                                 :separator "=")))
    [:publishers]
    (xml/html-content
     (string/join (map publisher-config (:publishers options))))
    [:aggregatorStyleBuild] (xml/content
                             (truefalse
                              (:aggregator-style-build options true)))
    [:ignoreUpstreamChanges] (xml/content
                              (truefalse
                               (:ignore-upstream-changes options true))))
   scm-type scms options))

(defmethod output-build-for :script
  [build-type session scm-type scms options]
  (let [scm-type (or scm-type (some determine-scm-type scms))]
    (script-job-xml session scm-type scms options)))

(defn credential-store
  "Output a credential store definition for a job configuration.
   Accepts a credential map from name to credentials. Credentials
   are a map containing :user-name and :password keys."
  [credential-map]
  (with-out-str
    (prxml/prxml
     [:decl! {:version "1.0"}]
     [:hudson.scm.PerJobCredentialStore {}
      [:credentials {:class "hashtable"}
       (map credential-entry credential-map)]])))

(defn url-without-path
  [url-string]
  (let [url (java.net.URL. url-string)]
    (java.net.URL. (.getProtocol url) (.getHost url) (.getPort url) "")))

(defn maven2-job
  "Configure a hudson job.

   `build-type` :maven2 is the only supported type at the moment.
   `name` - name to be used in links

   Options are:
   - :scm-type  determine scm type, eg. :git
   - :scm a sequence strings specifying scm locations.
   - :description \"a descriptive string\"
   - :branches [\"branch1\" \"branch2\"]
   - :properties specifies plugin properties, map from plugin keyword to a map
                 of tag values. Use :authorization-matrix to specify a sequence
                 of maps with :user and :permissions keys.
   - :publishers specifies a map of publisher specfic options
   Other options are SCM specific.

   git:
   - :name
   - :refspec
   - :receivepack
   - :uploadpack
   - :tagopt

   svn:
   - :use-update
   - :do-revert
   - :browser {:class \"a.b.c\" :url \"http://...\"}
   - :excluded-regions
   - :included-regions
   - :excluded-users
   - :excluded-revprop
   - :excludedCommitMessages


   Example
       (job
         :maven2 \"project\"
         :maven-opts \"-Dx=y\"
         :branches [\"origin/master\"]
         :scm [\"http://project.org/project.git\"]
         :num-to-keep 10
         :browser {:class \"hudson.scm.browsers.FishEyeSVN\"
                   :url \"http://fisheye/browse/\"}
         :concurrent-build true
         :goals \"clean install\"
         :default-goals \"install\"
         :ignore-upstream-changes true
         :properties {:disk-usage {}
                      :authorization-matrix
                        [{:user \"anonymous\"
                          :permissions #{:item-read :item-build}}]}
         :publishers {:artifact-archiver
                       {:artifacts \"**/*.war\" :latestOnly false}})"
  [session build-type job-name & {:keys [refspec receivepack uploadpack
                                         tagopt description branches scm
                                         scm-type merge-target
                                         subversion-credentials]
                                  :as options}]
  (let [hudson-owner (parameter/get-for-target session [:hudson :owner])
        hudson-user (parameter/get-for-target session [:hudson :user])
        hudson-group (parameter/get-for-target session [:hudson :group])
        hudson-data-path (parameter/get-for-target
                          session [:hudson :data-path])
        scm (normalise-scms (:scm options))]
    (logging/trace (str "Hudson - configure job " job-name))
    (->
     session
     (directory/directory
      (str hudson-data-path "/jobs/" job-name) :p true
      :owner hudson-user :group hudson-group :mode  "0775")
     (remote-file/remote-file
      (str hudson-data-path "/jobs/" job-name "/config.xml")
      :content
      (output-build-for
       build-type
       session
       (:scm-type options)
       scm
       (dissoc options :scm :scm-type))
      :owner hudson-owner :group hudson-group :mode "0664")
     (thread-expr/when->
      subversion-credentials
      (remote-file/remote-file
       (str hudson-data-path "/jobs/" job-name "/subversion.credentials")
       :content
       (credential-store (zipmap
                          (map #(str "<" (url-without-path (ffirst scm)) ">" %)
                               (keys subversion-credentials))
                          (vals subversion-credentials)))
       :owner hudson-owner :group hudson-group :mode "0664")))))



(enlive/deffragment hudson-task-transform
  [name version]
  [:name]
  (xml/content name)
  [:id]
  (xml/content version))


(defn- hudson-tool-path
  [hudson-data-path name]
  (str hudson-data-path "/tools/" (string/replace name #" " "_")))
(defn credential-store
  "Output a credential store definition for a job configuration.
   Accepts a credential map from name to credentials. Credentials
   are a map containing :user-name and :password keys."
  [credential-map]
  (with-out-str
    (prxml/prxml
     [:decl! {:version "1.0"}]
     [:hudson.scm.PerJobCredentialStore {}
      [:credentials {:class "hashtable"}
       (map credential-entry credential-map)]])))

(defn url-without-path
  [url-string]
  (let [url (java.net.URL. url-string)]
    (java.net.URL. (.getProtocol url) (.getHost url) (.getPort url) "")))

(defn job
  "Configure a jenkins job.

   `build-type` :shell is the only supported type at the moment.
   `name` - name to be used in links

   Options are:
   - :scm-type  determine scm type, eg. :git
   - :scm a sequence strings specifying scm locations.
   - :description \"a descriptive string\"
   - :branches [\"branch1\" \"branch2\"]
   - :properties specifies plugin properties, map from plugin keyword to a map
                 of tag values. Use :authorization-matrix to specify a sequence
                 of maps with :user and :permissions keys.
   - :publishers specifies a map of publisher specfic options
   Other options are SCM specific.

   git:
   - :name
   - :refspec
   - :receivepack
   - :uploadpack
   - :tagopt

   Example
       (job
         :branches [\"origin/master\"]
         :scm [\"http://project.org/project.git\"]
         :num-to-keep 10
         :browser {:class \"hudson.scm.browsers.FishEyeSVN\"
                   :url \"http://fisheye/browse/\"}
         :concurrent-build true
         :ignore-upstream-changes true
         :properties {:disk-usage {}
                      :authorization-matrix
                        [{:user \"anonymous\"
                          :permissions #{:item-read :item-build}}]}
         :publishers {:artifact-archiver
                       {:artifacts \"**/*.war\" :latestOnly false}})"
  [session build-type job-name & {:keys [refspec receivepack uploadpack
                                         tagopt description branches scm
                                         scm-type merge-target]
                                  :as options}]
  (let [jenkins-owner (parameter/get-for-target session [:jenkins :owner])
        jenkins-user (parameter/get-for-target session [:jenkins :user])
        jenkins-group (parameter/get-for-target session [:jenkins :group])
        jenkins-data-path (parameter/get-for-target
                          session [:jenkins :data-path])
        scm (normalise-scms (:scm options))]
    (logging/trace (str "Jenkins - configure job " job-name))
    (->
     session
     (directory/directory
      (str jenkins-data-path "/jobs/" job-name) :p true
      :owner jenkins-user :group jenkins-group :mode  "0775")
     (remote-file/remote-file
      (str jenkins-data-path "/jobs/" job-name "/config.xml")
      :content
      (output-build-for
       build-type
       session
       (:scm-type options)
       scm
       (dissoc options :scm :scm-type))
      :owner jenkins-owner :group jenkins-group :mode "0664")
     (thread-expr/when->
      subversion-credentials
      (remote-file/remote-file
      (str jenkins-data-path "/jobs/" job-name "/subversion.credentials")
      :content
      (credential-store (zipmap
                         (map #(str "<" (url-without-path (ffirst scm)) ">" %)
                              (keys subversion-credentials))
                         (vals subversion-credentials)))
      :owner jenkins-owner :group jenkins-group :mode "0664")))))


(enlive/deffragment jenkins-task-transform
  [name version]
  [:name]
  (xml/content name)
  [:id]
  (xml/content version))


(defn- jenkins-tool-path
  [jenkins-data-path name]
  (str jenkins-data-path "/tools/" (string/replace name #" " "_")))
