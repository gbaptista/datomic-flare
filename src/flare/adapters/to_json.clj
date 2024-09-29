(ns flare.adapters.to-json
  (:require
   [clojure.string :as str]
   [cheshire.generate :refer [add-encoder]])
  (:import
   [datomic.db Db Datum]
   [datomic.query EntityMap]
   [com.fasterxml.jackson.core JsonGenerator]
   [java.util Date]
   [java.time Instant LocalDateTime ZonedDateTime OffsetDateTime]
   [java.time.format DateTimeFormatter]))

(defn format-keyword [value]
  (str value))

(defn format-clojure-keyword [value]
  (str value))

(defn format-field-name [value]
  (cond
    (keyword? value) (format-keyword value)
    (instance? clojure.lang.Keyword value) (format-clojure-keyword value)
    :else (str value)))

(defn format-datetime [value]
  (cond
    ;; java.time.Instant (ISO 8601, precise to nanoseconds)
    (instance? Instant value) (.toString value)

    ;; java.time.LocalDateTime (ISO 8601, without time zone)
    (instance? LocalDateTime value) (.format value DateTimeFormatter/ISO_LOCAL_DATE_TIME)

    ;; java.time.ZonedDateTime (ISO 8601, with time zone)
    (instance? ZonedDateTime value) (.format value DateTimeFormatter/ISO_ZONED_DATE_TIME)

    ;; java.time.OffsetDateTime (ISO 8601, with time offset)
    (instance? OffsetDateTime value) (.format value DateTimeFormatter/ISO_OFFSET_DATE_TIME)

    ;; java.util.Date (fallback to Instant for ISO 8601)
    (instance? Date value) (.toString (.toInstant value))

    :else nil))

(defn write-number-value [^JsonGenerator json-generator value]
  (cond
    (instance? java.math.BigDecimal value) (.writeNumber json-generator (.toString ^java.math.BigDecimal value))
    (instance? java.math.BigInteger value) (.writeNumber json-generator (.toString ^java.math.BigInteger value))
    (instance? Float value) (.writeNumber json-generator value)
    (instance? Long value) (.writeNumber json-generator (long value))
    (instance? Double value) (.writeNumber json-generator (double value))
    (instance? Integer value) (.writeNumber json-generator (int value))))

(defn write-json-value [^JsonGenerator json-generator value]
  (cond
    (nil? value) (.writeNull json-generator)

    (string? value) (.writeString json-generator value)

    (boolean? value) (.writeBoolean json-generator value)

    (keyword? value) (.writeString json-generator (format-keyword value))

    (instance? clojure.lang.Keyword value)
    (.writeString json-generator (format-clojure-keyword value))

    (map? value) (do
                   (.writeStartObject json-generator)
                   (doseq [[k v] value]
                     (.writeFieldName json-generator (format-field-name k))
                     (write-json-value json-generator v))
                   (.writeEndObject json-generator))

    (coll? value) (do
                    (.writeStartArray json-generator)
                    (doseq [item value]
                      (write-json-value json-generator item))
                    (.writeEndArray json-generator))

    (number? value) (write-number-value json-generator value)

    :else
    (if-let [formatted-date (format-datetime value)]
      (.writeString json-generator formatted-date)
      (.writeString json-generator (str value)))))

(defn extract-class-name [obj]
  (-> obj class str (str/replace "class " "")))

(defn database-value->string [database]
  (str
   (extract-class-name database)
   "@"
   (Integer/toHexString (System/identityHashCode database))))

(defn encode-db [^JsonGenerator json-generator obj]
  (.writeString json-generator (database-value->string obj)))

(defn encode-datom-core [^JsonGenerator json-generator e a v tx added]
  (.writeStartArray json-generator)

  (write-number-value json-generator e)

  (write-json-value json-generator a)
  (write-json-value json-generator v)

  (write-number-value json-generator tx)

  (.writeBoolean json-generator added)
  (.writeEndArray json-generator))

(defn encode-client-datom [^JsonGenerator json-generator datom]
  (encode-datom-core json-generator (.e datom) (.a datom) (.v datom) (.tx datom) (.added datom)))

(add-encoder Datum
             (fn [^Datum datom ^JsonGenerator json-generator]
               (encode-datom-core json-generator (.e datom) (.a datom) (.v datom) (.tx datom) (.added datom))))

(add-encoder Db
             (fn [^Db db ^JsonGenerator json-generator]
               (encode-db json-generator db)))

(add-encoder Object
             (fn [obj ^JsonGenerator json-generator]
               (let [class-name (extract-class-name obj)]
                 (cond
                   (or (= class-name "datomic.client.impl.shared.Db")
                       (= class-name "datomic.db.Db"))
                   (encode-db json-generator obj)

                   (= class-name "datomic.client.impl.shared.datom.Datom")
                   (encode-client-datom json-generator obj)

                   :else (.writeString json-generator (str obj))))))

(defn recursive-entity-map->map [entity]
  (into {}
        (map (fn [[k v]]
               (if (instance? EntityMap v)
                 [k (assoc (recursive-entity-map->map v) :db/id (get v :db/id))]
                 [k v])))
        entity))

(defn write-json-entity [^JsonGenerator json-generator entity]
  (let [expanded-entity
        (assoc
         (recursive-entity-map->map entity)
         :db/id (get entity :db/id))]
    (.writeStartObject json-generator)
    (doseq [[k v] expanded-entity]
      (.writeFieldName json-generator (format-field-name k))
      (write-json-value json-generator v))
    (.writeEndObject json-generator)))

(add-encoder EntityMap
             (fn [^EntityMap entity ^JsonGenerator json-generator]
               (write-json-entity json-generator entity)))
