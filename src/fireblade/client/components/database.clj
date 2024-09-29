(ns fireblade.client.components.database
  (:require
   [clojure.string :as str]))

(defn generate-class-name [obj]
  (-> obj class str (str/replace "class " "")))

(defn database-value->string [database]
  (str
   (generate-class-name database)
   "@"
   (Integer/toHexString (System/identityHashCode database))))

(defn build-meta [database]
  (let [database-value (:value database)]
    (merge
     database
     {:value (database-value->string database-value)})))
