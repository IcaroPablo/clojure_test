(ns datomic-counter.db
  (:require [datomic.client.api :as d]))

;; -------------------------------
;; Datomic Setup
;; -------------------------------

(def storage
  (or (System/getenv "DB_PATH") "/tmp/datomic_test"))

(def client
  (d/client {:server-type :dev-local
             :system "counter"
             :storage-dir storage}))

(def db-name "counter-db")

(try
  (d/create-database client {:db-name db-name})
  (catch Exception _))

(def conn (d/connect client {:db-name db-name}))

(def schema
  [{:db/ident :counter/value
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one}])

(d/transact conn {:tx-data schema})

(defn ensure-counter []
  (let [db (d/db conn)
        found (-> (d/q '[:find ?e :where [?e :counter/value _]] db)
                  ffirst)]
    (when-not found
      (d/transact conn {:tx-data [{:counter/value 0}]}))))

(ensure-counter)

;; ----------------------------------
;; Public API for DB operations
;; ----------------------------------

(defn get-counter []
  (or (-> (d/q '[:find ?v :where [?e :counter/value ?v]] (d/db conn))
          ffirst)
      0))

(defn set-counter [val]
  (let [e (-> (d/q '[:find ?e :where [?e :counter/value _]] (d/db conn))
              ffirst)]
    (d/transact conn {:tx-data [{:db/id e :counter/value val}]})))

(defn inc-counter []
  (let [new (inc (get-counter))]
    (set-counter new)
    new))

(defn reset-counter []
  (set-counter 0)
  0)
