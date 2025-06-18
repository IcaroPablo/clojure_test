(ns front_end_counter.core
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [cljs.core.async :refer-macros [go]]))

(defn update-counter-display [element value]
  (set! (.-innerText element) (str "Count: " value)))

(defn fetch-counter [element]
  (go
    (let [response (<! (http/get "http://localhost:8080/count"
                                 {:with-credentials? false}))]
      (if (= 200 (:status response))
        (let [counter (get-in response [:body :counter])]
          (update-counter-display element counter))
        (js/console.error "Failed to fetch counter" (:status response))))))

(defn increment-counter [element]
  (go
    (let [response (<! (http/patch "http://localhost:8080/inc"
                                   {:with-credentials? false}))]
      (if (= 200 (:status response))
        (let [counter (get-in response [:body :counter])]
          (update-counter-display element counter))
        (js/console.error "Failed to increment counter" (:status response))))))

(defn reset-counter [element]
  (go
    (let [response (<! (http/patch "http://localhost:8080/reset"
                                   {:with-credentials? false}))]
      (if (= 200 (:status response))
        (let [counter (get-in response [:body :counter])]
          (update-counter-display element counter))
        (js/console.error "Failed to reset counter" (:status response))))))

(defn ^:export init []
  (let [body (.-body js/document)
        counter-display (.createElement js/document "div")

        button (.createElement js/document "button")
        _ (set! (.-innerText button) "Increment")

        reset-button (.createElement js/document "button")
        _ (set! (.-innerText reset-button) "Reset")]

    (set! (.-onclick button) #(increment-counter counter-display))
    (set! (.-onclick reset-button) #(reset-counter counter-display))

    (.appendChild body counter-display)
    (.appendChild body button)
    (.appendChild body reset-button)

    (fetch-counter counter-display)))
