(ns fireblade.client.api.q
  (:require
   [datomic.client.api :as d]
   [fireblade.client.components.database :as components.database]
   [fireblade.client.components.inputs :as components.inputs]))

(defn q [system request-body]
  (let [{:keys [query inputs]} request-body
        datomic-system (:datomic system)
        {:keys [reference-database inputs-list]}
        (components.inputs/retrieve datomic-system inputs)]
    {:meta {:database (components.database/build-meta reference-database)}
     :data (apply d/q (read-string query) inputs-list)}))
