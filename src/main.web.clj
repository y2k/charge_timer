(defn main []
  (set! (.-update_ui window)
        (fn [query text]
          (set! (.-innerHTML (.querySelector document query)) text))))
