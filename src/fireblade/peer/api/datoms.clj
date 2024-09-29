(ns fireblade.peer.api.datoms
  (:require
   [fireblade.peer.components.database :as components.database]
   [fireblade.peer.components.inputs :as components.inputs]
   [clojure.string :as str]
   [datomic.api :as d]))

(defn datoms [system request-body]
  (let [{:keys [database index]} request-body
        index-name (if (and (string? index) (str/starts-with? index ":"))
                     (keyword (symbol (str/replace index ":" "")))
                     (keyword (symbol index)))
        datomic-system (:datomic system)
        reference-database
        (components.inputs/retrieve-database datomic-system database)]
    {:meta {:database (components.database/build-meta reference-database)}
     :data (vec (d/datoms (:value reference-database) index-name))}))
