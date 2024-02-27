(def ALARM_PERCENT 90)

(defn html []
  [:html {:lang "ru" :data-theme "dark"}
   [:head
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
    [:link {:rel "stylesheet" :href "css/pico.classless.css"}]]
   [:body {:style "user-select: none;" :onload ""}
    [:main
     [:h3 {:id "text_charge"} "..."]
     [:h3 {:id "text_job_status"} "..."]
     [:button {:id "btn_get_info"} "Get info"]
     [:button {:id "btn_start"} "Start"]
     [:button {:id "btn_stop"} "Stop"]]
    [:script {:type :module} "import { main } from './js/main.js'; main()"]]])

(defn main []
  (set! (.-charge_changed window)
        (fn [message]
          (if-let [battery_percent (.-level (JSON/parse message))]
            (do
              (set!
               (.-innerHTML (.querySelector document "#text_charge"))
               (str "Battery: " battery_percent " / " ALARM_PERCENT)))
            null)))

;; ===============================================

  (.addEventListener
   (.querySelector document "#btn_get_info") "click"
   (fn []
     (.get_job_info Android :update_status)))

  (set! (.-update_status window)
        (fn [message]
          (set!
           (.-innerHTML (.querySelector document "#text_job_status"))
           message)))

;; ===============================================

  (.addEventListener
   (.querySelector document "#btn_start") "click"
   (fn []
     (.start_job Android)
     (set! (.-innerHTML (.querySelector document "#text_job_status")) "Started")))

  (.addEventListener
   (.querySelector document "#btn_stop") "click"
   (fn []
     (.stop_job Android)))

  (.register_broadcast Android :charge_changed "android.intent.action.BATTERY_CHANGED"))
