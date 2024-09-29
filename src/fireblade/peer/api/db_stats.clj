(ns fireblade.peer.api.db-stats
  (:require
   [fireblade.peer.components.database :as components.database]
   [fireblade.peer.components.inputs :as components.inputs]
   [datomic.api :as d]))

(defn db-stats [system request-body]
  (let [{:keys [database]} request-body
        datomic-system (:datomic system)
        reference-database
        (components.inputs/retrieve-database datomic-system database)]
    {:meta {:database (components.database/build-meta reference-database)}
     :data (d/db-stats (:value reference-database))}))
