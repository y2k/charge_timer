(def count_ref (Array/of 0))

(->
 (.querySelector document "#btn")
 (.addEventListener "click"
                    (fn []
                      (.push count_ref (+ 1 (.pop count_ref)))
                      (set!
                       (.-innerHTML (.querySelector document "#text2"))
                       (.at count_ref 0))
                      (println "FIXME(JS): clicked"))))

(defn- globalDispatch [key message]
  (println "FIXME(JS): " key message)
  (set!
   (.-innerHTML (.querySelector document "#text1"))
   (str "Charge: " (.-level (JSON/parse message)) "%")))

(.registerBroadcast Android "charge_changed" "android.intent.action.BATTERY_CHANGED")
