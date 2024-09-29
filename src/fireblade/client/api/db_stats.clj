(ns fireblade.client.api.db-stats
  (:require
   [fireblade.client.components.database :as components.database]
   [fireblade.client.components.inputs :as components.inputs]
   [datomic.client.api :as d]))

(defn db-stats [system request-body]
  (let [{:keys [database]} request-body
        datomic-system (:datomic system)
        reference-database
        (components.inputs/retrieve-database datomic-system database)]
    {:meta {:database (components.database/build-meta reference-database)}
     :data (d/db-stats (:value reference-database))}))
