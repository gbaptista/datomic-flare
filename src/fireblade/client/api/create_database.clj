(ns fireblade.client.api.create-database)

(defn create-database [_ _]
  {:status 500
   :data
   {:error
    "The create-database operation is not supported on Peer Servers"}})
