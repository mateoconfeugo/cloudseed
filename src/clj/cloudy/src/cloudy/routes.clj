(ns cloudy.routes
  (:require [compojure.core :as c-core :refer [defroutes]]
            [compojure.route :as c-route :refer [resources not-found]]
            [shoreleave.middleware.rpc :refer [remote-ns]]
            [cloudy.controllers.site :refer [site-routes]]
            [cloudy.controllers.api]))

(remote-ns 'cloudy.controllers.api :as "api")

(defroutes app-routes
;;  (c-route/resources "/")
  (c-route/resources "/design/" {:root "templates"})
  (c-route/resources "/design/css" {:root "public/css"})
  (c-route/resources "/css/" {:root "public/css"})
  (c-route/resources "/js/" {:root "public/js"})
  (c-route/not-found "404 Page not found."))

(def all-routes (c-core/routes site-routes app-routes))
