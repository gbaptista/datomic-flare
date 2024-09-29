(ns flare.components.router
  (:require
   [reitit.ring :as ring]
   [reitit.ring.middleware.parameters :as parameters]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [muuntaja.core :as m]
   [flare.components.logger :refer [wrap-http-logging]]))

(defn wrap-system [handler system]
  (fn [request]
    (handler (assoc request :system system))))

(defn middlewares [system]
  [wrap-http-logging
   parameters/parameters-middleware
   muuntaja/format-middleware
   (fn [handler] (wrap-system handler system))])

(defn create [system routes]
  (ring/ring-handler
   (ring/router
    routes
    {:data {:muuntaja m/instance
            :middleware (middlewares system)}})))
