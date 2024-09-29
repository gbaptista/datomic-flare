(ns flare.components.chronos)

(defn time-it [f & args]
  (let [start        (System/nanoTime)
        result       (apply f args)
        end          (System/nanoTime)
        milliseconds (/ (- end start) 1e6)]
    {:result result :milliseconds milliseconds}))
