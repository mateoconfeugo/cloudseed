(ns cloudseed.controllers.api
  "API controller for cloudseed client"
  (:require [cloudseed.models.shopping :as shop :refer [add-to-cart]]))

(defn get-item [item-id])

(defn list-items
  [cart-id])

(defn add-item
  [product cart-id])

(defn remove-item
  [product])

(defn edit-quantity [])

(defn get-store-categories [])

(defn get-items-in-category [category-id])

(defn get-catelog [])
