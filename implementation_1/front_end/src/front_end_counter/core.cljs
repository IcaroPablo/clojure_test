(ns front_end_counter.core)

(def counter (atom 0))

(defn update-counter-display [element]
  (set! (.-innerText element) (str "Count: " @counter)))

(defn ^:export init []
  (let [body (.-body js/document)
        counter-display (.createElement js/document "div")
        _ (update-counter-display counter-display)

        button (.createElement js/document "button")
        _ (set! (.-innerText button) "Increment")

        resetButton (.createElement js/document "button")
        _ (set! (.-innerText resetButton) "Reset")

        _ (set! (.-onclick button)
                (fn []
                  (swap! counter inc)
                  (update-counter-display counter-display)))

        _ (set! (.-onclick resetButton)
                (fn []
                  (reset! counter 0)
                  (update-counter-display counter-display)))]

    (.appendChild body counter-display)
    (.appendChild body button)
    (.appendChild body resetButton)))
