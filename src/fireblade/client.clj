(ns fireblade.client
  (:require
   [flare.components.dotenv :as dotenv]
   [datomic.client.api :as d]))

(defn create-datomic-component []
  (let [client (d/client
                {:server-type :peer-server
                 :endpoint    (dotenv/fetch "FLARE_CLIENT_ENDPOINT")
                 :secret      (dotenv/fetch "FLARE_CLIENT_SECRET")
                 :access-key  (dotenv/fetch "FLARE_CLIENT_ACCESS_KEY")
                 :validate-hostnames false})
        connection (d/connect
                    client
                    {:db-name (dotenv/fetch "FLARE_CLIENT_DATABASE_NAME")})]
    {:mode "client"
     :database-name (dotenv/fetch "FLARE_CLIENT_DATABASE_NAME")
     :client client
     :connection connection}))
