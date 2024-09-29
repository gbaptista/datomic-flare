(ns flare.components.logger
  (:require
   [clojure.tools.logging :as log]
   [flare.components.chronos :refer [time-it]]))

(defn log-http-request-start [request]
  (let [method (.toUpperCase (name (:request-method request)))
        uri (:uri request)
        start-message (str method " " uri)]
    (log/info start-message)))

(defn log-http-request-complete [request status-code milliseconds]
  (let [method (.toUpperCase (name (:request-method request)))
        uri (:uri request)
        end-message (str method " " uri " [" status-code "] (" milliseconds " ms)")]
    (log/info end-message)))

(defn wrap-http-logging [handler]
  (fn [request]
    (log-http-request-start request)
    (let [{:keys [result milliseconds]} (time-it handler request)
          status-code (:status result)]
      (log-http-request-complete request status-code milliseconds)
      result)))
