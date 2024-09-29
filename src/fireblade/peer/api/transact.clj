(ns fireblade.peer.api.transact
  (:require
   [datomic.api :as d]
   [fireblade.peer.components.database :as components.database]
   [fireblade.peer.components.inputs :as components.inputs]))

(defn transact [system request-body]
  (let [{:keys [data connection]} request-body
        database (:database connection)
        datomic-system (:datomic system)
        database-connection
        (components.inputs/retrieve-connection datomic-system database)
        reference-database
        (components.inputs/retrieve-database
         datomic-system
         (assoc (select-keys (:database connection) [:name]) :latest true))]
    {:meta {:database (components.database/build-meta reference-database)}
     :data @(d/transact database-connection (read-string data))}))
