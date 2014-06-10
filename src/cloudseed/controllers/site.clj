(ns cloudseed.controllers.site
  "Web specific controllers routes, ROA oriented"
  (:require [compojure.core :refer [defroutes GET]]
            [cloudseed.views.host-dom :as host-dom]))

(defroutes site-routes
  (GET "/home" [] (host-dom/home))
  (GET "/about" [] (host-dom/about))
  (GET "/myfarm" [] (host-dom/myfarm))
  (GET "/growingtogether" [] (host-dom/growingtogether))
  (GET "/mygarden" [] (host-dom/mygarden))
  (GET "/estore" [] (host-dom/estore))
  (GET "/thank-you" [name] (host-dom/thank-you name))
  (GET "/" [] (host-dom/home)))
