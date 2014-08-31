(ns cloudy.controllers.site
  "Web specific controllers routes, ROA oriented"
  (:require [compojure.core :refer [defroutes GET]]
            [cloudy.views.host-dom :as host-dom :refer [home]]))

(defroutes site-routes
  (GET "/" [] (host-dom/home))
  )
