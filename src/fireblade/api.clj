(ns fireblade.api
  (:require
   [flare.components.dotenv :as dotenv]

   [fireblade.wrapper :as wrapper]

   [fireblade.peer]
   [fireblade.peer.api.create-database]
   [fireblade.peer.api.delete-database]
   [fireblade.peer.api.get-database-names]
   [fireblade.peer.api.list-databases]
   [fireblade.peer.api.db-stats]
   [fireblade.peer.api.transact]
   [fireblade.peer.api.datoms]
   [fireblade.peer.api.q]
   [fireblade.peer.api.entity]

   [fireblade.client]
   [fireblade.client.api.create-database]
   [fireblade.client.api.delete-database]
   [fireblade.client.api.get-database-names]
   [fireblade.client.api.list-databases]
   [fireblade.client.api.db-stats]
   [fireblade.client.api.transact]
   [fireblade.client.api.datoms]
   [fireblade.client.api.q]
   [fireblade.client.api.entity]))

(defn create-datomic-component []
  (if (= (dotenv/fetch "FLARE_MODE") "peer")
    (fireblade.peer/create-datomic-component)
    (fireblade.client/create-datomic-component)))

(defn create-fireblade-component []
  (if (= (dotenv/fetch "FLARE_MODE") "peer")
    {:create-database    (wrapper/wrap fireblade.peer.api.create-database/create-database)
     :delete-database    (wrapper/wrap fireblade.peer.api.delete-database/delete-database)
     :get-database-names (wrapper/wrap fireblade.peer.api.get-database-names/get-database-names)
     :list-databases     (wrapper/wrap fireblade.peer.api.list-databases/list-databases)
     :db-stats           (wrapper/wrap fireblade.peer.api.db-stats/db-stats)
     :transact           (wrapper/wrap fireblade.peer.api.transact/transact)
     :datoms             (wrapper/wrap fireblade.peer.api.datoms/datoms)
     :q                  (wrapper/wrap fireblade.peer.api.q/q)
     :entity             (wrapper/wrap fireblade.peer.api.entity/entity)}
    {:create-database    (wrapper/wrap fireblade.client.api.create-database/create-database)
     :delete-database    (wrapper/wrap fireblade.client.api.delete-database/delete-database)
     :get-database-names (wrapper/wrap fireblade.client.api.get-database-names/get-database-names)
     :list-databases     (wrapper/wrap fireblade.client.api.list-databases/list-databases)
     :db-stats           (wrapper/wrap fireblade.client.api.db-stats/db-stats)
     :transact           (wrapper/wrap fireblade.client.api.transact/transact)
     :datoms             (wrapper/wrap fireblade.client.api.datoms/datoms)
     :q                  (wrapper/wrap fireblade.client.api.q/q)
     :entity             (wrapper/wrap fireblade.client.api.entity/entity)}))

(defn create-system []
  {:datomic   (create-datomic-component)
   :fireblade (create-fireblade-component)})
