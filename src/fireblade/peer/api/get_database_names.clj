(ns fireblade.peer.api.get-database-names
  (:require
   [datomic.api :as d]
   [clojure.string :as str]))

(defn- clear-uri [uri]
  (let [[base query] (str/split uri #"\?" 2)
        base (str/replace base #"/[^/]+$" "/*")]
    (if query
      (str base "?" query)
      base)))

(defn get-database-names [system _]
  (let [uri          (:uri (:datomic system))
        clean-uri    (clear-uri uri)]
    {:data (d/get-database-names clean-uri)}))
