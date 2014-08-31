(ns fn-jenkins.jobs.security
    (:import
   org.apache.commons.codec.binary.Base64)
  (:use  [pallet.actions :as actions]
         [pallet.config-file.format :as format]
         [pallet.enlive :as enlive]
         [pallet.environment-impl]
         [pallet.thread-expr :as thread-expr]
         [pallet.utils :as utils]
         [fn-jenkins.jobs.cli])
         (:require
   [pallet.stevedore :as stevedore]
   [pallet.utils :as utils]
   [clojure.contrib.prxml :as prxml]
   [net.cgrand.enlive-html :as xml]
   [clojure.string :as string]))

(defn credential-entry
  "Produce an xml representation for a credential entry in a credential store"
  [[name {:keys [user-name password]}]]
  [:entry {}
   [:string {} name]
   [:jenkins.scm.SubversionSCM_-DescriptorImpl_-PasswordCredential {}
    [:userName {} user-name]
    [:password {} (Base64/encodeBase64String (.getBytes password))]]])

(defn credential-store
  "Output a credential store definition for a job configuration.
   Accepts a credential map from name to credentials. Credentials
   are a map containing :user-name and :password keys."
  [credential-map]
  (with-out-str
    (prxml/prxml
     [:decl! {:version "1.0"}]
     [:jenkins.scm.PerJobCredentialStore {}
      [:credentials {:class "hashtable"}
       (map credential-entry credential-map)]])))

(def security-realm-class
  {:hudson "hudson.security.HudsonPrivateSecurityRealm"})

(def authorization-strategy-class
  {:global-matrix "hudson.security.GlobalMatrixAuthorizationStrategy"})

(def permission-class
  {:computer-configure "hudson.model.Computer.Configure"
   :computer-delete "hudson.model.Computer.Delete"
   :hudson-administer "hudson.model.Hudson.Administer"
   :hudson-read "hudson.model.Hudson.Read"
   :item-build "hudson.model.Item.Build"
   :item-configure "hudson.model.Item.Configure"
   :item-create "hudson.model.Item.Create"
   :item-delete "hudson.model.Item.Delete"
   :item-read "hudson.model.Item.Read"
   :item-workspace "hudson.model.Item.Workspace"
   :run-delete "hudson.model.Run.Delete"
   :run-update "hudson.model.Run.Update"
   :scm-tag "hudson.scm.SCM.Tag"
   :view-configure "hudson.model.View.Configure"
   :view-create "hudson.model.View.Create"
   :view-delete "hudson.model.View.Delete"})

(def all-permissions
  [:computer-configure :computer-delete :hudson-administer :hudson-read
   :item-build :item-configure :item-create :item-delete :item-read
   :item-workspace :run-delete :run-update :scm-tag :view-configure
   :view-create :view-delete])
