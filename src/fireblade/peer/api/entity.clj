(ns fireblade.peer.api.entity
  (:require
   [datomic.api :as d]
   [fireblade.peer.components.database :as components.database]
   [fireblade.peer.components.inputs :as components.inputs]))

(defn entity [system request-body]
  (let [{:keys [database id]} request-body
        datomic-system (:datomic system)
        reference-database
        (components.inputs/retrieve-database datomic-system database)
        entity (d/entity (:value reference-database) id)]
    {:meta {:database (components.database/build-meta reference-database)}
     :data entity}))
