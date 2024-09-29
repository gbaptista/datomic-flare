(ns fireblade.client.api.list-databases
  (:require
   [datomic.client.api :as d]))

(defn list-databases [system _]
  (let [client (:client (:datomic system))]
    {:data (d/list-databases client {})}))
