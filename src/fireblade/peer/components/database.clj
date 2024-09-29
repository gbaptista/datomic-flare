(ns fireblade.peer.components.database
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

(defn set-name [uri new-db-name]
  (let [prefix "datomic:sql://"
        [datomic-part jdbc-part] (clojure.string/split uri #"\?jdbc:" 2)
        _ (assert (clojure.string/starts-with? datomic-part prefix) "Invalid Datomic URL format")
        new-datomic-part (str prefix new-db-name)]
    (if jdbc-part
      (str new-datomic-part "?jdbc:" jdbc-part)
      new-datomic-part)))
