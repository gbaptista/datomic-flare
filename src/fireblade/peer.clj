(ns fireblade.peer
  (:require
   [datomic.api :as d]
   [flare.components.dotenv :as dotenv]))

(defn create-datomic-component []
  (let [uri        (dotenv/fetch-non-blank "FLARE_PEER_CONNECTION_URI")
        connection (d/connect uri)]
    {:mode "peer"
     :uri uri
     :connection connection}))
