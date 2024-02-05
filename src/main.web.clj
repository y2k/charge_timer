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
            (.play_alarm Android 4)
            null))
        null))
    null))

(.addEventListener
 (.querySelector document "#btn3") "click"
 (fn []
   (.open_settings Android)))

(.addEventListener
 (.querySelector document "#btn2") "click"
 (fn []
   (set! (.-innerHTML (.querySelector document "#text2")) "BEFORE")
    ;;  (.play_alarm Android 4)
   (.play_alarm Android 2)
   (set! (.-innerHTML (.querySelector document "#text2")) "AFTER")))

(let [count_ref (Array/of 0)]
  (.addEventListener
   (.querySelector document "#btn") "click"
   (let [count_ref (Array/of 0)]
     (fn []
       (.push count_ref (+ 1 (.pop count_ref)))
       (set! (.-innerHTML (.querySelector document "#text2")) (.at count_ref 0))))))

  ;; (.registerBroadcast Android :charge_changed "android.intent.action.BATTERY_CHANGED")
