(ns fireblade.client.api.get-database-names)

(defn get-database-names [_ _]
  {:status 500
   :data
   {:error
    "The get-database-names operation is not supported on Peer Servers. Please try using list-databases instead."}})
