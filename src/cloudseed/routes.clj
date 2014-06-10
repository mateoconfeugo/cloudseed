(ns cloudseed.routes
  (:require [compojure.core :as c-core :refer [defroutes]]
            [compojure.route :as c-route :refer [resources not-found]]
            [shoreleave.middleware.rpc :refer [remote-ns]]
            [cloudseed.controllers.site :refer [site-routes]]
            [cloudseed.controllers.api]))

(remote-ns 'cloudseed.controllers.api :as "api")

(defroutes app-routes
  (c-route/resources "/")
  (c-route/resources "/design/" {:root "templates"})
  (c-route/resources "/design/css/" {:root "public/css"})
  (c-route/not-found "404 Page not found."))

(def all-routes (c-core/routes site-routes app-routes))
