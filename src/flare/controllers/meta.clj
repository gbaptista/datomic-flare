(ns flare.controllers.meta
  (:require [cheshire.core :as json]
            [flare.components.dotenv :as dotenv]
            [clojure.string]
            [clojure.java.io :as io])
  (:import [java.time Instant]))

(defn get-version []
  (let [version-file (slurp (io/reader ".version"))]
    (clojure.string/trim version-file)))

(defn handler [_]
  (let [at    (.toString (Instant/now))
        start (System/nanoTime)
        version (get-version)
        data  {:mode (dotenv/fetch "FLARE_MODE")
               :datomic-flare          version
               :org.clojure/clojure    (clojure-version)
               :com.datomic/peer       "1.0.7187"
               :com.datomic/client-pro "1.0.81"}
        end   (System/nanoTime)
        milliseconds (/ (- end start) 1e6)]
    {:status 200
     :body (json/encode
            {:meta {:at at
                    :mode (dotenv/fetch "FLARE_MODE")
                    :took {:milliseconds milliseconds}}
             :data data})}))
