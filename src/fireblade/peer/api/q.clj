(ns fireblade.peer.api.q
  (:require
   [datomic.api :as d]
   [fireblade.peer.components.database :as components.database]
   [fireblade.peer.components.inputs :as components.inputs]))

(defn q [system request-body]
  (let [{:keys [query inputs]} request-body
        datomic-system (:datomic system)
        {:keys [reference-database inputs-list]}
        (components.inputs/retrieve datomic-system inputs)]
    {:meta {:database (components.database/build-meta reference-database)}
     :data (apply d/q (read-string query) inputs-list)}))
