(ns fireblade.client.components.inputs
  (:require
   [datomic.client.api :as d]))

(defn retrieve-connection [datomic-system options]
  (if (contains? options :name)
    (let [client         (:client datomic-system)
          default-name   (:database-name datomic-system)
          requested-name (:name options)]
      (if (= requested-name default-name)
        (:connection datomic-system)
        (d/connect client {:db-name requested-name})))
    (:connection datomic-system)))

(defn retrieve-name [datomic-system options]
  (if (contains? options :name)
    (:name options)
    (:database-name datomic-system)))

(defn validate-database-options! [options]
  (let [conflicting-options [:latest :as-of :t]
        provided-options (filter #(contains? options %) conflicting-options)]

    (when (> (count provided-options) 1)
      (throw (ex-info "Conflicting options: Only one of :latest, :as-of, or :t can be provided."
                      {:conflicting-options provided-options})))

    (when (not (some #{:latest :as-of :t} (keys options)))
      (throw (ex-info "Missing: :latest, :as-of, or :t." {:options options})))))

(defn retrieve-database [datomic-system options]
  (let [connection (retrieve-connection datomic-system options)]
    (validate-database-options! options)
    (let [base-db (-> (cond
                        (:as-of options) (d/as-of (d/db connection) (:as-of options))
                        (:t options) (d/as-of (d/db connection) (:t options))
                        :else (d/db connection))
                      (cond-> (:since options) (d/since (:since options)))
                      (cond-> (:history options) d/history))]
      {:value base-db
       :name (retrieve-name datomic-system options)})))

(defn prepare [datomic-system inputs]
  (mapv (fn [input]
          (if (and (map? input) (:database input))
            {:database (retrieve-database datomic-system (:database input))}
            input))
        inputs))

(defn transform [raw-inputs]
  (mapv (fn [input]
          (if (and (map? input) (:database input))
            (:value (:database input))
            input))
        raw-inputs))

(defn reference-database [raw-inputs]
  (some (fn [input]
          (when (and (map? input) (:database input))
            (:database input)))
        raw-inputs))

(defn retrieve [datomic-system inputs]
  (let [raw-inputs         (prepare datomic-system inputs)
        reference-database (reference-database raw-inputs)
        transformed-inputs (transform raw-inputs)]
    {:reference-database reference-database
     :inputs-list        transformed-inputs}))
