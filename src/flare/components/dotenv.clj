(ns flare.components.dotenv
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(defn from-file []
  (when (.exists (io/file ".env"))
    (with-open [reader (io/reader ".env")]
      (->> (line-seq reader)
           (map #(clojure.string/split % #"=" 2))
           (filter #(= 2 (count %)))
           (map (fn [[k v]]
                  [(clojure.string/trim k)
                   (clojure.string/trim (clojure.string/replace v #"^\"|\"$" ""))]))
           (into {})))))

(defn fetch
  ([key] (fetch key nil))
  ([key default]
   (let [system-env (into {} (System/getenv))
         env-vars   (merge system-env (from-file))]
     (get env-vars key default))))

(defn fetch-non-blank
  [key]
  (let [value (fetch key)]
    (when-not (str/blank? value)
      value)))
