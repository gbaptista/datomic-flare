(ns fireblade.client.api.transact
  (:require
   [datomic.client.api :as d]
   [fireblade.client.components.database :as components.database]
   [fireblade.client.components.inputs :as components.inputs]))

(defn transact [system request-body]
  (let [{:keys [data connection]} request-body
        datomic-system (:datomic system)
        database-connection
        (components.inputs/retrieve-connection
         datomic-system (:database connection))
        reference-database
        (components.inputs/retrieve-database
         datomic-system
         (assoc (select-keys (:database connection) [:name]) :latest true))]
    {:meta {:database (components.database/build-meta reference-database)}
     :data (d/transact
            database-connection
            {:tx-data (read-string data)})}))
