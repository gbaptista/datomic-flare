(ns fireblade.client.api.entity)

(defn entity [_ _]
  {:status 500
   :data
   {:error
    "The entity operation is not supported on Peer Servers"}})
