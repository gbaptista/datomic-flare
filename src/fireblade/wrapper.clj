(ns fireblade.wrapper
  (:import [java.time Instant]))

(defn- time-it [f]
  (let [start (System/nanoTime)
        result (f)
        end (System/nanoTime)
        milliseconds (/ (- end start) 1e6)]
    {:result result
     :milliseconds milliseconds}))

(defn wrap [handler]
  (fn [system body]
    (let [at          (.toString (Instant/now))
          mode        (:mode (:datomic system))
          timed       (time-it (fn [] (handler system body)))
          {:keys [result milliseconds]} timed
          status      (or (:status result) 200)
          extra-meta  (:meta result)
          meta-data   (merge {:at at
                              :mode mode
                              :took {:milliseconds milliseconds}}
                             extra-meta)
          response    {:meta meta-data
                       :data (:data result)}]
      {:status status :body response})))
