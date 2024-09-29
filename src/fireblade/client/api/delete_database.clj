(ns fireblade.client.api.delete-database)

(defn delete-database [_ _]
  {:status 500
   :data
   {:error
    "The delete-database operation is not supported on Peer Servers"}})
