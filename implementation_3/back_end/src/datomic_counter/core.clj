; (ns datomic-counter.core
;   (:require [org.httpkit.server :as http]
;             [datomic.client.api :as d]
;             [ring.middleware.cors :refer [wrap-cors]]
;             [cheshire.core :as json]))
;
; ;; -------------------------------
; ;; Datomic Setup
; ;; -------------------------------
;
; (def storage
;   (or (System/getProperty "db") :mem))
;
; (def client
;   (d/client {:server-type :dev-local
;              :system "counter"
;              :storage-dir storage}))
;
; (def db-name "counter-db")
;
; (try
;   (d/create-database client {:db-name db-name})
;   (catch Exception _))
;
; (def conn (d/connect client {:db-name db-name}))
;
; (def schema
;   [{:db/ident :counter/value
;     :db/valueType :db.type/long
;     :db/cardinality :db.cardinality/one}])
;
; (d/transact conn {:tx-data schema})
;
; (defn ensure-counter []
;   (let [db (d/db conn)
;         found (-> (d/q '[:find ?e :where [?e :counter/value _]] db)
;                   ffirst)]
;     (when-not found
;       (d/transact conn {:tx-data [{:counter/value 0}]}))))
;
; (ensure-counter)
;
; ;; ----------------------------------
; ;; Helper functions
; ;; ----------------------------------
;
; (defn get-counter []
;   (or (-> (d/q '[:find ?v :where [?e :counter/value ?v]] (d/db conn))
;           ffirst)
;       0))
;
; (defn set-counter [val]
;   (let [e (-> (d/q '[:find ?e :where [?e :counter/value _]] (d/db conn))
;               ffirst)]
;     (d/transact conn {:tx-data [{:db/id e :counter/value val}]})))
;
; (defn inc-counter []
;   (let [new (inc (get-counter))]
;     (set-counter new)
;     new))
;
; (defn reset-counter []
;   (set-counter 0)
;   0)
;
; ;; ----------------------------------
; ;; HTTP handler
; ;; ----------------------------------
;
; (defn json-response [data]
;   {:status 200
;    :headers {"Content-Type" "application/json"}
;    :body (json/generate-string data)})
;
; (defn handler [{:keys [uri request-method]}]
;   (case [request-method uri]
;     [:patch "/inc"]   (json-response {:counter (inc-counter)})
;     [:patch "/reset"] (json-response {:counter (reset-counter)})
;     [:get "/count"]   (json-response {:counter (get-counter)})
;     {:status 404
;      :headers {"Content-Type" "application/json"}
;      :body (json/generate-string {:error "Not found"})}))
;
; (def app
;   (wrap-cors handler
;              :access-control-allow-origin [#".*"]
;              :access-control-allow-methods [:get :patch :options]
;              :access-control-allow-headers ["Content-Type"]))
;
; (defn -main []
;   (http/run-server app {:port 8080}))

(ns datomic-counter.core
  (:require [org.httpkit.server :as http]
            [ring.middleware.cors :refer [wrap-cors]]
            [cheshire.core :as json]
            [datomic-counter.db :as db]))

;; ----------------------------------
;; HTTP handler
;; ----------------------------------

(defn json-response [data]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (json/generate-string data)})

(defn handler [{:keys [uri request-method]}]
  (case [request-method uri]
    [:patch "/inc"]   (json-response {:counter (db/inc-counter)})
    [:patch "/reset"] (json-response {:counter (db/reset-counter)})
    [:get "/count"]   (json-response {:counter (db/get-counter)})
    {:status 404
     :headers {"Content-Type" "application/json"}
     :body (json/generate-string {:error "Not found"})}))

(def app
  (wrap-cors handler
             :access-control-allow-origin [#".*"]
             :access-control-allow-methods [:get :patch :options]
             :access-control-allow-headers ["Content-Type"]))

(defn -main []
  (http/run-server app {:port 8080}))
