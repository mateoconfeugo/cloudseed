(ns cloudseed.views.host-dom
  (:require [clojure.string :as string :refer [join]]
            [net.cgrand.enlive-html :refer [deftemplate defsnippet content sniptest emit*]]))

(defsnippet top-banner "templates/top_banner.html" [:div#top-banner :div.row]
  [{:keys[] :as model}])

(defsnippet marketing-message "templates/market_message.html" [:div#market-message] [{:keys[] :as model}])

(defsnippet lead-modal "templates/lead_modal.html" [:div.modal-dialog]
  [{:keys[] :as model}])

(defsnippet top-nav-header "templates/header.html" [:header#header] [{:keys[] :as model}])
(defsnippet bottom-footer "templates/footer.html" [:footer#footer] [{:keys[] :as model}])
(defsnippet copyright "templates/copyright.html" [:div#copyright] [{:keys[] :as model}])

(deftemplate current-release "templates/release.html"
  []
  [:div#top-banner] (content (top-banner {}))
  [:div.container.marketing] (content (marketing-message {}))
  [:div#leadModal] (content (lead-modal {}))
  [:footer] (content (bottom-footer {})))

(deftemplate home "templates/home.html" []
  [:div#header-wrapper :header] (content (top-nav-header {}))
  [:div#footer-wrapper :footer] (content (bottom-footer {}))
  [:div#copyright] (content (copyright {})))

(deftemplate about "templates/about.html" []
  [:div#header-wrapper] (content (top-nav-header {}))
  [:div#footer-wrapper] (content (bottom-footer {}))
  [:div#copyright] (content (copyright {})))

(deftemplate myfarm "templates/myfarm.html" []
  [:div#header-wrapper] (content (top-nav-header {}))
  [:div#footer-wrapper] (content (bottom-footer {}))
  [:div#copyright] (content (copyright {})))

(deftemplate mygarden "templates/mygarden.html" []
  [:div#header-wrapper] (content (top-nav-header {}))
  [:div#footer-wrapper] (content (bottom-footer {}))
  [:div#copyright] (content (copyright {})))

(deftemplate growingtogether "templates/growingtogether.html" []
  [:div#header-wrapper] (content (top-nav-header {}))
  [:div#footer-wrapper] (content (bottom-footer {}))
  [:div#copyright] (content (copyright {})))

(deftemplate estore "templates/estore.html" []
  [:div#header-wrapper] (content (top-nav-header {}))
  [:div#footer-wrapper] (content (bottom-footer {}))
  [:div#copyright]  (content (copyright {})))

(deftemplate thank-you "templates/thank_you.html"
  [name]
  [:p#persons-name] (content name))
