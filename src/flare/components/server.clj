(ns flare.components.server
  (:require
   [flare.components.dotenv :as dotenv]
   [clojure.tools.logging :as log]
   [org.httpkit.server :as http-kit]
   [flare.ports.http :as ports.http]
   [fireblade.api :as fireblade-api]
   [flare.adapters.to-json]))

(defn start-http-server []
  (let [port   (Integer/parseInt (dotenv/fetch "FLARE_PORT" 3042))
        bind   (dotenv/fetch "FLARE_BIND" "0.0.0.0")
        mode   (dotenv/fetch "FLARE_MODE")
        system (fireblade-api/create-system)]
    (log/info (str "Starting server on http://" bind ":" port " as " mode))
    (http-kit/run-server
     (ports.http/create system)
     {:port port :host bind})))

(defn -main [& _]
  (start-http-server))
