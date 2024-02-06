(def ALARM_PERCENT 90)
(def percent_ref (Array/of 0))

(defn- charge_changed [message]
  (if-let [battery_percent (.-level (JSON/parse message))]
    (do
      (set!
       (.-innerHTML (.querySelector document "#text1"))
       (str "Battery: " battery_percent " / " ALARM_PERCENT))

      (if-let [_ (< (first percent_ref) ALARM_PERCENT)]
        (do
          (.pop percent_ref)
          (.push percent_ref battery_percent)

          (if (>= battery_percent ALARM_PERCENT)
            (.play_alarm Android)
            null))
        null))
    null))

(.addEventListener
 (.querySelector document "#btn_settings") "click"
 (fn []
   (.open_settings Android)))

(.addEventListener
 (.querySelector document "#btn_test_sound") "click"
 (fn []
   (set! (.-innerHTML (.querySelector document "#text2")) "BEFORE")
   (.play_alarm Android)
   (set! (.-innerHTML (.querySelector document "#text2")) "AFTER")))

(.addEventListener
 (.querySelector document "#btn_inc") "click"
 (let [count_ref (Array/of 0)]
   (fn []
     (.push count_ref (+ 1 (.pop count_ref)))
     (set! (.-innerHTML (.querySelector document "#text2")) (get count_ref 0)))))

(.register_broadcast Android :charge_changed "android.intent.action.BATTERY_CHANGED")
