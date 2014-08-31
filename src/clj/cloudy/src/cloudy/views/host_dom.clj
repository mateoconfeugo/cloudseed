(ns cloudy.views.host-dom
  (:require [clojure.string :as string :refer [join]]
            [net.cgrand.enlive-html :refer [deftemplate defsnippet content sniptest emit*]]))

(defsnippet top-nav-header "templates/html/header.html" [:header#header] [{:keys[] :as model}])

(defsnippet bottom-footer "templates/html/footer.html" [:footer#footer] [{:keys[] :as model}])

(defsnippet copyright "templates/html/copyright.html" [:div#copyright] [{:keys[] :as model}])

(deftemplate home "templates/html/home.html" []
  [:div#header-wrapper :header] (content (top-nav-header {}))
  [:div#footer-wrapper :footer] (content (bottom-footer {}))
  [:div#copyright] (content (copyright {})))
