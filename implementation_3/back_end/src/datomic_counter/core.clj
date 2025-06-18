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
