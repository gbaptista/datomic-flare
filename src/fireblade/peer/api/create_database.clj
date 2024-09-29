(ns fireblade.peer.api.create-database
  (:require
   [datomic.api :as d]
   [fireblade.peer.components.database :as components.database]))

(defn create-database [system request-body]
  (let [{:keys [name]} request-body
        uri (:uri (:datomic system))]
    {:data (d/create-database (components.database/set-name uri name))}))
