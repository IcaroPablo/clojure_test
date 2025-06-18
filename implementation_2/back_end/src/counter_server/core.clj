(ns counter-server.core
  (:require [org.httpkit.server :as http]
            [cheshire.core :as json]
            [ring.middleware.cors :refer [wrap-cors]]))

(def counter (atom 0))

(defn json-response [data]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (json/generate-string data)})

(defn handler [{:keys [uri request-method]}]
  (case [request-method uri]
    [:get "/count"]   (json-response {:counter @counter})
    [:patch "/inc"]   (json-response {:counter (swap! counter inc)})
    [:patch "/reset"] (json-response {:counter (reset! counter 0)})
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


; (defn -main []
;   (http/run-server handler {:port 8080}))
