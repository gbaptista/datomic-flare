(ns fireblade.peer.api.delete-database
  (:require
   [datomic.api :as d]
   [fireblade.peer.components.database :as components.database]))

(defn delete-database [system request-body]
  (let [{:keys [name]} request-body
        uri (:uri (:datomic system))]
    {:data (d/delete-database (components.database/set-name uri name))}))
