(ns fireblade.peer.api.list-databases)

(defn list-databases [_ _]
  {:status 500
   :data
   {:error
    "The list-databases operation is not supported on Embedded Peers. Please try using get-database-names instead."}})
