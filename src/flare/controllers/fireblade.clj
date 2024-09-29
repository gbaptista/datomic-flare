(ns flare.controllers.fireblade
  (:require [cheshire.core :as json]))

(defn handler-for [operation-key]
  (fn [{:keys [system body-params]}]
    (let [fireblade (:fireblade system)
          operation (get fireblade operation-key)
          response  (operation system body-params)]
      {:status (:status response)
       :body   (json/encode (:body response))})))

(def create-database    (handler-for :create-database))
(def delete-database    (handler-for :delete-database))

(def get-database-names (handler-for :get-database-names))
(def list-databases     (handler-for :list-databases))

(def db-stats           (handler-for :db-stats))

(def transact           (handler-for :transact))

(def datoms             (handler-for :datoms))
(def q                  (handler-for :q))
(def entity             (handler-for :entity))
